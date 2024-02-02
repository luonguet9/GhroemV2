package com.ghroem.domain.model

import java.io.Serializable

data class Song(
	val id: Long,
	val title: String?,
	val artist: String?,
	val duration: Long?,
	val data: String?,
	val albumArt: String?,
	var playlistId: Int? = 0
) : Serializable
