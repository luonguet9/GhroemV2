package com.ghroem.domain.use_case.playlist

import com.ghroem.domain.model.Song
import com.ghroem.domain.repository.PlaylistRepository

class RemoveSongFromPlaylist(
	private val playlistRepository: PlaylistRepository
) {
	operator fun invoke(song: Song, playlistId: Int) {
		return playlistRepository.removeSongFromPlaylist(song, playlistId)
	}
}
