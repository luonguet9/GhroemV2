package com.ghroem.data.data_source.local.pref

import android.content.Context
import android.content.SharedPreferences
import com.ghroem.domain.repository.SharedPreferencesManager

class SharedPreferencesManagerImpl(context: Context) : SharedPreferencesManager {
	private var sharedPreferences: SharedPreferences
	
	companion object {
		const val MY_PREF = "my_preferences"
	}
	
	init {
		sharedPreferences = context.getSharedPreferences(MY_PREF, Context.MODE_PRIVATE)
	}
	
	override fun put(key: String, data: Any) {
		with(sharedPreferences.edit()) {
			when (data) {
				is String -> putString(key, data)
				is Boolean -> putBoolean(key, data)
				is Float -> putFloat(key, data)
				is Int -> putInt(key, data)
				is Long -> putLong(key, data)
			}
			apply()
		}
	}
	
	/*	override fun get(key: String, defaultValue: Any): Any {
			return when (defaultValue) {
				is String -> sharedPreferences.getString(key, defaultValue) as String
				is Boolean -> sharedPreferences.getBoolean(key, defaultValue)
				is Float -> sharedPreferences.getFloat(key, defaultValue)
				is Int -> sharedPreferences.getInt(key, defaultValue) as Int
				is Long -> sharedPreferences.getLong(key, defaultValue)
				else -> throw IllegalArgumentException()
			}
		}*/
	
	override fun <T : Any> get(key: String, defaultValue: T): T {
		return when (defaultValue) {
			is String -> sharedPreferences.getString(key, defaultValue) as T
			is Boolean -> sharedPreferences.getBoolean(key, defaultValue) as T
			is Float -> sharedPreferences.getFloat(key, defaultValue) as T
			is Int -> sharedPreferences.getInt(key, defaultValue) as T
			is Long -> sharedPreferences.getLong(key, defaultValue) as T
			else -> throw IllegalArgumentException()
		}
	}
	
	override fun clear() {
		sharedPreferences.edit().clear().apply()
	}
	
	override fun remove(key: String) {
		sharedPreferences.edit().remove(key).apply()
	}
}
