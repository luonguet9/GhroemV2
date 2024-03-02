package com.ghroem.data.data_source.local.pref

import android.content.Context
import android.content.SharedPreferences
import kotlin.reflect.KProperty

abstract class Preferences(context: Context) {
	private val prefs: SharedPreferences by lazy {
		context.getSharedPreferences(MY_PREF, Context.MODE_PRIVATE)
	}
	
	abstract class PrefDelegate<T>(val prefKey: String) {
		abstract operator fun getValue(thisRef: Any?, property: KProperty<*>): T
		abstract operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T)
	}
	
	@Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
	inner class GenericPrefDelegate<T>(
		prefKey: String,
		private val defaultValue: T,
	) : PrefDelegate<T>(prefKey) {
		override fun getValue(thisRef: Any?, property: KProperty<*>): T {
			return when (defaultValue) {
				is String -> prefs.getString(prefKey, defaultValue as String)
				is Int -> prefs.getInt(prefKey, defaultValue as Int)
				is Float -> prefs.getFloat(prefKey, defaultValue as Float)
				is Boolean -> prefs.getBoolean(prefKey, defaultValue as Boolean)
				is Long -> prefs.getLong(prefKey, defaultValue as Long)
				is Set<*> -> prefs.getStringSet(prefKey, defaultValue as Set<String>)
				else -> throw IllegalArgumentException("Unsupported type")
			} as T
		}
		
		override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
			with(prefs.edit()) {
				when (value) {
					is String -> putString(prefKey, value)
					is Int -> putInt(prefKey, value)
					is Float -> putFloat(prefKey, value)
					is Boolean -> putBoolean(prefKey, value)
					is Long -> putLong(prefKey, value)
					is Set<*> -> putStringSet(prefKey, value as Set<String>)
					else -> throw IllegalArgumentException("Unsupported type")
				}
				apply()
			}
		}
	}
	
	fun stringPref(prefKey: String, defaultValue: String = "") =
		GenericPrefDelegate(prefKey, defaultValue)
	
	fun intPref(prefKey: String, defaultValue: Int = 0) =
		GenericPrefDelegate(prefKey, defaultValue)
	
	fun floatPref(prefKey: String, defaultValue: Float = 0f) =
		GenericPrefDelegate(prefKey, defaultValue)
	
	fun booleanPref(prefKey: String, defaultValue: Boolean = false) =
		GenericPrefDelegate(prefKey, defaultValue)
	
	fun longPref(prefKey: String, defaultValue: Long = 0L) =
		GenericPrefDelegate(prefKey, defaultValue)
	
	fun stringSetPref(prefKey: String, defaultValue: Set<String> = HashSet()) =
		GenericPrefDelegate(prefKey, defaultValue)
	
	companion object {
		private const val MY_PREF = "my_preferences"
	}
}
