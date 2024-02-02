package com.ghroem.data.data_source.local.realm

import android.content.Context
import io.realm.*

class RealmManager(context: Context) {
	private var realm: Realm
	
	init {
		Realm.init(context)
		val config = RealmConfiguration.Builder()
			.schemaVersion(1)
			.migration(MyMigration())
			.build()
		Realm.setDefaultConfiguration(config)
		realm = Realm.getDefaultInstance()
	}
	
	fun clear() {
		val configs = realm.configuration
		realm.close()
		Realm.deleteRealm(configs)
	}
	
	fun executeRealmTransaction(callback: (() -> Unit)? = null) {
		realm = Realm.getDefaultInstance()
		realm.beginTransaction()
		callback?.invoke()
		realm.commitTransaction()
		realm.close()
	}
	
	fun <T : RealmObject?> delete(item: T) {
		realm = Realm.getDefaultInstance()
		realm.beginTransaction()
		item?.deleteFromRealm()
		realm.commitTransaction()
		realm.close()
	}
	
	fun <T : RealmObject?> save(item: T, callback: (() -> Unit)? = null) {
		realm = Realm.getDefaultInstance()
		realm.beginTransaction()
		callback?.invoke()
		realm.copyToRealmOrUpdate(item)
		realm.commitTransaction()
		realm.close()
	}
	
	fun <T : RealmObject?> findFirst(type: Class<T>, vararg conditions: Pair<String, Any>): T? {
		val query = realm.where(type)
		for (condition in conditions) {
			val key = condition.first
			when (val value = condition.second) {
				is Int -> query.equalTo(key, value)
				is String -> query.equalTo(key, value)
				is Double -> query.equalTo(key, value)
				is Float -> query.equalTo(key, value)
				is Boolean -> query.equalTo(key, value)
				is Long -> query.equalTo(key, value)
				else -> throw IllegalArgumentException("Unsupported value type")
			}
		}
		return query.findFirst()
	}
	
	fun <T : RealmObject?> findAll(type: Class<T>): RealmResults<T>? {
		return realm.where(type).findAll()
	}
	
	fun <T : RealmObject?> findAllWithConditions(
		type: Class<T>,
		key: String,
		value: Any,
		field: String,
		sort: Sort = Sort.ASCENDING,
	): RealmResults<T>? {
		val query = realm.where(type)
		when (value) {
			is Int -> query.equalTo(key, value)
			is String -> query.equalTo(key, value)
			is Double -> query.equalTo(key, value)
			is Float -> query.equalTo(key, value)
			is Boolean -> query.equalTo(key, value)
			is Long -> query.equalTo(key, value)
			else -> throw IllegalArgumentException("Unsupported value type")
		}
		return query.sort(field, sort).findAll()
	}
	
	fun <T : RealmObject?> findAllSorted(
		type: Class<T>,
		field: String,
		sort: Sort
	): RealmResults<*>? {
		realm = Realm.getDefaultInstance()
		return realm.where(type).findAll().sort(field, sort)
	}
	
	fun <T : RealmObject?> findAllAsync(type: Class<T>): RealmResults<*>? {
		realm = Realm.getDefaultInstance()
		return realm.where(type).findAllAsync()
	}
	
	fun <T : RealmObject?> findAllLimitAsync(type: Class<T>, limit: Long = 3): RealmResults<*>? {
		realm = Realm.getDefaultInstance()
		return realm.where(type).limit(limit).findAllAsync()
	}
	
	fun <T : RealmObject?> findAllSortedAsync(
		type: Class<T>,
		field: String,
		sort: Sort,
		limit: Long = 50
	): RealmResults<*>? {
		realm = Realm.getDefaultInstance()
		return realm.where(type).sort(field, sort).limit(limit).findAllAsync()
	}
	
	fun <T : RealmObject?> findAllSorted(
		type: Class<T>,
		key: String,
		value: Boolean,
		field: String,
		sort: Sort
	): RealmResults<*>? {
		realm = Realm.getDefaultInstance()
		return realm.where(type).equalTo(key, value).findAllAsync().sort(field, sort)
	}
	
	fun <T : RealmObject?> findAllAsync(
		type: Class<T>,
		key: String,
		value: Boolean
	): RealmResults<*>? {
		realm = Realm.getDefaultInstance()
		return realm.where(type).equalTo(key, value).findAll()
	}
	
	fun <T : RealmObject?> findAllAsync(
		type: Class<T>,
		key: String,
		value: Boolean,
		key2: String,
		value2: Boolean,
	): RealmResults<*>? {
		realm = Realm.getDefaultInstance()
		return realm.where(type).equalTo(key, value).equalTo(key2, value2).findAll()
	}
	
	fun <T : RealmObject?> findAllEqualSortedLimitAsync(
		type: Class<T>,
		key: String,
		value: Boolean,
		key2: String,
		value2: Boolean,
		field: String,
		sort: Sort,
		limit: Long = 50
	): RealmResults<*>? {
		realm = Realm.getDefaultInstance()
		return realm.where(type).equalTo(key, value).equalTo(key2, value2).sort(field, sort)
			.limit(limit).findAll()
	}
	
	fun <T : RealmObject?> findOneAsync(type: Class<T>, key: String, value: Int): T {
		realm = Realm.getDefaultInstance()
		return realm.where(type).equalTo(key, value).findFirstAsync()
	}
	
	fun <T : RealmObject?> findAllSortedAsync(
		type: Class<T>,
		key: String,
		value: String
	): RealmResults<*>? {
		realm = Realm.getDefaultInstance()
		return realm.where(type).contains(key, value, Case.INSENSITIVE).findAllAsync()
			.sort("name", Sort.ASCENDING)
	}
	
	fun <T : RealmObject?> findAllSortedAsync(
		type: Class<T>,
		key1: String,
		value1: String,
		key2: String,
		value2: Boolean
	): RealmResults<*>? {
		return realm.where(type).equalTo(key2, value2).contains(key1, value1, Case.INSENSITIVE)
			.findAllAsync().sort("name", Sort.ASCENDING)
	}
	
	fun <T : RealmObject?> getMaxId(type: Class<T>, paramId: String): Number? {
		realm = Realm.getDefaultInstance()
		return realm.where(type).max(paramId)
	}
	
	fun <T : RealmObject?> clear(type: Class<T>) {
		realm = Realm.getDefaultInstance()
		realm.beginTransaction()
		realm.delete(type)
		realm.commitTransaction()
		realm.close()
	}
	
	private class MyMigration : RealmMigration {
		override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
		}
	}
}
