package com.ghroem.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ghroem.domain.model.Playlist
import com.ghroem.domain.model.Song
import com.ghroem.domain.use_case.playlist.PlaylistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
	private val playlistUseCase: PlaylistUseCase,
) : ViewModel() {
	
	private val _songs = MutableLiveData<List<Song>>()
	val songs: LiveData<List<Song>>
		get() = _songs
	
	fun getOrUpdateSongsInPlaylist(playlist: Playlist) {
		val songs = playlistUseCase.getSongsFromPlaylist.invoke(playlist)
		_songs.postValue(songs)
	}
	
	fun addSongToPlaylist(song: Song, playlistId: Int): Boolean {
		if (playlistUseCase.addSongToPlaylist.invoke(song, playlistId)) {
			getPlaylistById(playlistId)?.let { getOrUpdateSongsInPlaylist(it) }
			return true
		}
		return false
	}
	
	fun removeSongFromPlaylist(song: Song, playlistId: Int) {
		playlistUseCase.removeSongFromPlaylist.invoke(song, playlistId)
	}
	
	fun checkSongInPlaylist(song: Song, playlistId: Int): Boolean {
		return playlistUseCase.checkSongInPlaylist.invoke(song, playlistId)
	}
	
	fun getPlaylistById(id: Int): Playlist? {
		return playlistUseCase.getPlaylistById.invoke(id)
	}
	
	public override fun onCleared() {
		_songs.postValue(emptyList())
		super.onCleared()
		Log.i("CHECK_PLAYLIST_SCREEN", "onCleared: ")
	}
	
}

