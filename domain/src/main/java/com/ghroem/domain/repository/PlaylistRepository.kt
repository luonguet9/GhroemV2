package com.ghroem.domain.repository

import com.ghroem.domain.model.Playlist
import com.ghroem.domain.model.Song

interface PlaylistRepository {
	fun getAllPlaylist(): List<Playlist>
	fun addPlaylist(playlist: Playlist)
	fun renamePlaylist(playlist: Playlist, newName: String)
	fun deletePlaylist(playlist: Playlist)
	fun getPlaylistId(): Int
	fun getPlaylistById(id: Int): Playlist?
	fun checkValidPlaylistName(name: String): Boolean
	fun getSongsInPlaylist(playlist: Playlist): List<Song>
	fun addSongToPlaylist(song: Song, playlistId: Int) : Boolean
	fun removeSongFromPlaylist(song: Song, playlistId: Int)
	fun checkSongInPlaylist(song: Song, playlistId: Int): Boolean
}
