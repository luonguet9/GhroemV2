package com.ghroem.domain.use_case.playlist

import com.ghroem.domain.repository.PlaylistRepository

class GetPlaylistId(
	private val playlistRepository: PlaylistRepository
) {
	operator fun invoke(): Int = playlistRepository.getPlaylistId()
	
}
