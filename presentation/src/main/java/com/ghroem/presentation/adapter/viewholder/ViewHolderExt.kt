package com.ghroem.presentation.adapter.viewholder

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.ghroem.R

inline fun <reified T : ViewBinding> ViewGroup.viewBinding(
	crossinline bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> T
): T {
	return bindingInflater.invoke(LayoutInflater.from(context), this, false)
}

fun RecyclerView.ViewHolder.setupUIItemPlaylist(view: View, position: Int) {
	val screenWidth = Resources.getSystem().displayMetrics.widthPixels
	val margin16 = view.context.resources.getDimensionPixelOffset(R.dimen.dp24)
	val itemWidth = (screenWidth - 3 * margin16) / 2
	
	when (val layoutParams = view.layoutParams) {
		is GridLayoutManager.LayoutParams -> {
			layoutParams.marginEnd = margin16
			
			view.post {
				layoutParams.width = itemWidth
				//layoutParams.height = (itemWidth * 2.5 / 2).toInt()
				view.requestLayout()
			}
		}
		
	}
}
