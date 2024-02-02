package com.ghroem.presentation.ui.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.ghroem.databinding.LayoutBottomSheetDetailBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MusicPlayerDialog : BottomSheetDialogFragment() {
	
	private lateinit var binding: LayoutBottomSheetDetailBinding
	private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = LayoutBottomSheetDetailBinding.inflate(inflater, container, false)
		return binding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initView()
		initListener()
	}
	
	private fun initView() {
		bottomSheetBehavior = BottomSheetBehavior.from(binding.layoutContainer.parent as View)
		bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
		bottomSheetBehavior.peekHeight = resources.displayMetrics.heightPixels
		//binding.layoutContainer.minimumHeight = resources.displayMetrics.heightPixels
		//binding.toolBar.setPaddingStatusBar()
		bottomSheetBehavior.addBottomSheetCallback(object :
			BottomSheetBehavior.BottomSheetCallback() {
			override fun onStateChanged(bottomSheet: View, newState: Int) {
				when (newState) {
					BottomSheetBehavior.STATE_EXPANDED -> {
//						handleUIBottomSheetDetail()
//						updateRealTimeSeekbar()
						Log.i("MusicPlayerDialog", "STATE_EXPANDED: ")
					}
					BottomSheetBehavior.STATE_COLLAPSED -> {
//						handleUIBottomSheetDetail()
//						updateRealTimeSeekbar()
						Log.i("MusicPlayerDialog", "STATE_COLLAPSED: ")
					}
					else -> return
				}
			}
			
			override fun onSlide(bottomSheet: View, slideOffset: Float) {
//				handleBackgroundItemSongDetail(slideOffset)
				Log.i("CHECK_DETAIL", "onSlide: ")
			}
		})
		
	}
	
	private fun initListener() {
		binding.ivDown.setOnClickListener {
			dismiss()
		}
	}
	
	fun show(fragmentManager: FragmentManager) {
		show(fragmentManager, TAG_NAME)
	}
	
	companion object {
		private const val TAG_NAME = "BottomSheetExercise"
	}
}
