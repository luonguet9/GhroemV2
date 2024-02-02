package com.ghroem.presentation.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.WindowManager

object StatusBarUtils {
    fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId =
            context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }
    
    fun makeStatusBarTransparentAndDark(activity: Activity, isLightMode: Boolean = true) {
        var systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        if (isLightMode) {
            systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        activity.window.decorView.systemUiVisibility = systemUiVisibility
        setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false, activity)
        activity.window.statusBarColor = Color.TRANSPARENT
        activity.window.navigationBarColor = Color.TRANSPARENT
    }
    
    fun makeStatusBarTransparentDialog(dialog: Dialog, isLightMode: Boolean = true) {
/*        var systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        if (isLightMode) {
            systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        //dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        dialog.window?.decorView?.systemUiVisibility = systemUiVisibility
        setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false, dialog)
        dialog.window?.statusBarColor = Color.TRANSPARENT
        dialog.window?.navigationBarColor = Color.TRANSPARENT*/
 /*       dialog.window?.decorView?.systemUiVisibility =  View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)*/
        //setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, false, dialog)
        dialog.window?.statusBarColor = Color.TRANSPARENT
    }
    
    private fun setWindowFlag(bits: Int, on: Boolean, activity: Activity) {
        val win = activity.window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }
    
    private fun setWindowFlag(bits: Int, on: Boolean, dialog: Dialog) {
        val win = dialog.window
        val winParams = win?.attributes
        if (on) {
            winParams?.flags = winParams?.flags?.or(bits)
        } else {
            winParams?.flags = winParams?.flags?.and(bits.inv())
        }
        win?.attributes = winParams
    }
}
