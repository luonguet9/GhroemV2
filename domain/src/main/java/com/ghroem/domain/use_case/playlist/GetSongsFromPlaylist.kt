package com.ghroem.domain.use_case.playlist

import com.ghroem.domain.model.Playlist
import com.ghroem.domain.model.Song
import com.ghroem.domain.repository.PlaylistRepository

class GetSongsFromPlaylist(
	private val playlistRepository: PlaylistRepository
) {
	operator fun invoke(playlist: Playlist): List<Song> {
		return playlistRepository.getSongsInPlaylist(playlist)
	}
}
