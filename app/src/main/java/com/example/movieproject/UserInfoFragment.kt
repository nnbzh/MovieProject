package com.example.movieproject

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class UserInfoFragment : Fragment() {

private lateinit var username: TextView
private lateinit var sharedPreferences: SharedPreferences

override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
): View? {
    return inflater.inflate(R.layout.account_fragment, container, false)
}

override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    bindViews(view)
}

private fun bindViews(view: View) = with(view) {

    username = findViewById(R.id.tv_username)

    sharedPreferences = activity?.getSharedPreferences(
        getString(R.string.preference_file), Context.MODE_PRIVATE
    )!!

    if (sharedPreferences.contains("username"))
        username.text = sharedPreferences.getString("username", "null")
}
}