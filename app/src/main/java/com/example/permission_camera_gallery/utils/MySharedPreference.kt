package com.example.permission_camera_gallery.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object MySharedPreference {
    private const val NAME = "my_catch_file"
    private const val MODE = Context.MODE_PRIVATE

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(NAME, MODE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }


    var contactList: ArrayList<String>
        get() = gsonStringtoList(sharedPreferences.getString("keyList", "[]")!!)
        set(value) = sharedPreferences.edit {
            it.putString("keyList", listToString(value))
        }

    fun gsonStringtoList(gsonString: String): ArrayList<String> {
        val type = object : TypeToken<ArrayList<String>>() {}.type
        return Gson().fromJson(gsonString, type)
    }

    fun listToString(list: ArrayList<String>): String {
        return Gson().toJson(list)
    }
}