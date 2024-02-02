package com.ghroem.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ghroem.databinding.ItemPlaylistBinding
import com.ghroem.domain.model.Playlist
import com.ghroem.presentation.adapter.viewholder.PlaylistViewHolder
import java.util.concurrent.Executors

class PlaylistAdapter(
	private val onClickItemPlaylist: (Playlist) -> Unit,
	private val onClickPopupMenu: (Playlist, View) -> Unit,
) : ListAdapter<Any, RecyclerView.ViewHolder>(
	AsyncDifferConfig.Builder(ItemPlaylistCallback())
		.setBackgroundThreadExecutor(Executors.newSingleThreadExecutor())
		.build()
) {
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		return PlaylistViewHolder(
			binding = ItemPlaylistBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
			),
			onClickItemPlaylist = onClickItemPlaylist,
			onClickPopupMenu = onClickPopupMenu
		)
	}
	
	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		val item = getItem(position)
		when (holder) {
			is PlaylistViewHolder -> {
				val playlist = item as? Playlist ?: return
				holder.bind(playlist)
			}
		}
	}
	
	class ItemPlaylistCallback : DiffUtil.ItemCallback<Any>() {
		override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
			return if (oldItem is Playlist && newItem is Playlist) oldItem.id == newItem.id
			else false
		}
		
		override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
			return if (oldItem is Playlist && newItem is Playlist) oldItem == newItem
			else false
		}
		
		override fun getChangePayload(oldItem: Any, newItem: Any): Any {
			return newItem
		}
	}
}
