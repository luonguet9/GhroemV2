package com.ghroem.presentation.ui.main

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.ghroem.R
import com.ghroem.databinding.ActivityMainBinding
import com.ghroem.domain.model.Song
import com.ghroem.presentation.utils.enums.ActionMusic
import com.ghroem.presentation.adapter.DetailAdapter
import com.ghroem.presentation.base.BaseActivity
import com.ghroem.presentation.ui.home.HomeFragment
import com.ghroem.presentation.ui.library.LibraryFragment
import com.ghroem.presentation.ui.setting.SettingFragment
import com.ghroem.presentation.service.MusicService
import com.ghroem.presentation.utils.ACTION_MUSIC
import com.ghroem.presentation.viewmodel.MainViewModel
import com.ghroem.presentation.utils.SEND_ACTION_TO_ACTIVITY
import com.ghroem.presentation.utils.SONG_OBJECT
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
	
	/**Music Service*/
	var musicService: MusicService? = null
	private var isServiceConnected = false
	
	private val broadcastReceiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context?, intent: Intent?) {
			intent?.getIntExtra(ACTION_MUSIC, 0)?.let { action ->
				handleActionMusic(action)
			}
		}
	}
	
	private val serviceConnection = object : ServiceConnection {
		override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
			val binder = service as MusicService.MusicBinder
			musicService = binder.getService()
			isServiceConnected = true
			
			mainViewModel.songs.observe(this@MainActivity) {
				musicService?.let { musicService ->
					musicService.songs = it
					detailAdapter.submitList(it)
				}
			}
			
			handleUIBottomMediaPlayer()
			handleUIBottomSheetDetail()
			setUpSliderMusic()
		}
		
		override fun onServiceDisconnected(name: ComponentName?) {
			musicService = null
			isServiceConnected = false
		}
		
	}
	
	private lateinit var navController: NavController
	private lateinit var navHostFragment: NavHostFragment
	
	val mainViewModel: MainViewModel by viewModels()
	
	lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
	var isLoadImageSuccess = false
	
	/**Recycler View In Detail*/
	lateinit var detailAdapter: DetailAdapter
	lateinit var detailLayoutManager: LinearLayoutManager
	
	override fun getContentLayout(): Int = R.layout.activity_main
	
	override fun initView() {
		//binding.container.setPaddingStatusBar()
		setUpNavigationControllerAndBottomNavigation()
		registerReceiver()
		setUpBottomSheetDetail()
		setupRecyclerView()
		
	}
	
	override fun initListener() {
		handleOnClickBottomMediaPlayer()
		handleOnClickBottomSheetPlayer()
		handleOnBackPressed()
	}
	
	override fun observerLiveData() {
	
	}
	
	override fun onResume() {
		super.onResume()
		mainViewModel.getSongsFromStorage()
		if (isServiceConnected) {
			handleUIBottomMediaPlayer()
		}
	}
	
	override fun onDestroy() {
		super.onDestroy()
		LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
		if (isServiceConnected) {
			unbindService(serviceConnection)
			isServiceConnected = false
		}
		stopService(Intent(this, MusicService::class.java))
	}
	
	private fun setUpNavigationControllerAndBottomNavigation() {
		navHostFragment =
			supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
		navController = navHostFragment.navController
		// Setup with bottom navigation
		NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
		navController.addOnDestinationChangedListener { controller, destination, arguments ->
			if (destination.id != R.id.homeFragment && destination.id != R.id.libraryFragment && destination.id != R.id.settingFragment) {
				binding.bottomNavigation.visibility = View.GONE
			} else {
				binding.bottomNavigation.visibility = View.VISIBLE
			}
		}
		binding.bottomNavigation.setOnItemReselectedListener { menuItem ->
			val currentFragment = navHostFragment.childFragmentManager.fragments[0]
			when (menuItem.itemId) {
				R.id.home -> {
					if (currentFragment is HomeFragment) {
						currentFragment.handleReselectedHome()
					}
				}
				
				R.id.library -> {
					if (currentFragment is LibraryFragment) {
						currentFragment.handleReselectedLibrary()
					} else {
						navController.popBackStack()
					}
				}
			}
		}
	}
	
	fun startMusicService(song: Song) {
		val intent = Intent(this, MusicService::class.java).apply {
			putExtra(SONG_OBJECT, song)
		}
		startService(intent)
		bindService(intent, serviceConnection, BIND_AUTO_CREATE)
	}
	
	private fun handleActionMusic(action: Int) {
		when (action) {
			ActionMusic.START.action -> {
				handleUIBottomMediaPlayer()
				handleUIBottomSheetDetail()
			}
			
			ActionMusic.PAUSE.action -> {
				setStatusPlayOrPauseBottomMediaPlayer()
				setStatusPlayOrPauseBottomSheetDetail()
			}
			
			ActionMusic.RESUME.action -> {
				setStatusPlayOrPauseBottomMediaPlayer()
				setStatusPlayOrPauseBottomSheetDetail()
			}
			
			ActionMusic.NEXT.action -> {
				handleUIBottomMediaPlayer()
				handleUIBottomSheetDetail()
				scrollToCurrentSong()
			}
			
			ActionMusic.PREVIOUS.action -> {
				handleUIBottomMediaPlayer()
				handleUIBottomSheetDetail()
				scrollToCurrentSong()
			}
		}
	}
	
	private fun handleUIBottomMediaPlayer() {
		binding.bottomPlayer.root.visibility = View.VISIBLE
		handleSongInfoBottomPlayer()
		setStatusPlayOrPauseBottomMediaPlayer()
	}
	
	private fun setupRecyclerView() {
		PagerSnapHelper().attachToRecyclerView(binding.bottomSheetDetail.rcvDetail)
		detailAdapter = DetailAdapter()
		binding.bottomSheetDetail.rcvDetail.adapter = detailAdapter
		detailLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
		binding.bottomSheetDetail.rcvDetail.layoutManager = detailLayoutManager
		binding.bottomSheetDetail.rcvDetail.isNestedScrollingEnabled = false
		
		binding.bottomSheetDetail.rcvDetail.addOnScrollListener(object : OnScrollListener() {
			override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
				super.onScrollStateChanged(recyclerView, newState)
				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
					val currentPosition =
						detailLayoutManager.findFirstCompletelyVisibleItemPosition()
					
					if (currentPosition == RecyclerView.NO_POSITION) return
					
					when {
						currentPosition > getCurrentSongPosition() -> {
							sendActionToService(ActionMusic.NEXT.action)
						}
						
						currentPosition < getCurrentSongPosition() -> {
							sendActionToService(ActionMusic.PREVIOUS.action)
						}
					}
					onSwipe()
				}
			}
		})
	}
	
	fun handleUIBottomSheetDetail() {
		handleSongInfoBottomSheetDetail()
		setTimeTotal()
		setStatusPlayOrPauseBottomSheetDetail()
		setShuffleStatus()
		setRepeatStatus()
		setFavoriteStatus()
		Log.e("CHECK_FAVORITE", "handleUIBottomSheetDetail: ")
	}
	
	private fun registerReceiver() {
		LocalBroadcastManager.getInstance(applicationContext)
			.registerReceiver(broadcastReceiver, IntentFilter(SEND_ACTION_TO_ACTIVITY))
	}
	
	private fun handleOnBackPressed() {
		onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
					bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
				} else {
					when (navHostFragment.childFragmentManager.fragments[0]) {
						is HomeFragment -> {
							finish()
						}
						
						is LibraryFragment -> {
							navController.navigate(R.id.homeFragment)
						}
						
						is SettingFragment -> {
							navController.navigate(R.id.homeFragment)
						}
					}
				}
			}
		})
	}
}
