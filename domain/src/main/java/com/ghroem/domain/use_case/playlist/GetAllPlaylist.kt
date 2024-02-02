package com.ghroem.domain.use_case.playlist

import com.ghroem.domain.model.Playlist
import com.ghroem.domain.repository.PlaylistRepository

class GetAllPlaylist(
	private val playlistRepository: PlaylistRepository
) {
	operator fun invoke(): List<Playlist> {
		return playlistRepository.getAllPlaylist()
	}
}
