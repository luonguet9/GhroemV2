package com.ghroem.presentation.ui.dialog

import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ghroem.R
import com.ghroem.databinding.DialogAddSongToPlaylistBinding
import com.ghroem.domain.model.Playlist
import com.ghroem.domain.model.Song
import com.ghroem.presentation.adapter.SongAdapter
import com.ghroem.presentation.base.BaseDialog
import com.ghroem.presentation.utils.StatusBarUtils
import com.ghroem.presentation.utils.enums.ScreenType
import com.ghroem.presentation.viewmodel.MainViewModel
import com.ghroem.presentation.viewmodel.PlaylistViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddSongToPlaylistDialog(
	private var playlist: Playlist
) : BaseDialog<DialogAddSongToPlaylistBinding>() {
	
	private val mainViewModel: MainViewModel by viewModels()
	private val playlistViewModel: PlaylistViewModel by activityViewModels()
	
	private lateinit var songAdapter: SongAdapter
	private var songs = mutableListOf<Song>()
	private var addSongToPlaylistCallback: ((song: Song) -> Unit)? = null
	
	override fun getLayoutResource(): Int = R.layout.dialog_add_song_to_playlist
	
	override fun getGravityForDialog(): Int = Gravity.CENTER
	
	override fun initView() {
		Log.i("CHECK_UPDATE", "playlist: $playlist")
		//binding.container.setPaddingStatusBar()
		setFullScreenDialog()
		mainViewModel.getSongsFromStorage()
		setupRecyclerView()
		observeLiveData()
		dialog?.let { StatusBarUtils.makeStatusBarTransparentDialog(it) }
	}
	
	override fun initListener() {
		handleSearchBarListener()
		binding.ivClose.setOnClickListener {
			dismiss()
		}
	}
	
	private fun setupRecyclerView() {
		songAdapter = SongAdapter(
			screenType = ScreenType.ADD_SONG_TO_PLAYLIST_DIALOG,
			playlist = playlist,
			onClickItemSong = { song ->
				if (playlistViewModel.addSongToPlaylist(song, playlist.id)) {
					val newPlaylist = playlistViewModel.getPlaylistById(playlist.id)
					lifecycleScope.launch {
						delay(100)
						newPlaylist?.let { songAdapter.updatePlaylist(it) }
						songAdapter.notifyItemChanged(songs.indexOfFirst { it.id == song.id }, song)
					}
					
					Toast.makeText(requireContext(), "Successfully", Toast.LENGTH_SHORT).show()
				}
				
			}
		)
		
		binding.rcvListSong.apply {
			adapter = songAdapter
			layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
		}
	}
	
	private fun observeLiveData() {
		mainViewModel.songs.observe(viewLifecycleOwner) { songs ->
			this.songs = songs.toMutableList()
			songAdapter.submitList(songs)
		}
	}
	
	private fun handleSearchBarListener() {
/*		binding.edtSearch.setOnEditorActionListener { _, actionId, _ ->
			if (actionId == EditorInfo.IME_ACTION_SEARCH) {
				searchSong()
			}
			false
		}
		
		binding.edtSearch.addTextChangedListener(object : TextWatcher {
			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
			
			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
			
			override fun afterTextChanged(s: Editable?) {
				searchSong()
			}
			
		})*/
		
	}
	
	private fun searchSong() {
		val searchText = binding.searchBar.text.toString().lowercase()
		val filteredList = songs.filter { it.title?.lowercase()?.contains(searchText) == true }
		songAdapter.submitList(filteredList)
	}
	
	fun addSongToPlaylistCallback(callback: ((song: Song) -> Unit)? = null) {
		addSongToPlaylistCallback = callback
	}
	
	
}
