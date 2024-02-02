package com.ghroem.presentation.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.ghroem.presentation.utils.StatusBarUtils

abstract class BaseActivity<BINDING : ViewDataBinding> : AppCompatActivity() {
	lateinit var binding: BINDING
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, getContentLayout())
		StatusBarUtils.makeStatusBarTransparentAndDark(this)
		initView()
		initListener()
		observerLiveData()
	}
	
	abstract fun getContentLayout(): Int
	
	abstract fun initView()
	
	abstract fun initListener()
	
	abstract fun observerLiveData()
	
}
