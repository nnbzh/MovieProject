package com.example.movieproject.view.Fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.movieproject.view.Activities.LoginActivity
import com.example.movieproject.R
import com.example.movieproject.view_model.ProfileViewModel
import com.example.movieproject.view_model.ViewModelProviderFactory

class UserInfoFragment : Fragment() {

    private lateinit var username: TextView
    private lateinit var btnLogout: Button
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var viewModelProviderFactory: ViewModelProviderFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.account_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        bindViews(view)

        btnLogout.setOnClickListener {
            var editor: SharedPreferences.Editor = profileViewModel.shared.value!!.edit()
            editor.clear()
            editor.commit()
            requireActivity().run {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }
        private fun initViewModel() {
            viewModelProviderFactory = ViewModelProviderFactory(context = requireContext())
            profileViewModel =
                ViewModelProvider(this, viewModelProviderFactory).get(ProfileViewModel::class.java)
        }


        private fun bindViews(view: View) = with(view) {
            username = findViewById(R.id.tv_username)
            btnLogout = findViewById(R.id.btnLogout)
            username.text = profileViewModel.liveData.value
        }

    }