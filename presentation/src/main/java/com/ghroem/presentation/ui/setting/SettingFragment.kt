package com.ghroem.presentation.ui.setting

import com.ghroem.R
import com.ghroem.databinding.FragmentSettingBinding
import com.ghroem.presentation.base.BaseFragment
import com.ghroem.presentation.utils.ext.setPaddingStatusBar

class SettingFragment : BaseFragment<FragmentSettingBinding>() {
	
	override fun getContentLayout(): Int = R.layout.fragment_setting
	
	override fun initView() {
		binding.root.setPaddingStatusBar()
	}
	
	override fun initListener() {
	
	}
	
	override fun observerLiveData() {
	
	}
	
}
