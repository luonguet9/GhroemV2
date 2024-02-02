package com.ghroem.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ghroem.databinding.ItemSongInDetailBinding
import com.ghroem.domain.model.Song
import com.ghroem.presentation.adapter.viewholder.DetailSongViewHolder
import java.util.concurrent.Executors

class DetailAdapter: ListAdapter<Any, RecyclerView.ViewHolder>(
    AsyncDifferConfig.Builder(SongAdapter.ItemSongCallback())
        .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor())
        .build()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailSongViewHolder {
        return DetailSongViewHolder(
            ItemSongInDetailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is DetailSongViewHolder -> {
                val song = item as? Song ?: return
                holder.bind(song)
            }
        }
    }
}
