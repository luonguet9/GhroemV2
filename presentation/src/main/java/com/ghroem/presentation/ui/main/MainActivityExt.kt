package com.ghroem.presentation.ui.main

import android.content.Intent
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.Player.REPEAT_MODE_OFF
import androidx.media3.common.Player.REPEAT_MODE_ONE
import androidx.media3.common.Player.REPEAT_MODE_ALL
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.ghroem.R
import com.ghroem.presentation.adapter.viewholder.DetailSongViewHolder
import com.ghroem.presentation.service.MusicService
import com.ghroem.presentation.utils.ACTION_MUSIC_SERVICE
import com.ghroem.presentation.utils.GlideUtils
import com.ghroem.presentation.utils.enums.ActionMusic
import com.ghroem.presentation.utils.ext.setPaddingStatusBar
import com.ghroem.presentation.utils.getGradientBackground
import com.ghroem.presentation.utils.setOnSafeClickListener
import com.ghroem.presentation.utils.timeFormatter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**----------------Handle UI Bottom Player----------------*/
fun MainActivity.handleSongInfoBottomPlayer() {
	musicService?.currentSong?.let { song ->
		GlideUtils.loadImageFromUrl(
			image = binding.bottomPlayer.ivSong,
			url = song.albumArt,
			options = RequestOptions().transform(RoundedCorners(8))
		)
		
		binding.bottomPlayer.apply {
			tvTitle.text = song.title
			tvArtist.text = song.artist
		}
	}
}

fun MainActivity.startOrStopAnimationImageSong(isStart: Boolean = true) {
	Log.i("startOrStopAnimationImageSong", "isStart: $isStart")
	val viewHolder = getDetailSongViewHolder() ?: return
	Log.i("startOrStopAnimationImageSong", "getDetailSongViewHolder")
	if (isStart) {
		viewHolder.ivSong.animate()
			.rotationBy(360f)
			.setDuration(20000)
			.setInterpolator(LinearInterpolator())
			.withEndAction {
				startOrStopAnimationImageSong(mainViewModel.isPlaying.value == true)
			}
			.start()
		
	} else {
		viewHolder.ivSong.animate().cancel()
	}
}

/**----------------Handle UI Bottom Sheet Detail----------------*/
fun MainActivity.setUpBottomSheetDetail() {
	bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetDetail.layoutContainer)
	binding.bottomSheetDetail.toolBar.setPaddingStatusBar()
	bottomSheetBehavior.isGestureInsetBottomIgnored = true
	
	bottomSheetBehavior.addBottomSheetCallback(object :
		BottomSheetBehavior.BottomSheetCallback() {
		override fun onStateChanged(bottomSheet: View, newState: Int) {
			when (newState) {
				BottomSheetBehavior.STATE_EXPANDED -> {
					startOrStopAnimationImageSong(mainViewModel.isPlaying.value == true)
				}
				
				else -> return
			}
		}
		
		override fun onSlide(bottomSheet: View, slideOffset: Float) {
			handleBackgroundItemSongDetail(slideOffset)
			Log.i("CHECK_DETAIL", "onSlide: ")
		}
	})
	
}

private fun MainActivity.handleBackgroundItemSongDetail(slideOffset: Float = 1F) {
	val viewHolder = getDetailSongViewHolder() ?: return
	if (viewHolder.isLoadImageSuccess) {
		val bg = getGradientBackground(viewHolder.ivSong).apply {
			cornerRadii = if (slideOffset == 1F) {
				FloatArray(8) { 0f }
			} else {
				floatArrayOf(
					resources.getDimensionPixelSize(R.dimen.dp20).toFloat(),
					resources.getDimensionPixelSize(R.dimen.dp20).toFloat(),
					resources.getDimensionPixelSize(R.dimen.dp20).toFloat(),
					resources.getDimensionPixelSize(R.dimen.dp20).toFloat(),
					0f,
					0f,
					0f,
					0f
				)
			}
		}
		viewHolder.viewBackground.background = bg
	} else {
		viewHolder.viewBackground.setBackgroundResource(
			if (slideOffset == 1F) R.drawable.bg_solid_gray
			else R.drawable.bg_solid_gray_corner
		)
	}
	
}

fun MainActivity.handleSongInfoBottomSheetDetail() {
	musicService?.currentSong?.let { song ->
		binding.bottomSheetDetail.tvTitle.text = song.title
		binding.bottomSheetDetail.tvArtist.text = song.artist
	}
}

fun MainActivity.updateRealTimeSlider() {
	lifecycleScope.launch {
		while (isActive) {
			musicService?.exoPlayer?.let {
				binding.bottomSheetDetail.apply {
					slider.apply {
						if (it.duration > 0) valueTo = it.duration.toFloat()
						if (valueTo > 0) valueFrom = 0f
						if (it.currentPosition > 0 && it.currentPosition < valueTo) value =
							it.currentPosition.toFloat()
					}
					tvTotalTime.text = timeFormatter().format(it.duration)
					tvRunTime.text = timeFormatter().format(it.currentPosition)
				}
			}
			delay(200)
		}
	}
}

fun MainActivity.setShuffleStatus() {
	val shuffleImageResource =
		if (musicService?.exoPlayer?.shuffleModeEnabled == true) R.drawable.ic_shuffle_on else R.drawable.ic_shuffle_off
	binding.bottomSheetDetail.ivShuffle.setImageResource(shuffleImageResource)
}

fun MainActivity.setRepeatStatus() {
	val repeatImageResource = when (musicService?.exoPlayer?.repeatMode) {
		REPEAT_MODE_OFF -> R.drawable.ic_repeat_off
		REPEAT_MODE_ONE -> R.drawable.ic_repeat_one
		REPEAT_MODE_ALL -> R.drawable.ic_repeat_all
		else -> null
	}
	repeatImageResource?.let { binding.bottomSheetDetail.ivRepeat.setImageResource(it) }
}

fun MainActivity.setFavoriteStatus() {
	musicService?.currentSong?.let {
		if (mainViewModel.checkSongInPlaylist(it, 1)) {
			binding.bottomSheetDetail.ivFavorite.setImageResource(R.drawable.ic_favorite_on)
		} else {
			binding.bottomSheetDetail.ivFavorite.setImageResource(R.drawable.ic_favorite_off)
		}
	}
}

fun MainActivity.setupSliderMusic() {
	binding.bottomSheetDetail.slider.apply {
		setLabelFormatter { value: Float ->
			timeFormatter().format(value)
		}
		addOnChangeListener { slider, value, fromUser ->
			if (fromUser) {
				musicService?.exoPlayer?.seekTo(value.toLong())
			}
		}
	}
}

/**----------------Handle OnClick Bottom Player----------------*/
fun MainActivity.handleOnClickBottomMediaPlayer() {
	binding.bottomPlayer.ivPlayOrPause.setOnClickListener {
		handleOnClickPlayOrPause()
	}
	
	binding.bottomPlayer.ivNext.setOnSafeClickListener {
		handleOnClickNext()
	}
	
	binding.bottomPlayer.ivPrevious.setOnSafeClickListener {
		handleOnClickPrevious()
	}
	
	binding.bottomPlayer.container.setOnSafeClickListener {
		bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
		scrollToCurrentSong(false)
	}
}

fun MainActivity.handleOnClickPlayOrPause() {
	if (mainViewModel.isPlaying.value == true) {
		sendActionToService(ActionMusic.PAUSE.action)
	} else {
		sendActionToService(ActionMusic.RESUME.action)
	}
}

fun MainActivity.handleOnClickNext() {
	sendActionToService(ActionMusic.NEXT.action)
}

fun MainActivity.handleOnClickPrevious() {
	sendActionToService(ActionMusic.PREVIOUS.action)
}

/**----------------Handle Onclick Bottom Sheet Detail----------------*/
fun MainActivity.handleOnClickBottomSheetPlayer() {
	binding.bottomSheetDetail.ivDown.setOnSafeClickListener {
		bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
	}
	
	binding.bottomSheetDetail.ivShuffle.setOnClickListener {
		handleOnClickShuffle()
	}
	
	binding.bottomSheetDetail.ivRepeat.setOnClickListener {
		handleOnClickRepeat()
	}
	
	binding.bottomSheetDetail.ivPrevious.setOnSafeClickListener {
		handleOnClickPrevious()
	}
	
	binding.bottomSheetDetail.ivPlayOrPause.setOnClickListener {
		handleOnClickPlayOrPause()
	}
	
	binding.bottomSheetDetail.ivNext.setOnSafeClickListener {
		handleOnClickNext()
	}
	
	binding.bottomSheetDetail.ivFavorite.setOnClickListener {
		handleOnClickFavorite()
	}
}

fun MainActivity.handleOnClickShuffle() {
	musicService?.exoPlayer?.let {
		it.shuffleModeEnabled = !it.shuffleModeEnabled
	}
	setShuffleStatus()
}

fun MainActivity.handleOnClickRepeat() {
	musicService?.exoPlayer?.repeatMode = when (musicService?.exoPlayer?.repeatMode) {
		REPEAT_MODE_OFF -> REPEAT_MODE_ONE
		REPEAT_MODE_ONE -> REPEAT_MODE_ALL
		REPEAT_MODE_ALL -> REPEAT_MODE_OFF
		else -> {
			REPEAT_MODE_OFF
		}
	}
	setRepeatStatus()
}

fun MainActivity.handleOnClickFavorite() {
	musicService?.currentSong?.let {
		if (mainViewModel.checkSongInPlaylist(it, 1)) {
			mainViewModel.removeSongFromPlaylist(it, 1)
		} else {
			mainViewModel.addSongToPlaylist(it, 1)
		}
		setFavoriteStatus()
	}
}

/**----------------Recycler View Detail----------------*/

fun MainActivity.scrollToCurrentSong(isSmooth: Boolean = true) {
	musicService?.songs?.let {
		val position = it.indexOf(musicService?.currentSong)
		binding.bottomSheetDetail.rcvDetail.apply {
			if (isSmooth) smoothScrollToPosition(position) else scrollToPosition(position)
		}
	}
}

/**----------------Others----------------*/
fun MainActivity.sendActionToService(action: Int) {
	Intent(this, MusicService::class.java).apply {
		putExtra(ACTION_MUSIC_SERVICE, action)
		startService(this)
	}
}

fun MainActivity.getCurrentSongPosition(): Int {
	return musicService?.let {
		it.songs.indexOf(it.currentSong)
	} ?: 0
}

fun MainActivity.getDetailSongViewHolder(): DetailSongViewHolder? {
	val currentPosition =
		detailLayoutManager.findFirstCompletelyVisibleItemPosition()
	return binding.bottomSheetDetail.rcvDetail.findViewHolderForAdapterPosition(currentPosition) as? DetailSongViewHolder
}

