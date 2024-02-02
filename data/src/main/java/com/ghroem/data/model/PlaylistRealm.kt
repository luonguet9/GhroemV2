package com.ghroem.data.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class PlaylistRealm(
    @PrimaryKey
    var id: Int = 0,
    var name: String = "",
    var songs: RealmList<SongRealm> = RealmList(),
    var createDate: String = ""
) : RealmObject()
