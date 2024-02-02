package com.ghroem.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghroem.domain.model.Song
import com.ghroem.domain.use_case.playlist.PlaylistUseCase
import com.ghroem.domain.use_case.song.SongUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
	private val songUseCase: SongUseCase,
	private val playlistUseCase: PlaylistUseCase,
) : ViewModel() {
	
	private val _songs = MutableLiveData<List<Song>>()
	val songs: LiveData<List<Song>>
		get() = _songs
	
	private val _isPlaying = MutableLiveData(false)
	val isPlaying: LiveData<Boolean>
		get() = _isPlaying
	
	fun getSongsFromStorage() {
		viewModelScope.launch(Dispatchers.IO) {
			_songs.postValue(songUseCase.getSongsFromStorage.invoke())
		}
	}
	
	fun addSongToPlaylist(song: Song, playlistId: Int) {
		playlistUseCase.addSongToPlaylist.invoke(song, playlistId)
	}
	
	fun removeSongFromPlaylist(song: Song, playlistId: Int) {
		playlistUseCase.removeSongFromPlaylist.invoke(song, playlistId)
	}
	
	fun checkSongInPlaylist(song: Song, playlistId: Int): Boolean {
		return playlistUseCase.checkSongInPlaylist.invoke(song, playlistId)
	}
	
	fun setPlayingStatus(isPlaying: Boolean) {
		_isPlaying.postValue(isPlaying)
	}
}
