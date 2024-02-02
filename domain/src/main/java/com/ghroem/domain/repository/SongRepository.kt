package com.ghroem.domain.repository

import com.ghroem.domain.model.Song

interface SongRepository {
	fun getSongsFromStorage(): List<Song>
}
