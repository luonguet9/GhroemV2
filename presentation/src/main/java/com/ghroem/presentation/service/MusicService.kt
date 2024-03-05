package com.ghroem.presentation.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.media.app.NotificationCompat.MediaStyle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.ghroem.R
import com.ghroem.data.data_source.local.pref.AppPreferences
import com.ghroem.domain.model.Song
import com.ghroem.presentation.receive.ActionReceive
import com.ghroem.presentation.ui.main.MainActivity
import com.ghroem.presentation.utils.ACTION_MUSIC
import com.ghroem.presentation.utils.ACTION_MUSIC_SERVICE
import com.ghroem.presentation.utils.GlideUtils
import com.ghroem.presentation.utils.MEDIA_SESSION_COMPAT
import com.ghroem.presentation.utils.MUSIC_CHANNEL_ID
import com.ghroem.presentation.utils.NOTIFICATION_ID
import com.ghroem.presentation.utils.SEND_ACTION_TO_ACTIVITY
import com.ghroem.presentation.utils.SONG_OBJECT
import com.ghroem.presentation.utils.enums.ActionMusic
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : Service() {
	@Inject
	lateinit var exoPlayer: ExoPlayer
	
	@Inject
	lateinit var appPreferences: AppPreferences
	
	private val mediaSession: MediaSessionCompat by lazy {
		MediaSessionCompat(this, MEDIA_SESSION_COMPAT)
	}
	
	private val binder = MusicBinder()
	
	var currentSong: Song? = null
	var songs = listOf<Song>()
	
	inner class MusicBinder : Binder() {
		fun getService(): MusicService = this@MusicService
	}
	
	override fun onCreate() {
		super.onCreate()
		exoPlayer.apply {
			repeatMode = appPreferences.repeatMode
			shuffleModeEnabled = appPreferences.shuffleMode
		}
		initListener()
	}
	
	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		val song = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			intent?.getSerializableExtra(SONG_OBJECT, Song::class.java)
		} else {
			@Suppress("DEPRECATION")
			intent?.getSerializableExtra(SONG_OBJECT) as? Song
		}
		song?.let {
			if (currentSong == null || currentSong != it) {
				currentSong = it
				startMusic(it)
			} else if (!exoPlayer.isPlaying) {
				resumeMusic()
			}
		}
		
		val actionReceive = intent?.getIntExtra(ACTION_MUSIC_SERVICE, -1)
		handleActionMusic(actionReceive)
		return START_NOT_STICKY
	}
	
	override fun onBind(intent: Intent?): IBinder {
		return binder
	}
	
	override fun onUnbind(intent: Intent?): Boolean {
		return super.onUnbind(intent)
	}
	
	override fun onDestroy() {
		super.onDestroy()
		exoPlayer.release()
		
		// Cancel Notification when close app
		val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
		notificationManager.cancel(NOTIFICATION_ID)
		
	}
	
	private fun initListener() {
		exoPlayer.addListener(object : Player.Listener {
			override fun onPlaybackStateChanged(playbackState: Int) {
				super.onPlaybackStateChanged(playbackState)
				if (playbackState == Player.STATE_ENDED) {
					onCompletionListener()
				}
			}
			
			override fun onIsPlayingChanged(isPlaying: Boolean) {
				Log.i(TAG, "onIsPlayingChanged: $isPlaying")
				sendNotification()
				super.onIsPlayingChanged(isPlaying)
				
			}
		})
	}
	
	fun onCompletionListener() {
		val currentIndex = currentSong?.let { song ->
			songs.indexOfFirst { it.id == song.id }
		} ?: -1
		
		when (exoPlayer.repeatMode) {
			Player.REPEAT_MODE_OFF -> {
				if (currentIndex == songs.lastIndex && !exoPlayer.shuffleModeEnabled) {
					pauseMusic()
				} else {
					nextMusic()
				}
			}
			
			Player.REPEAT_MODE_ONE -> {
				currentSong?.let {
					startMusic(it)
				}
			}
			
			Player.REPEAT_MODE_ALL -> {
				nextMusic()
			}
		}
	}
	
	private fun sendNotification() {
		Log.i(TAG, "sendNotification----------")
		val notificationIntent = Intent(this, MainActivity::class.java).apply {
			flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
		}
		val pendingIntent = PendingIntent.getActivity(
			this, 0,
			notificationIntent, PendingIntent.FLAG_IMMUTABLE
		)
		
		val builder = NotificationCompat.Builder(this, MUSIC_CHANNEL_ID)
			.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
			.setContentTitle(currentSong?.title)
			.setContentText(currentSong?.artist)
			.setSmallIcon(R.drawable.ic_music_note)
			.setContentIntent(pendingIntent)
			.setStyle(
				MediaStyle()
					.setShowActionsInCompactView(0, 1, 2)
					.setMediaSession(mediaSession.sessionToken)
			)
			.setOngoing(true)
			.setSound(null)
		
		builder.addAction(
			R.drawable.ic_previous,
			ActionMusic.PREVIOUS.actionName,
			getPendingIntent(ActionMusic.PREVIOUS.action)
		)
			.addAction(
				if (exoPlayer.isPlaying) R.drawable.ic_pause else R.drawable.ic_play,
				if (exoPlayer.isPlaying) ActionMusic.PAUSE.actionName else ActionMusic.RESUME.actionName,
				if (exoPlayer.isPlaying) getPendingIntent(ActionMusic.PAUSE.action)
				else getPendingIntent(ActionMusic.RESUME.action)
			)
			.addAction(
				R.drawable.ic_next,
				ActionMusic.NEXT.actionName,
				getPendingIntent(ActionMusic.NEXT.action)
			)
		
		GlideUtils.loadImageFromUrlWithCallback(
			context = applicationContext,
			url = currentSong?.albumArt,
			onFinished = {
				builder.setLargeIcon(it)
				startForeground(NOTIFICATION_ID, builder.build())
			})
	}
	
	private fun startMusic(song: Song) {
		val mediaItem = MediaItem.fromUri(song.data.toString())
		exoPlayer.setMediaItem(mediaItem)
		exoPlayer.prepare()
		exoPlayer.play()
		sendActionToActivity(ActionMusic.START.action)
	}
	
	private fun pauseMusic() {
		if (exoPlayer.isPlaying) {
			exoPlayer.pause()
		}
		sendActionToActivity(ActionMusic.PAUSE.action)
	}
	
	private fun resumeMusic() {
		if (!exoPlayer.isPlaying) {
			exoPlayer.play()
		}
		sendActionToActivity(ActionMusic.RESUME.action)
	}
	
	private fun nextMusic() {
		if (songs.isEmpty()) {
			return
		}
		
		val currentIndex = currentSong?.let { song ->
			songs.indexOfFirst { it.id == song.id }
		} ?: 0
		val nextIndex = when (exoPlayer.shuffleModeEnabled) {
			false -> (currentIndex + 1) % (songs.size)
			true -> {
				var randomIndex = (0 until (songs.size)).random()
				while (randomIndex == currentIndex) {
					randomIndex = (0 until (songs.size)).random()
				}
				randomIndex
			}
		}
		currentSong = songs[nextIndex]
		currentSong?.let {
			startMusic(it)
		}
		//sendActionToActivity(ActionMusic.NEXT.action)
	}
	
	private fun previousMusic() {
		if (songs.isEmpty()) {
			return
		}
		
		val currentIndex = currentSong?.let { song ->
			songs.indexOfFirst { it.id == song.id }
		} ?: -1
		val prevIndex = when (exoPlayer.shuffleModeEnabled) {
			false -> (currentIndex - 1 + songs.size) % songs.size
			true -> {
				var randomIndex = (0 until (songs.size)).random()
				while (randomIndex == currentIndex) {
					randomIndex = (0 until (songs.size)).random()
				}
				randomIndex
			}
		}
		currentSong = songs[prevIndex]
		currentSong?.let {
			startMusic(it)
		}
		//sendActionToActivity(ActionMusic.PREVIOUS.action)
	}
	
	private fun handleActionMusic(action: Int?) {
		when (action) {
			ActionMusic.PAUSE.action -> {
				pauseMusic()
			}
			
			ActionMusic.RESUME.action -> {
				resumeMusic()
			}
			
			ActionMusic.NEXT.action -> {
				nextMusic()
			}
			
			ActionMusic.PREVIOUS.action -> {
				previousMusic()
			}
		}
	}
	
	private fun getPendingIntent(action: Int): PendingIntent {
		val intent = Intent(this, ActionReceive::class.java).apply {
			putExtra(ACTION_MUSIC, action)
		}
		
		return PendingIntent.getBroadcast(
			applicationContext,
			action,
			intent,
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		)
	}
	
	private fun sendActionToActivity(action: Int) {
		val intent = Intent(SEND_ACTION_TO_ACTIVITY).apply {
			putExtra(ACTION_MUSIC, action)
		}
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
	}
	
	fun getCurrentSongIndex(): Int {
		return songs.indexOfFirst { it.id == currentSong?.id }
	}
	
	companion object {
		private const val TAG = "MusicService"
	}
}
