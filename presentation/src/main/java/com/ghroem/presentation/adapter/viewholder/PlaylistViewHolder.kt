package com.ghroem.presentation.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ghroem.R
import com.ghroem.databinding.ItemPlaylistBinding
import com.ghroem.domain.model.Playlist
import com.ghroem.presentation.utils.GlideUtils
import com.ghroem.presentation.utils.setOnSafeClickListener

class PlaylistViewHolder(
	private val binding: ItemPlaylistBinding,
	private val onClickItemPlaylist: (Playlist) -> Unit,
	private val onClickPopupMenu: (Playlist, View) -> Unit,
) : ViewHolder(binding.root) {
	
	init {
		setupUIItemPlaylist(itemView, bindingAdapterPosition)
	}
	
	fun bind(playlist: Playlist) {
		if (playlist.id == 1) {
			binding.ivMore.visibility = View.GONE
		}
		binding.tvNamePlaylist.text = playlist.name
		val pluralResource =
			if (playlist.songs.size > 1) R.string.songs_with_placeholder else R.string.song_with_placeholder
		binding.tvNumberOfSongs.text =
			itemView.context.getString(pluralResource, playlist.songs.size)
		
		if (playlist.songs.isNotEmpty()) {
			GlideUtils.loadImageFromUrl(binding.ivPlaylist, playlist.songs[0].albumArt)
		} else {
			binding.ivPlaylist.setImageResource(R.drawable.ic_favorite_on)
		}
		
		binding.root.setOnSafeClickListener {
			onClickItemPlaylist.invoke(playlist)
		}
		
		if (playlist.id != 1) {
			binding.root.setOnLongClickListener {
				onClickPopupMenu.invoke(playlist, binding.ivMore)
				true
			}
		}
		
		binding.ivMore.setOnSafeClickListener {
			onClickPopupMenu.invoke(playlist, binding.ivMore)
		}
	}
}
