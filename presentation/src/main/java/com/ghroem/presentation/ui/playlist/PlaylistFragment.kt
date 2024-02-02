package com.ghroem.presentation.ui.playlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ghroem.R
import com.ghroem.databinding.FragmentPlaylistBinding
import com.ghroem.domain.model.Playlist
import com.ghroem.domain.model.Song
import com.ghroem.presentation.adapter.SongAdapter
import com.ghroem.presentation.base.BaseFragment
import com.ghroem.presentation.ui.dialog.AddSongToPlaylistDialog
import com.ghroem.presentation.ui.main.MainActivity
import com.ghroem.presentation.utils.GlideUtils
import com.ghroem.presentation.utils.enums.ScreenType
import com.ghroem.presentation.utils.ext.setPaddingStatusBar
import com.ghroem.presentation.utils.setOnSafeClickListener
import com.ghroem.presentation.viewmodel.PlaylistViewModel
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

@AndroidEntryPoint
class PlaylistFragment : BaseFragment<FragmentPlaylistBinding>() {
	
	private val args: PlaylistFragmentArgs by navArgs()
	private val playlistViewModel: PlaylistViewModel by activityViewModels()
	private var currentPlaylist: Playlist? = null
	private lateinit var songAdapter: SongAdapter
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		Log.i("CHECK_INIT", "onCreate: $this")
	}
	
	override fun getContentLayout(): Int = R.layout.fragment_playlist
	
	override fun initView() {
		binding.root.setPaddingStatusBar()
		getDataFromNavArgs()
		setupRecyclerView()
		currentPlaylist?.let { playlistViewModel.getOrUpdateSongsInPlaylist(it) }
		setupToolbar()
	}
	
	override fun initListener() {
		binding.ivBack.setOnSafeClickListener {
			findNavController().popBackStack()
		}
		
		binding.tvAddSong.setOnSafeClickListener {
			handleOnClickAddSong()
		}
	}
	
	override fun observerLiveData() {
		playlistViewModel.songs.observe(this) { songs ->
			updateNumberOfSongs(songs.size)
			handlePlaylistImage(songs)
			handleEnableCollapsingToolbar(songs.size)
			songAdapter.submitList(songs)
			Log.d("CHECK_SONGS", "songs: $this $songs")
			Log.d("CHECK_SONGS", "currentPlaylist: $this $currentPlaylist")
		}
	}
	
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		Log.i("CHECK_PLAYLIST_SCREEN", "onCreateView: $this")
		return super.onCreateView(inflater, container, savedInstanceState)
	}
	
	override fun onDestroyView() {
		Log.i("CHECK_PLAYLIST_SCREEN", "onDestroyView: $this")
		playlistViewModel.onCleared()
		super.onDestroyView()
	}
	
	override fun onDestroy() {
		
		Log.i("CHECK_PLAYLIST_SCREEN", "onDestroy: $this")
		super.onDestroy()
	}
	
	private fun getDataFromNavArgs() {
		currentPlaylist = args.playlist
	}
	
	private fun setupRecyclerView() {
		songAdapter = SongAdapter(
			screenType = ScreenType.PLAYLIST,
			onClickItemSong = { song ->
				startMusic(song)
			},
			onLongClickItemSong = { song, view ->
				PopupMenu(requireContext(), view).apply {
					menuInflater.inflate(R.menu.menu_remove_song, menu)
					setOnMenuItemClickListener {
						when (it.itemId) {
							R.id.item_remove -> {
								currentPlaylist?.let { playlist ->
									playlistViewModel.removeSongFromPlaylist(song, playlist.id)
									playlistViewModel.getOrUpdateSongsInPlaylist(playlist)
								}
								return@setOnMenuItemClickListener true
							}
							
							else -> {
								return@setOnMenuItemClickListener true
							}
						}
					}
					show()
				}
			}
		)
		
		binding.rcvListSong.apply {
			adapter = songAdapter
			layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
		}
	}
	
	private fun startMusic(song: Song) {
		(requireActivity() as MainActivity).startMusicService(song)
		//val l = (requireActivity() as MainActivity).musicService?.songList
		
	}
	
	private fun updateNumberOfSongs(numberOfSongs: Int) {
		val pluralResource =
			if (numberOfSongs > 1) R.string.songs_with_placeholder else R.string.song_with_placeholder
		binding.tvNumberOfSongs.text = getString(pluralResource, numberOfSongs)
	}
	
	private fun handlePlaylistImage(songs: List<Song>) {
		if (songs.isNotEmpty()) {
			GlideUtils.loadImageFromUrl(binding.ivPlaylist, songs[0].albumArt)
		} else {
			//GlideUtils.loadImageFromResource(binding.ivPlaylist, R.drawable.ic_favorite_on)
			binding.ivPlaylist.setImageResource(R.drawable.ic_favorite_on)
		}
	}
	
	private fun handleEnableCollapsingToolbar(numberOfSongs: Int) {
		val params = binding.collapsingToolbar.layoutParams as AppBarLayout.LayoutParams
		params.scrollFlags = if (numberOfSongs > 0) {
			AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
		} else {
			AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL
		}
		binding.collapsingToolbar.layoutParams = params
	}
	
	private fun handleOnClickAddSong() {
		activity?.supportFragmentManager?.let { fragmentManager ->
			currentPlaylist?.id?.let { id ->
				playlistViewModel.getPlaylistById(id)?.let { playlist ->
					AddSongToPlaylistDialog(playlist).apply {
						show(fragmentManager, this.tag)
					}
				}
			}
		}
	}
	
	private fun setupToolbar() {
		binding.tvToolbar.text = currentPlaylist?.name
		binding.tvPlaylistName.text = currentPlaylist?.name
		binding.appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
			Log.d("CHECK_TOOLBAR", "totalScrollRange: ${appBarLayout.totalScrollRange}")
			val alpha =
				((abs(verticalOffset.toFloat()) / (appBarLayout.totalScrollRange / 0.5).toFloat()))
			Log.i("CHECK_TOOLBAR", "totalScrollRange: ${appBarLayout.totalScrollRange}")
			Log.i("CHECK_TOOLBAR", "alpha: $alpha")
			binding.layoutAddSong.alpha = 1 - alpha
			if (binding.layoutAddSong.alpha == alpha) {
				binding.tvToolbar.alpha = 1F
			} else {
				binding.tvToolbar.alpha = 0F
			}
			
		}
	}
	
}
