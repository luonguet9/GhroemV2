package com.ghroem.presentation.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.media.app.NotificationCompat.MediaStyle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.ghroem.R
import com.ghroem.domain.model.Song
import com.ghroem.presentation.utils.enums.ActionMusic
import com.ghroem.presentation.receive.ActionReceive
import com.ghroem.presentation.ui.main.MainActivity
import com.ghroem.presentation.utils.ACTION_MUSIC
import com.ghroem.presentation.utils.ACTION_MUSIC_SERVICE
import com.ghroem.presentation.utils.GlideUtils
import com.ghroem.presentation.utils.SEND_ACTION_TO_ACTIVITY
import com.ghroem.presentation.utils.SONG_OBJECT
import com.ghroem.presentation.utils.NOTIFICATION_ID
import com.ghroem.presentation.utils.MEDIA_SESSION_COMPAT
import com.ghroem.presentation.utils.MUSIC_CHANNEL_ID
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : Service() {
	@Inject
	lateinit var exoPlayer: ExoPlayer
	
	private val binder = MusicBinder()
	
	var currentSong: Song? = null
	var songs: List<Song> = emptyList()
	
	var isPlaying = false
	
	inner class MusicBinder : Binder() {
		fun getService(): MusicService = this@MusicService
	}
	
	override fun onCreate() {
		super.onCreate()
		setupPlayerListener()
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
				sendNotification()
			} else if (!isPlaying) {
				resumeMusic()
				sendNotification()
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
	
	private fun setupPlayerListener() {
		exoPlayer.addListener(object : Player.Listener {
			override fun onPlaybackStateChanged(playbackState: Int) {
				super.onPlaybackStateChanged(playbackState)
				if (playbackState == Player.STATE_ENDED) {
					onCompletionListener()
				}
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
		val notificationIntent = Intent(this, MainActivity::class.java).apply {
			flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
		}
		val pendingIntent = PendingIntent.getActivity(
			this, 0,
			notificationIntent, PendingIntent.FLAG_IMMUTABLE
		)
		
		val mediaSession = MediaSessionCompat(this, MEDIA_SESSION_COMPAT)
		
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
		
		if (isPlaying) {
			builder.addAction(
				R.drawable.ic_previous,
				ActionMusic.PREVIOUS.actionName,
				getPendingIntent(ActionMusic.PREVIOUS.action)
			)
				.addAction(
					R.drawable.ic_pause,
					ActionMusic.PAUSE.actionName,
					getPendingIntent(ActionMusic.PAUSE.action)
				)
				.addAction(
					R.drawable.ic_next,
					ActionMusic.NEXT.actionName,
					getPendingIntent(ActionMusic.NEXT.action)
				)
		} else {
			builder.addAction(
				R.drawable.ic_previous,
				ActionMusic.PREVIOUS.actionName,
				getPendingIntent(ActionMusic.PREVIOUS.action)
			)
				.addAction(
					R.drawable.ic_play,
					ActionMusic.RESUME.actionName,
					getPendingIntent(ActionMusic.RESUME.action)
				)
				.addAction(
					R.drawable.ic_next,
					ActionMusic.NEXT.actionName,
					getPendingIntent(ActionMusic.NEXT.action)
				)
		}
		
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
		isPlaying = true
		sendActionToActivity(ActionMusic.START.action)
	}
	
	private fun pauseMusic() {
		if (isPlaying) {
			exoPlayer.pause()
			isPlaying = false
		}
		sendNotification()
		sendActionToActivity(ActionMusic.PAUSE.action)
	}
	
	private fun resumeMusic() {
		if (!isPlaying) {
			exoPlayer.play()
			isPlaying = true
		}
		sendNotification()
		sendActionToActivity(ActionMusic.RESUME.action)
	}
	
	private fun nextMusic() {
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
		sendNotification()
		sendActionToActivity(ActionMusic.NEXT.action)
	}
	
	private fun previousMusic() {
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
		sendNotification()
		sendActionToActivity(ActionMusic.PREVIOUS.action)
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
	
}