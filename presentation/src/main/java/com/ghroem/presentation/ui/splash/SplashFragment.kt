package com.ghroem.presentation.ui.splash

import android.Manifest
import android.animation.AnimatorSet
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.ghroem.R
import com.ghroem.databinding.FragmentSplashBinding
import com.ghroem.presentation.base.BaseFragment
import com.ghroem.presentation.utils.AnimationUtils
import com.ghroem.presentation.utils.ext.setPaddingStatusBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>() {
	
	private val requestPermissionLauncher =
		registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
			if (isGranted) {
				navigateToHome()
			} else {
				Toast.makeText(
					requireContext(),
					getString(R.string.denied_to_fetch_songs),
					Toast.LENGTH_SHORT
				).show()
			}
		}
	
	override fun getContentLayout(): Int = R.layout.fragment_splash
	
	override fun initView() {
		binding.root.setPaddingStatusBar()
		requestPermission()
	}
	
	override fun initListener() {
	
	}
	
	override fun observerLiveData() {
	
	}
	
	private fun requestPermission() {
		val permission =
			if (Build.VERSION.SDK_INT > 32) Manifest.permission.READ_MEDIA_AUDIO else Manifest.permission.READ_EXTERNAL_STORAGE
		
		if (ContextCompat.checkSelfPermission(requireContext(), permission) == PERMISSION_GRANTED) {
			navigateToHome()
		} else {
			requestPermissionLauncher.launch(permission)
		}
	}
	
	private fun navigateToHome() {
		val scale1 = AnimationUtils.createScaleAnimator(requireContext(), binding.ivSplash, 1.0f)
		val scale2 = AnimationUtils.createScaleAnimator(requireContext(), binding.ivSplash, 0.6f)
		val scale3 = AnimationUtils.createScaleAnimator(requireContext(), binding.ivSplash, 1.0f)
		val scale4 = AnimationUtils.createScaleAnimator(requireContext(), binding.ivSplash, 0.95f)
		
		AnimatorSet().apply {
			playSequentially(scale1, scale2, scale3, scale4)
			duration = 300
			start()
			doOnEnd {
				AnimationUtils.animateFollowY(binding.ivSplash, 200, 100f)
				lifecycleScope.launch {
					delay(100)
					AnimationUtils.animateFollowY(binding.ivSplash, 1000, -2000f)
					delay(1000)
					val navOptions = NavOptions.Builder()
						.setPopUpTo(R.id.splashFragment, true)
						.build()
					findNavController().navigate(
						R.id.action_splashFragment_to_home,
						null,
						navOptions
					)
				}
			}
		}
	}
	
}
