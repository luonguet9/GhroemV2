package com.ghroem.presentation.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

abstract class BaseDialog<BINDING : ViewDataBinding> : DialogFragment() {
	lateinit var binding: BINDING
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		binding = DataBindingUtil.inflate(
			inflater, getLayoutResource(), container, false
		)
		configureDialog(dialog)
		Log.i("CHECK_DIALOG", "onCreateView: ")
		return binding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setFullSizeDialog()
		initView()
		initListener()
		Log.i("CHECK_DIALOG", "onViewCreated: ")
	}
	
	override fun show(fragmentManager: FragmentManager, tag: String?) {
		try {
			var transaction = fragmentManager.beginTransaction()
			val prevFragment = fragmentManager.findFragmentByTag(tag)
			prevFragment?.let { transaction.remove(it) }
			transaction.addToBackStack(null)
			transaction.commitAllowingStateLoss()
			//new transaction
			transaction = fragmentManager.beginTransaction()
			show(transaction, tag)
			Log.i("SHOW_DIALOG", "SHOWING:")
		} catch (ignored: IllegalStateException) {
			Log.e("SHOW_DIALOG", "IllegalStateException:" + ignored.message)
		}
	}
	
	@LayoutRes
	protected abstract fun getLayoutResource(): Int
	
	protected abstract fun getGravityForDialog(): Int
	
	protected abstract fun initView()
	
	protected abstract fun initListener()
	
	private fun configureDialog(dialog: Dialog?) {
		dialog?.window?.let { window ->
			with(window) {
				requestFeature(Window.FEATURE_NO_TITLE)
				setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
				setDimAmount(0.3f)
				attributes.gravity = getGravityForDialog()
				Log.i("CHECK_DIALOG", "configureDialog: ")
			}
		}
	}
	
	protected fun setFullSizeDialog() {
		dialog?.window?.setLayout(
			WindowManager.LayoutParams.MATCH_PARENT,
			WindowManager.LayoutParams.WRAP_CONTENT
		)
	}
	
	protected fun setFullScreenDialog() {
		dialog?.window?.setLayout(
			WindowManager.LayoutParams.MATCH_PARENT,
			WindowManager.LayoutParams.MATCH_PARENT
		)
	}
}
