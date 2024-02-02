package com.ghroem.domain.model

import java.io.Serializable

data class Playlist(
	val id: Int = 0,
	var name: String = "",
	val songs: List<Song> = listOf()
) : Serializable
