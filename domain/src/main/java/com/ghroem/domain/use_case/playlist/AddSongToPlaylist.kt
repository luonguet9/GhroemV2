package com.ghroem.domain.use_case.playlist

import com.ghroem.domain.model.Song
import com.ghroem.domain.repository.PlaylistRepository
import com.ghroem.domain.repository.SongRepository

class AddSongToPlaylist(
	private val playlistRepository: PlaylistRepository
) {
	operator fun invoke(song: Song, playlistId: Int) : Boolean {
		return playlistRepository.addSongToPlaylist(song, playlistId)
	}
}
