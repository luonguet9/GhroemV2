package com.ghroem.presentation.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.widget.ImageView
import androidx.palette.graphics.Palette
import java.text.SimpleDateFormat
import java.util.Locale

fun getGradientBackground(resource: Any): GradientDrawable {
	val bitmap = when (resource) {
		is ImageView -> (resource.drawable as BitmapDrawable).bitmap
		is Bitmap -> resource
		else -> throw IllegalArgumentException("Unsupported resource type")
	}
	
	val palette = Palette.from(bitmap).generate()
	
	val swatches = palette.swatches.sortedByDescending { it.population }
	
	val dominantColor = swatches.getOrNull(0)?.rgb ?: Color.GRAY
	val secondDominantColor = swatches.getOrNull(1)?.rgb ?: Color.GRAY
	
	// Create a list of colors with their corresponding brightness
	val colorsWithBrightness = listOf(
		Pair(dominantColor, getBrightness(dominantColor)),
		Pair(secondDominantColor, getBrightness(secondDominantColor))
	)
	
	// Sort the list by brightness in ascending order
	val sortedColors = colorsWithBrightness.sortedBy { it.first }
	
	// Extract the two colors with the lowest brightness
	val colors = intArrayOf(sortedColors[0].first, sortedColors[1].first)
	
	return GradientDrawable(
		GradientDrawable.Orientation.TOP_BOTTOM,
		colors
	)
}

fun getBrightness(color: Int): Double {
	val red = Color.red(color)
	val green = Color.green(color)
	val blue = Color.blue(color)
	return (0.299 * red + 0.587 * green + 0.114 * blue)
}


fun timeFormatter(): SimpleDateFormat {
	return SimpleDateFormat("mm:ss", Locale.getDefault())
}


