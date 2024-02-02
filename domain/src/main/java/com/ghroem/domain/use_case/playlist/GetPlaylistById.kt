package com.ghroem.domain.use_case.playlist

import com.ghroem.domain.model.Playlist
import com.ghroem.domain.repository.PlaylistRepository

class GetPlaylistById(
	private val playlistRepository: PlaylistRepository
) {
	operator fun invoke(id: Int): Playlist? = playlistRepository.getPlaylistById(id)
	
}
