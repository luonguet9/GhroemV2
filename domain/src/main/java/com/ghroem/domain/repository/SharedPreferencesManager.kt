package com.ghroem.domain.repository

interface SharedPreferencesManager {
	fun put(key: String, data: Any)
	fun <T : Any> get(key: String, defaultValue: T): T
	fun clear()
	fun remove(key: String)
}
