package com.ghroem.domain.mapper

interface PlaylistMapper<E, M> {
	fun mapToPlaylist(from: E): M
	fun mapToPlaylistRealm(from: M): E
}
