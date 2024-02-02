package com.ghroem.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class SongRealm(
	@PrimaryKey
	var id: Long = 0,
	var title: String? = "",
	var artist: String? = "",
	var duration: Long? = 0,
	var data: String? = "",
	var albumArt: String? = "",
	var playlistId: Int? = 0,
	
	var createDate: String = ""
) : RealmObject()
