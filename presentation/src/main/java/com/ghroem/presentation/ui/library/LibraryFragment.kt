package com.ghroem.presentation.ui.library

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.ghroem.R
import com.ghroem.databinding.FragmentLibraryBinding
import com.ghroem.domain.model.Playlist
import com.ghroem.presentation.adapter.PlaylistAdapter
import com.ghroem.presentation.base.BaseFragment
import com.ghroem.presentation.ui.dialog.CreateNewPlaylistDialog
import com.ghroem.presentation.ui.dialog.DeletePlaylistDialog
import com.ghroem.presentation.ui.dialog.RenamePlaylistDialog
import com.ghroem.presentation.utils.ext.setPaddingStatusBar
import com.ghroem.presentation.viewmodel.LibraryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LibraryFragment : BaseFragment<FragmentLibraryBinding>() {
	
	private lateinit var playlistAdapter: PlaylistAdapter
	
	private val libraryViewModel: LibraryViewModel by viewModels()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		Log.i("CHECK_INIT", "onCreate: $this")
	}
	
	override fun getContentLayout(): Int = R.layout.fragment_library
	
	override fun initView() {
		binding.root.setPaddingStatusBar()
		setupRecyclerView()
		libraryViewModel.getOrUpdateListPlaylist()
	}
	
	override fun initListener() {
		binding.ivAddPlaylist.setOnClickListener {
			activity?.supportFragmentManager?.let {
				CreateNewPlaylistDialog().apply {
					addPlaylistCallback { name ->
						libraryViewModel.addPlaylist(
							Playlist(libraryViewModel.getPlaylistId(), name)
						)
					}
					show(it, this.tag)
				}
			}
		}
	}
	
	override fun observerLiveData() {
		libraryViewModel.listPlaylist.observe(viewLifecycleOwner) {
			if (it.isEmpty()) {
				initFavoritePlaylist()
			}
			playlistAdapter.submitList(it)
			
			Log.i("CHECK_PLAYLIST", "playlist list: $it")
		}
	}
	
	private fun setupRecyclerView() {
		playlistAdapter = PlaylistAdapter(
			onClickItemPlaylist = {
				handleOnClickItemPlaylist(it)
			},
			onClickPopupMenu = { playlist, view ->
				handleOnClickPopupMenu(playlist, view)
			}
		)
		binding.rvPlaylist.adapter = playlistAdapter
		binding.rvPlaylist.layoutManager = GridLayoutManager(requireContext(), 2)
	}
	
	
	private fun initFavoritePlaylist() {
		libraryViewModel.addPlaylist(
			Playlist(libraryViewModel.getPlaylistId(), "Favorite")
		)
	}
	
	private fun handleOnClickItemPlaylist(playlist: Playlist) {
		val action = LibraryFragmentDirections.actionLibraryFragmentToPlaylistFragment(playlist)
		findNavController().navigate(action)
	}
	
	private fun handleOnClickPopupMenu(playlist: Playlist, view: View) {
		PopupMenu(requireContext(), view).apply {
			menuInflater.inflate(R.menu.menu_more_playlist, menu)
			setOnMenuItemClickListener {
				when (it.itemId) {
					R.id.item_rename -> {
						renamePlaylist(playlist)
						return@setOnMenuItemClickListener true
					}
					
					else -> {
						deletePlaylist(playlist)
						return@setOnMenuItemClickListener true
					}
				}
			}
			show()
		}
	}
	
	private fun renamePlaylist(playlist: Playlist) {
		activity?.supportFragmentManager?.let {
			RenamePlaylistDialog().apply {
				renamePlaylistCallback { name ->
					libraryViewModel.renamePlaylist(playlist, name)
				}
				show(it, this.tag)
			}
		}
	}
	
	private fun deletePlaylist(playlist: Playlist) {
		activity?.supportFragmentManager?.let {
			DeletePlaylistDialog().apply {
				deletePlaylistCallback {
					libraryViewModel.deletePlaylist(playlist)
				}
				show(it, this.tag)
			}
		}
	}
	
	fun handleReselectedLibrary() {
		binding.rvPlaylist.smoothScrollToPosition(0)
	}
}
