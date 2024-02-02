package com.ghroem.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ghroem.domain.model.Playlist
import com.ghroem.domain.use_case.playlist.PlaylistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
	private val playlistUseCase: PlaylistUseCase
) : ViewModel() {
	
	private val _listPlaylist = MutableLiveData<List<Playlist>>()
	val listPlaylist: LiveData<List<Playlist>>
		get() = _listPlaylist
	
	fun getOrUpdateListPlaylist() {
		_listPlaylist.postValue(playlistUseCase.getAllPlaylist.invoke())
	}
	
	fun addPlaylist(playlist: Playlist) {
		playlistUseCase.addPlaylist.invoke(playlist)
		getOrUpdateListPlaylist()
	}
	
	fun renamePlaylist(playlist: Playlist, name: String) {
		playlistUseCase.renamePlaylist.invoke(playlist, name)
		getOrUpdateListPlaylist()
	}
	
	fun deletePlaylist(playlist: Playlist) {
		playlistUseCase.deletePlaylist.invoke(playlist)
		getOrUpdateListPlaylist()
	}
	
	fun getPlaylistId(): Int {
		return playlistUseCase.getPlaylistId.invoke()
	}
	
}
