package com.ghroem.presentation.ui.dialog

import android.view.Gravity
import com.ghroem.R
import com.ghroem.databinding.DialogRenamePlaylistBinding
import com.ghroem.presentation.base.BaseDialog
import com.ghroem.presentation.utils.setOnSafeClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RenamePlaylistDialog : BaseDialog<DialogRenamePlaylistBinding>() {
	
	private var renamePlaylistCallback: ((name: String) -> Unit)? = null
	
	override fun getLayoutResource(): Int = R.layout.dialog_rename_playlist
	
	override fun getGravityForDialog(): Int = Gravity.CENTER
	
	override fun initView() {
	
	}
	
	override fun initListener() {
		binding.btCancel.setOnSafeClickListener {
			dismiss()
		}
		
		binding.btRename.setOnSafeClickListener {
			val name = binding.edtNamePlaylist.text.toString().trim()
			renamePlaylistCallback?.invoke(name)
			dismiss()
		}
	}
	
	fun renamePlaylistCallback(callback: ((name: String) -> Unit)? = null) {
		renamePlaylistCallback = callback
	}
}
