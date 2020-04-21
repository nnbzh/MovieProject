package com.example.movieproject
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle

import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.movieproject.Account.LoginValidationData
import com.example.movieproject.Account.Session
import com.example.movieproject.Account.Token
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity: AppCompatActivity() {
    private lateinit var loginValidationData: LoginValidationData
    private lateinit var token: Token
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var wrongDataText: TextView
    private lateinit var signInButton: Button
    private lateinit var username: EditText
    private lateinit var receivedToken: String
    private lateinit var password: EditText
    private lateinit var registrationLink: TextView
    private lateinit var progressBar: ProgressBar

    private val signUpUrl: String = "https://www.themoviedb.org/account/signup"
    private var sessionId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)

        if (sharedPreferences.contains(getString(R.string.session_id))) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        bindViews()
    }

    private fun bindViews() {
        username = findViewById(R.id.etUsername)
        password = findViewById(R.id.etPassword)
        signInButton = findViewById(R.id.btnSignIn)
        wrongDataText = findViewById(R.id.tvWrongData)
        registrationLink = findViewById(R.id.tvRegister)

        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.GONE

        registrationLink.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(signUpUrl))
            startActivity(browserIntent)
        }

        signInButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            createTokenRequest()
        }
    }


    private fun createTokenRequest() {
        ServiceBuilder.getPostApi().createRequestToken(MovieDBApiKey)
            .enqueue(object: Callback<Token> {

                override fun onFailure(call: Call<Token>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, getString(R.string.error), Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                    receivedToken = ""
                }

                override fun onResponse(call: Call<Token>, response: Response<Token>) {
                    if (response.isSuccessful) {
                        val requestedToken = response.body()
                        if (requestedToken != null) {
                            receivedToken = requestedToken.token
                            loginValidationData = LoginValidationData(
                                username.text.toString(),
                                password.text.toString(), receivedToken
                            )
                            validateWithLogin()
                        }
                    } else {
                        progressBar.visibility = View.GONE
                    }
                }
            })
    }

    private fun validateWithLogin() {
        ServiceBuilder.getPostApi().validateWithLogin(MovieDBApiKey, loginValidationData).enqueue(object :
           Callback<Token> {
            override fun onFailure(call: Call<Token>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@LoginActivity, getString(R.string.error), Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Token>, response: Response<Token>) {
                if (response.isSuccessful) {
                    token = Token(receivedToken)
                    createSession()
                } else {
                    wrongDataText.text = getString(R.string.wrong)
                    progressBar.visibility = View.GONE
                }
            }
        })
    }

    private fun createSession() {
        ServiceBuilder.getPostApi().createSession(MovieDBApiKey, token).enqueue(object :
            Callback<Session> {
            override fun onFailure(call: Call<Session>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@LoginActivity, getString(R.string.error), Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Session>, response: Response<Session>) {
                if (response.isSuccessful) {
                    sessionId = response.body()?.sessionId.toString()


                    saveToSharedPreferences()

                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                }
            }
        })
    }


    private fun saveToSharedPreferences() {

        val editor = sharedPreferences.edit()
        editor.putString(getString(R.string.username), username.text.toString())
        editor.putString(getString(R.string.password), password.text.toString())
        editor.putString(getString(R.string.session_id), sessionId)
        editor.apply()
    }


}