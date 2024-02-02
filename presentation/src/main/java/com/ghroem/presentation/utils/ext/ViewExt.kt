package com.ghroem.presentation.utils.ext

import android.view.View
import com.ghroem.presentation.utils.StatusBarUtils

fun View.setPaddingStatusBar() {
	setPadding(0, StatusBarUtils.getStatusBarHeight(context), 0, 0)
}

