package com.example.movieproject
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import kotlinx.coroutines.CoroutineScope
import androidx.appcompat.app.AppCompatActivity
import com.example.movieproject.Account.LoginValidationData
import com.example.movieproject.Account.Token
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class LoginActivity: AppCompatActivity(), CoroutineScope {

    private lateinit var loginValidationData: LoginValidationData
    private lateinit var token: Token
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var wrongDataText: TextView
    private lateinit var signInButton: Button
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var registrationLink: TextView
    private lateinit var progressBar: ProgressBar

    private val signUpUrl: String = "https://www.themoviedb.org/account/signup"
    private var sessionId: String = ""
    private var receivedToken: String = ""

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

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

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private fun createTokenRequest() {
        launch {
            try {
                val response = ServiceBuilder.getPostApi().createRequestToken(MovieDBApiKey)
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
                    Toast.makeText(
                        this@LoginActivity,
                        getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                    progressBar.visibility = View.GONE
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@LoginActivity,
                    getString(R.string.error),
                    Toast.LENGTH_SHORT
                ).show()
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun validateWithLogin() {
        launch {
            try {
                val response =
                    ServiceBuilder.getPostApi().validateWithLogin(MovieDBApiKey, loginValidationData)

                if (response.isSuccessful) {
                    token = Token(receivedToken)
                    createSession()
                } else {
                    wrongDataText.text = getString(R.string.wrong)
                    progressBar.visibility = View.GONE
                }
            } catch (e: Exception) {
                wrongDataText.text = getString(R.string.wrong)
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun createSession() {
        launch {
            try {
                val response = ServiceBuilder.getPostApi().createSession(MovieDBApiKey, token)
                if (response.isSuccessful) {
                    sessionId = response.body()?.sessionId.toString()
                    saveToSharedPreferences()

                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)

                } else {
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@LoginActivity,
                        getString(R.string.error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Toast.makeText(
                    this@LoginActivity,
                    getString(R.string.error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun saveToSharedPreferences() {

        val editor = sharedPreferences.edit()
        editor.putString(getString(R.string.username), username.text.toString())
        editor.putString(getString(R.string.password), password.text.toString())
        editor.putString(getString(R.string.session_id), sessionId)
        editor.apply()
    }


}