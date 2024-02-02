package com.ghroem.data.mapper

import com.ghroem.data.model.PlaylistRealm
import com.ghroem.data.model.SongRealm
import com.ghroem.domain.mapper.PlaylistMapper
import com.ghroem.domain.mapper.SongMapper
import com.ghroem.domain.model.Playlist
import com.ghroem.domain.model.Song

class PlaylistMapperImpl(
	private val songMapper: SongMapper<SongRealm, Song>
) : PlaylistMapper<PlaylistRealm, Playlist> {
	
	override fun mapToPlaylist(from: PlaylistRealm): Playlist {
		return Playlist(
			id = from.id,
			name = from.name,
			songs = from.songs.map {
				songMapper.mapToSong(it)
			}
		)
	}
	
	override fun mapToPlaylistRealm(from: Playlist): PlaylistRealm {
		return PlaylistRealm(
			id = from.id,
			name = from.name,
		).apply {
			songs.addAll(from.songs.map {
				songMapper.mapToSongRealm(it)
			})
		}
	}
}
