package com.ghroem.presentation.adapter.viewholder

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.ghroem.R
import com.ghroem.databinding.ItemSongBinding
import com.ghroem.domain.model.Playlist
import com.ghroem.domain.model.Song
import com.ghroem.presentation.utils.GlideUtils
import com.ghroem.presentation.utils.enums.ScreenType

class SongViewHolder(
	private val binding: ItemSongBinding,
	private val screenType: ScreenType,
	private val playlist: Playlist? = null,
	private val onClickItemSong: (Song) -> Unit,
	private val onLongClickItemSong: ((Song, View) -> Unit)? = null,
) : ViewHolder(binding.root) {
	
	private var currentPlaylist = playlist
	fun bind(song: Song) {
		binding.song = song
		binding.executePendingBindings()
		
		GlideUtils.loadImageFromUrl(
			image = binding.ivSong,
			url = song.albumArt,
			options = RequestOptions().transform(RoundedCorners(16)),
			placeholderResId = R.drawable.spotify_blue
		)
		
		if (screenType == ScreenType.ADD_SONG_TO_PLAYLIST_DIALOG) {
			Log.i("CHECK_BIND_DATA", "bind: song: $song")
			binding.ivAddToPlaylistStatus.visibility = View.VISIBLE
			updateAddToPlaylistStatus(song)
		}
		
		binding.root.setOnClickListener {
			onClickItemSong.invoke(song)
		}
		
		binding.root.setOnLongClickListener {
			onLongClickItemSong?.invoke(song, binding.ivMore)
			true
		}
	}
	
	fun updatePlaylist(playlist: Playlist?) {
		currentPlaylist = playlist
	}
	
	fun updateAddToPlaylistStatus(song: Song) {
		val isInPlaylist = currentPlaylist?.songs?.any { it.id == song.id } ?: false
		binding.ivAddToPlaylistStatus.setImageResource(
			if (!isInPlaylist)
				R.drawable.ic_add_circle
			else
				R.drawable.ic_check
		)
	}
}
