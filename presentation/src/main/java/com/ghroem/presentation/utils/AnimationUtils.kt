package com.ghroem.presentation.utils

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.view.View
import com.ghroem.R

object AnimationUtils {
    fun createScaleAnimator(
        context: Context,
        view: View,
        scaleFactor: Float
    ): ObjectAnimator {
        return ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat(context.getString(R.string.scaleX), scaleFactor),
            PropertyValuesHolder.ofFloat(context.getString(R.string.scaleY), scaleFactor)
        )
    }

    fun animateFollowX(view: View, duration: Long, valueX: Float) {
        view.animate()
            .translationX(valueX)
            .duration = duration
    }

    fun animateFollowY(view: View, duration: Long, valueY: Float) {
        view.animate()
            .translationY(valueY)
            .duration = duration
    }
}
