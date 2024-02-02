package com.ghroem.presentation.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.ghroem.R

object GlideUtils {
    fun loadImageFromUrl(
        image: ImageView,
        url: Any?,
        options: RequestOptions? = null,
        placeholderResId: Int? = R.drawable.spotify_blue
    ) {
        val glideRequest = Glide.with(image.context)
            .load(url)
        
        options?.let { glideRequest.apply(it) }
        placeholderResId?.let { glideRequest.placeholder(it) }
        
        glideRequest.into(image)
    }
    
    fun loadImageFromResource(imageView: ImageView, resources: Int) {
        Glide.with(imageView.context)
            .load(resources)
            .placeholder(R.drawable.spotify_blue)
            .error(R.drawable.spotify_blue)
            .into(imageView)
    }

    fun loadImageFromUrlWithCallback(
        context: Context,
        url: String?,
        onFinished: (Bitmap) -> Unit
    ) {
        Glide.with(context)
            .asBitmap()
            .load(url)
            .placeholder(R.drawable.spotify_blue)
            .error(R.drawable.spotify_blue)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    onFinished(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    val bitmap = (errorDrawable as BitmapDrawable).bitmap
                    onFinished(bitmap)
                }

            })
    }

    fun loadImageFromUrlWithCallback(
        context: Context,
        url: String?,
        options: RequestOptions? = null,
        onSuccess: (Bitmap) -> Unit,
        onFailed: (Bitmap) -> Unit,
    ) {
        val glideRequest = Glide.with(context)
            .asBitmap()
            .load(url)
            .placeholder(R.drawable.spotify_blue)
            .error(R.drawable.spotify_blue)
    
        options?.let { glideRequest.apply(it) }
    
        glideRequest.into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                onSuccess(resource)
            }
        
            override fun onLoadCleared(placeholder: Drawable?) {}
        
            override fun onLoadFailed(errorDrawable: Drawable?) {
                super.onLoadFailed(errorDrawable)
                val bitmap = (errorDrawable as BitmapDrawable).bitmap
                onFailed(bitmap)
            }
        })
    }

}
