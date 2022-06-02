package com.payu.baas.coreUI.model.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class PreferenceUtils private constructor(context: Context, fileName: String) {
    var editor: SharedPreferences.Editor
    var mPreferences: SharedPreferences
    var masterKey: MasterKey

    init {
        masterKey = MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
        mPreferences = EncryptedSharedPreferences.create(
            context,
            fileName, masterKey, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        editor = mPreferences.edit()
    }

    fun putString(key: String, value: String?) {
        editor.putString(key, value).apply()
    }

    fun getString(key: String):String? {
        return mPreferences.getString(key, null)
    }

    fun putInt(key: String, value: Int) {
        editor.putInt(key, value).apply()
    }

    fun getInt(key: String):Int {
        return mPreferences.getInt(key, -1)
    }

    fun removeKey(key: String) {
        editor.remove(key).apply()
    }
    fun containsKey(key: String):Boolean{
        return mPreferences.contains(key)
    }
    fun clearPref(){
        editor.clear()
        editor.commit()
    }

    companion object {
        @Volatile
        private var instance: PreferenceUtils? = null
        fun getInstance(context: Context, fileName: String): PreferenceUtils {
            val checkInstance = instance
            if (checkInstance != null) {
                return checkInstance
            }

            return synchronized(this) {
                val checkInstanceAgain = instance
                if (checkInstanceAgain != null) {
                    checkInstanceAgain
                } else {
                    val created = PreferenceUtils(context, fileName)
                    instance = created
                    created
                }
            }
        }
    }
}