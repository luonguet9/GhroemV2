package com.ghroem.presentation.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ghroem.R
import com.ghroem.databinding.FragmentHomeBinding
import com.ghroem.domain.model.Song
import com.ghroem.presentation.adapter.SongAdapter
import com.ghroem.presentation.base.BaseFragment
import com.ghroem.presentation.ui.main.MainActivity
import com.ghroem.presentation.utils.enums.ScreenType
import com.ghroem.presentation.utils.ext.setPaddingStatusBar
import com.ghroem.presentation.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
	
	private val mainViewModel: MainViewModel by activityViewModels()
	private var songs = mutableListOf<Song>()
	private lateinit var songAdapter: SongAdapter
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		Log.i("CHECK_INIT", "onCreate: $this")
	}
	
	override fun getContentLayout(): Int = R.layout.fragment_home
	
	override fun initView() {
		binding.root.setPaddingStatusBar()
		setUpRecyclerView()
		getSongsFromLocal()
	}
	
	override fun initListener() {
		handleSearchBarListener()
	}
	
	override fun observerLiveData() {
		mainViewModel.songs.observe(viewLifecycleOwner) { songs ->
			this.songs = songs.toMutableList()
			songAdapter.submitList(songs)
			Log.i("CHECK_SONGS", "songs: $songs")
		}
	}
	
	override fun onResume() {
		super.onResume()
		Log.i("CHECK_HOME", "onResume: ")
	}
	
	override fun onPause() {
		super.onPause()
		Log.i("CHECK_HOME", "onPause: ")
	}
	
	override fun onStop() {
		super.onStop()
		Log.i("CHECK_HOME", "onStop: ")
	}
	
	override fun onDestroy() {
		super.onDestroy()
		Log.i("CHECK_HOME", "onDestroy: ")
	}
	
	private fun setUpRecyclerView() {
		songAdapter = SongAdapter(
			screenType = ScreenType.HOME,
			onClickItemSong = { song ->
				startMusic(song)
			},
			onLongClickItemSong = {song, view ->
			
			}
		)
		
		binding.rcvListSong.apply {
			adapter = songAdapter
			layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
		}
	}
	
	private fun startMusic(song: Song) {
		val mainActivity = requireActivity() as MainActivity
		mainActivity.startMusicService(song)
	}
	
	private fun getSongsFromLocal() {
		mainViewModel.getSongsFromStorage()
	}
	
	private fun handleSearchBarListener() {
		binding.edtSearch.setOnEditorActionListener { _, actionId, _ ->
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
			
		})
	}
	
	private fun searchSong() {
		val searchText = binding.edtSearch.text.toString().lowercase()
		val filteredList = songs.filter { it.title?.lowercase()?.contains(searchText) == true }
		songAdapter.submitList(filteredList)
	}
	
	fun handleReselectedHome() {
		binding.rcvListSong.smoothScrollToPosition(0)
	}
	
}
