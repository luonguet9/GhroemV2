package com.ghroem.presentation.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ghroem.R
import com.ghroem.databinding.ItemPlaylistBinding
import com.ghroem.domain.model.Playlist
import com.ghroem.presentation.utils.GlideUtils

class PlaylistViewHolder(
	private val binding: ItemPlaylistBinding,
	private val onClickItemPlaylist: (Playlist) -> Unit,
	private val onClickPopupMenu: (Playlist, View) -> Unit,
) : ViewHolder(binding.root) {
	
	init {
		setupUIItemPlaylist(itemView, bindingAdapterPosition)
	}
	
	fun bind(playlist: Playlist, numberOfSongsText: String) {
		if (playlist.id == 1) {
			binding.ivMore.visibility = View.GONE
		}
		binding.tvNamePlaylist.text = playlist.name
		binding.tvNumberOfSongs.text = numberOfSongsText
		if (playlist.songs.isNotEmpty()) {
			GlideUtils.loadImageFromUrl(binding.ivPlaylist, playlist.songs[0].albumArt)
		} else {
			binding.ivPlaylist.setImageResource(R.drawable.ic_favorite_on)
		}
		
		binding.root.setOnClickListener {
			onClickItemPlaylist.invoke(playlist)
		}
		
		binding.root.setOnLongClickListener {
			if (playlist.id != 1) {
				onClickPopupMenu.invoke(playlist, binding.ivMore)
			}
			true
		}
		
		binding.ivMore.setOnClickListener {
			onClickPopupMenu.invoke(playlist, binding.ivMore)
		}
	}
}
