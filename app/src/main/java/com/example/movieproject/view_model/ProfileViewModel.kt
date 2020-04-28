package com.example.movieproject.view_model

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.movieproject.R
import com.example.movieproject.view.Activities.LoginActivity

class ProfileViewModel(private val context: Context) : ViewModel() {

    val liveData = MutableLiveData<String>()
    val shared = MutableLiveData<SharedPreferences>()

    init {
        getUsername()
    }

    private fun getUsername() {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            context.getString(R.string.preference_file), Context.MODE_PRIVATE
        )
        if (sharedPreferences.contains(context.getString(R.string.username)))
            liveData.value =
                sharedPreferences.getString(
                    context.getString(R.string.username),
                    "null"
                )
        shared.value = sharedPreferences
    }



}