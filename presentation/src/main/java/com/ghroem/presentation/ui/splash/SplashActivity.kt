package com.ghroem.presentation.ui.splash

import android.Manifest
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.ghroem.R
import com.ghroem.databinding.ActivitySplashBinding
import com.ghroem.presentation.base.BaseActivity
import com.ghroem.presentation.ui.main.MainActivity
import com.ghroem.presentation.utils.AnimationUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
	
	companion object {
		private const val PERMISSION_REQUEST_CODE = 123
	}
	
	override fun getContentLayout(): Int = R.layout.activity_splash
	
	override fun initView() {
		requestPermission()
	}
	
	override fun initListener() {
	
	}
	
	override fun observerLiveData() {
	
	}
	
	private fun requestPermission() {
		val permission =
			if (Build.VERSION.SDK_INT > 32) Manifest.permission.READ_MEDIA_AUDIO else Manifest.permission.READ_EXTERNAL_STORAGE
		
		if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
		) {
			navigateToMainActivity()
		} else {
			requestPermissions(arrayOf(permission), PERMISSION_REQUEST_CODE)
		}
	}
	
	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<String>,
		grantResults: IntArray
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		if (requestCode == PERMISSION_REQUEST_CODE) {
			if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				navigateToMainActivity()
			} else {
				Toast.makeText(this, getString(R.string.denied_to_fetch_songs), Toast.LENGTH_SHORT)
					.show()
			}
		}
	}
	
	private fun navigateToMainActivity() {
		val scale1 = AnimationUtils.createScaleAnimator(this, binding.ivSplash, 1.0f)
		val scale2 = AnimationUtils.createScaleAnimator(this, binding.ivSplash, 0.6f)
		val scale3 = AnimationUtils.createScaleAnimator(this, binding.ivSplash, 1.0f)
		val scale4 = AnimationUtils.createScaleAnimator(this, binding.ivSplash, 0.95f)
		
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
					startActivity(Intent(applicationContext, MainActivity::class.java))
					finish()
				}
			}
		}
	}
	
}

