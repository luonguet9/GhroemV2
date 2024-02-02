package com.ghroem.domain.use_case.song

import com.ghroem.domain.model.Song
import com.ghroem.domain.repository.SongRepository

class GetSongsFromStorage(
	private val songRepository: SongRepository
) {
	operator fun invoke(): List<Song> {
		return songRepository.getSongsFromStorage()
	}
}
