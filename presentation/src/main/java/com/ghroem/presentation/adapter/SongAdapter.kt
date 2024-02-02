package com.ghroem.presentation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ghroem.R
import com.ghroem.databinding.ItemSongBinding
import com.ghroem.domain.model.Playlist
import com.ghroem.domain.model.Song
import com.ghroem.presentation.adapter.viewholder.SongViewHolder
import com.ghroem.presentation.utils.enums.ScreenType
import java.util.concurrent.Executors

class SongAdapter(
	private val screenType: ScreenType,
	private val onClickItemSong: (Song) -> Unit,
	private val onLongClickItemSong: ((Song, View) -> Unit)? = null,
	private val playlist: Playlist? = null
) : ListAdapter<Any, ViewHolder>(
	AsyncDifferConfig.Builder(ItemSongCallback())
		.setBackgroundThreadExecutor(Executors.newSingleThreadExecutor())
		.build()
) {
	
	private var currentPlaylist = playlist
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
		return SongViewHolder(
			binding = ItemSongBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
			),
			screenType = screenType,
			playlist = currentPlaylist,
			onClickItemSong = onClickItemSong,
			onLongClickItemSong = onLongClickItemSong
		)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val item = getItem(position)
		when (holder) {
			is SongViewHolder -> {
				val song = item as? Song ?: return
				holder.bind(song)
			}
		}
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
		if (payloads.isNotEmpty()) {
			val payload = payloads[0]
			val songViewHolder = holder as? SongViewHolder ?: return
			val song = payload as? Song ?: return
			currentPlaylist?.let { songViewHolder.updatePlaylist(it) }
			songViewHolder.updateAddToPlaylistStatus(song)
		} else super.onBindViewHolder(holder, position, payloads)
	}
	
	fun updatePlaylist(playlist: Playlist) {
		currentPlaylist = playlist
	}
	
	class ItemSongCallback : DiffUtil.ItemCallback<Any>() {
		override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
			return if (oldItem is Song && newItem is Song) oldItem.id == newItem.id
			else false
		}
		
		override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
			return if (oldItem is Song && newItem is Song) oldItem == newItem
			else false
		}
		
		override fun getChangePayload(oldItem: Any, newItem: Any): Any {
			return newItem
		}
	}
}
