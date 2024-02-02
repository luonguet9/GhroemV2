package com.ghroem.domain.use_case.playlist

import com.ghroem.domain.model.Playlist
import com.ghroem.domain.repository.PlaylistRepository

class RenamePlaylist(
	private val playlistRepository: PlaylistRepository
) {
	operator fun invoke(playlist: Playlist, name: String) {
		return playlistRepository.renamePlaylist(playlist, name)
	}
}

