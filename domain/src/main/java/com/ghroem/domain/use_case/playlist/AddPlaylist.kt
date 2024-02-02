package com.ghroem.domain.use_case.playlist

import com.ghroem.domain.model.Playlist
import com.ghroem.domain.repository.PlaylistRepository

class AddPlaylist(
	private val playlistRepository: PlaylistRepository
) {
	operator fun invoke(playlist: Playlist) {
		return playlistRepository.addPlaylist(playlist)
	}
}
