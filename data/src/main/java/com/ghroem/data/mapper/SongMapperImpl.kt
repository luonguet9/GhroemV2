package com.ghroem.data.mapper

import com.ghroem.data.model.SongRealm
import com.ghroem.domain.mapper.SongMapper
import com.ghroem.domain.model.Song

class SongMapperImpl : SongMapper<SongRealm, Song> {
	override fun mapToSong(from: SongRealm): Song {
		return Song(
			id = from.id,
			title = from.title,
			artist = from.artist,
			duration = from.duration,
			data = from.data,
			albumArt = from.albumArt,
			playlistId = from.playlistId
		)
	}
	
	override fun mapToSongRealm(from: Song): SongRealm {
		return SongRealm(
			id = from.id,
			title = from.title,
			artist = from.artist,
			duration = from.duration,
			data = from.data,
			albumArt = from.albumArt,
			playlistId = from.playlistId
		)
	}
}
