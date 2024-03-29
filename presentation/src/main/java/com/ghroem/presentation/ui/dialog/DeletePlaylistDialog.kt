package com.ghroem.presentation.ui.dialog

import android.view.Gravity
import com.ghroem.R
import com.ghroem.databinding.DialogDeletePlaylistBinding
import com.ghroem.presentation.base.BaseDialog
import com.ghroem.presentation.utils.setOnSafeClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeletePlaylistDialog : BaseDialog<DialogDeletePlaylistBinding>() {
	
	private var deletePlaylistCallback: (() -> Unit)? = null
	
	override fun getLayoutResource(): Int = R.layout.dialog_delete_playlist
	
	override fun getGravityForDialog(): Int = Gravity.CENTER
	
	override fun initView() {
	
	}
	
	override fun initListener() {
		binding.btCancel.setOnSafeClickListener {
			dismiss()
		}
		
		binding.btDelete.setOnSafeClickListener {
			deletePlaylistCallback?.invoke()
			dismiss()
		}
	}
	
	fun deletePlaylistCallback(callback: (() -> Unit)? = null) {
		deletePlaylistCallback = callback
	}
}
