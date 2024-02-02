package com.ghroem.presentation.ui.dialog

import android.view.Gravity
import com.ghroem.R
import com.ghroem.databinding.DialogCreateNewPlaylistBinding
import com.ghroem.presentation.base.BaseDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateNewPlaylistDialog : BaseDialog<DialogCreateNewPlaylistBinding>() {
	
	private var addPlaylistCallback: ((name: String) -> Unit)? = null
	
	override fun getLayoutResource(): Int = R.layout.dialog_create_new_playlist
	
	override fun getGravityForDialog(): Int = Gravity.CENTER
	
	override fun initView() {
	
	}
	
	override fun initListener() {
		binding.btCancel.setOnClickListener {
			dismiss()
		}
		
		binding.btCreate.setOnClickListener {
			val name = binding.edtNamePlaylist.text.toString().trim()
			addPlaylistCallback?.invoke(name)
			dismiss()
		}
	}
	
	fun addPlaylistCallback(callback: ((name: String) -> Unit)? = null) {
		addPlaylistCallback = callback
	}
}
