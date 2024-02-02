package com.ghroem.data.repository

import com.ghroem.data.data_source.local.realm.RealmManager
import com.ghroem.data.model.PlaylistRealm
import com.ghroem.data.model.SongRealm
import com.ghroem.data.utils.ID_PLAYLIST
import com.ghroem.domain.mapper.PlaylistMapper
import com.ghroem.domain.mapper.SongMapper
import com.ghroem.domain.model.Playlist
import com.ghroem.domain.model.Song
import com.ghroem.domain.repository.PlaylistRepository
import com.ghroem.domain.repository.SharedPreferencesManager

class PlaylistRepositoryImpl(
	private val sharedPreferencesManager: SharedPreferencesManager,
	private val realmManager: RealmManager,
	private val playlistMapper: PlaylistMapper<PlaylistRealm, Playlist>,
	private val songMapper: SongMapper<SongRealm, Song>,
) : PlaylistRepository {
	
	override fun getAllPlaylist(): List<Playlist> {
		return (realmManager.findAll(PlaylistRealm::class.java))?.map {
			playlistMapper.mapToPlaylist(it)
		} ?: listOf()
	}
	
	override fun addPlaylist(playlist: Playlist) {
		if (!checkValidPlaylistName(playlist.name)) return
		realmManager.save(playlistMapper.mapToPlaylistRealm(playlist))
	}
	
	override fun renamePlaylist(playlist: Playlist, newName: String) {
		if (!checkValidPlaylistName(newName)) return
		val playlistRealm = playlistMapper.mapToPlaylistRealm(playlist)
		realmManager.save(playlistRealm) {
			playlistRealm.name = newName
		}
	}
	
	override fun deletePlaylist(playlist: Playlist) {
		getPlaylistRealmById(playlist.id)?.let {
			realmManager.delete(it)
		}
	}
	
	override fun getPlaylistId(): Int {
		increaseId()
		return sharedPreferencesManager.get(ID_PLAYLIST, 0)
	}
	
	override fun getPlaylistById(id: Int): Playlist? {
		val playlistRealm = realmManager.findFirst(PlaylistRealm::class.java, "id" to id)
		return playlistRealm?.let { playlistMapper.mapToPlaylist(it) }
	}
	
	override fun checkValidPlaylistName(name: String): Boolean {
		if (name.isEmpty()) return false
		realmManager.findFirst(PlaylistRealm::class.java, "name" to name)?.let {
			return false
		}
		return true
	}
	
	override fun getSongsInPlaylist(playlist: Playlist): List<Song> {
		return getPlaylistRealmById(playlist.id)?.let { playlistRealm ->
			playlistRealm.songs.map {
				songMapper.mapToSong(it)
			}
		} ?: emptyList()
	}
	
	override fun addSongToPlaylist(song: Song, playlistId: Int): Boolean {
		if (checkSongInPlaylist(song, playlistId)) return false
		getPlaylistRealmById(playlistId)?.let { playlistRealm ->
			val songRealm = songMapper.mapToSongRealm(song)
			realmManager.save(playlistRealm) {
				playlistRealm.songs.add(songRealm)
			}
			return true
		}
		return false
	}
	
	override fun removeSongFromPlaylist(song: Song, playlistId: Int) {
		getPlaylistRealmById(playlistId)?.let { playlistRealm ->
			realmManager.executeRealmTransaction {
				playlistRealm.songs.removeIf { it.id == song.id }
			}
		}
	}
	
	override fun checkSongInPlaylist(song: Song, playlistId: Int): Boolean {
		val playlistRealm = getPlaylistRealmById(playlistId)
		return playlistRealm?.songs?.any { it.id == song.id } ?: false
	}
	
	private fun getPlaylistRealmById(id: Int): PlaylistRealm? {
		return realmManager.findFirst(PlaylistRealm::class.java, "id" to id)
	}
	
	private fun increaseId() {
		var id = sharedPreferencesManager.get(ID_PLAYLIST, 0)
		sharedPreferencesManager.put(ID_PLAYLIST, ++id)
	}
}
