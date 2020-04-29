package com.example.movieproject.view.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle

import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.movieproject.R
import com.example.movieproject.view_model.LoginViewModel
import com.example.movieproject.view_model.ViewModelProviderFactory
import com.google.firebase.messaging.FirebaseMessaging

class LoginActivity: AppCompatActivity() {

    private lateinit var wrongDataText: TextView
    private lateinit var signInButton: Button
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var registrationLink: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var viewModelProviderFactory: ViewModelProviderFactory
    private val topic = "movies"

    private val signUpUrl: String = "https://www.themoviedb.org/account/signup"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)
        subscribe()

        initViewModel()
        login()
        bindViews()
    }
    private fun subscribe(){
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
    }

    private fun initViewModel() {
        viewModelProviderFactory = ViewModelProviderFactory(context = this)
        loginViewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(LoginViewModel::class.java)
    }

    private fun bindViews() {
        username = findViewById(R.id.etUsername)
        password = findViewById(R.id.etPassword)
        signInButton = findViewById(R.id.btnSignIn)
        wrongDataText = findViewById(R.id.tvWrongData)
        registrationLink = findViewById(R.id.tvRegister)

        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.GONE
        wrongDataText.text = ""

        registrationLink.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(signUpUrl))
            startActivity(browserIntent)
        }

        signInButton.setOnClickListener {

            loginViewModel.createTokenRequest(username.text.toString(), password.text.toString())
        }
    }
    private fun login() {
        loginViewModel.liveData.observe(this, Observer { result ->
            when (result) {
                is LoginViewModel.State.ShowLoading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is LoginViewModel.State.HideLoading -> {
                    progressBar.visibility = View.GONE
                }
                is LoginViewModel.State.FailedLoading -> {
                    Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT)
                        .show()
                }
                is LoginViewModel.State.WrongDataProvided -> {
                    wrongDataText.text = getString(R.string.wrong_data)
                }
                is LoginViewModel.State.Result -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        })
    }

}