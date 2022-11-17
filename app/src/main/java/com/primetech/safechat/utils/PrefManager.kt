package com.primetech.safechat.utils

import android.content.Context
import android.content.SharedPreferences
import com.primetech.safechat.utils.Constants.Companion.USER_SESSION

class PrefManager(context: Context) {
    private val pref: SharedPreferences = context.getSharedPreferences(
        Constants.PREF_NAME,
        Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = pref.edit()


    // it store user encrypted email
    var session:String
        get() = pref.getString(USER_SESSION, "")!!
        set(value) {
            editor.putString(USER_SESSION,value)
            editor.apply()
            editor.commit()
        }

    fun logout(){
        editor.clear()
        editor.apply()
        editor.commit()
    }

}