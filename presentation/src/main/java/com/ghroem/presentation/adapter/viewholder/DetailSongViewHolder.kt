package com.ghroem.presentation.adapter.viewholder

import android.os.Build
import android.util.Log
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ghroem.R
import com.ghroem.databinding.ItemSongInDetailBinding
import com.ghroem.domain.model.Song
import com.ghroem.presentation.utils.GlideUtils
import com.ghroem.presentation.utils.ext.setPaddingStatusBar
import com.ghroem.presentation.utils.getGradientBackground
import eightbitlab.com.blurview.RenderEffectBlur

class DetailSongViewHolder(
	private val binding: ItemSongInDetailBinding
) : ViewHolder(binding.root) {
	
	var isLoadImageSuccess = false
	val viewBackground = binding.viewBackground
	val ivSong = binding.ivSong
	
	init {
		binding.layoutSong.setPaddingStatusBar()
	}
	
	fun bind(song: Song) {
/*		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			binding.blurView.setupWith(binding.container, RenderEffectBlur()).setBlurRadius(20f)
		}*/
		GlideUtils.loadImageFromUrlWithCallback(binding.root.context, song.albumArt,
			onSuccess = {
				isLoadImageSuccess = true
				ivSong.setImageBitmap(it)
				viewBackground.background = getGradientBackground(it)
			},
			onFailed = {
				isLoadImageSuccess = false
				ivSong.setImageBitmap(it)
				viewBackground.setBackgroundResource(R.drawable.bg_solid_gray)
			})
	}
}
