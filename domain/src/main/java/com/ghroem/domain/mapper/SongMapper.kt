package com.ghroem.domain.mapper

interface SongMapper<E, M> {
	fun mapToSong(from: E): M
	fun mapToSongRealm(from: M): E
}
