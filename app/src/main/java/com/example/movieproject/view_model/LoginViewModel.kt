package com.example.movieproject.view_model

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.example.movieproject.R
import com.example.movieproject.model.Account.LoginValidationData
import com.example.movieproject.model.Account.Token
import com.example.movieproject.model.MovieDBApiKey
import com.example.movieproject.model.ServiceBuilder
import kotlinx.coroutines.launch

class LoginViewModel(private val context: Context) : CentralViewModel() {
    private lateinit var loginValidationData: LoginValidationData
    private lateinit var token: Token
    private var sessionId: String = ""
    private var receivedToken: String = ""
    private var username: String = ""
    private var password: String = ""

    val liveData = MutableLiveData<State>()

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        context.getString(R.string.preference_file),
        Context.MODE_PRIVATE
    )

    init {
        if (sharedPreferences.contains(context.getString(R.string.session_id))) {
            liveData.value = State.Result
        }
    }

    fun createTokenRequest(receivedUsername: String, receivedPassword: String) {
        launch {
            liveData.value = State.ShowLoading
            try {
                val response = ServiceBuilder.getPostApi().createRequestToken(MovieDBApiKey)
                if (response.isSuccessful) {
                    username = receivedUsername
                    password = receivedPassword
                    val requestedToken = response.body()
                    if (requestedToken != null) {
                        receivedToken = requestedToken.token
                        loginValidationData = LoginValidationData(
                            username,
                            password,
                            receivedToken
                        )
                        validateWithLogin()
                    }

                } else {
                    liveData.value = State.FailedLoading
                    liveData.value = State.HideLoading
                }
            } catch (e: Exception) {
                liveData.value = State.FailedLoading
                liveData.value = State.HideLoading
            }
        }
    }

    private fun validateWithLogin() {
        launch {
            try {
                val response =
                    ServiceBuilder.getPostApi()
                        .validateWithLogin(MovieDBApiKey, loginValidationData)
                if (response.isSuccessful) {
                    token = Token(receivedToken)
                    createSession()
                } else {
                    liveData.value = State.WrongDataProvided
                    liveData.value = State.HideLoading
                }
            } catch (e: Exception) {
                liveData.value = State.WrongDataProvided
                liveData.value = State.HideLoading
            }
        }
    }

    private fun createSession() {
        launch {
            liveData.value = State.ShowLoading
            try {
                val response = ServiceBuilder.getPostApi().createSession(MovieDBApiKey, token)
                if (response.isSuccessful) {
                    sessionId = response.body()?.sessionId.toString()
                    saveToSharedPreferences()
                    liveData.value = State.HideLoading
                    liveData.value = State.Result
                }
            } catch (e: Exception) {
                liveData.value = State.FailedLoading
                liveData.value = State.HideLoading
            }
        }
    }

    private fun saveToSharedPreferences() {
        val editor = sharedPreferences.edit()
        editor.putString(context.getString(R.string.username), username)
        editor.putString(context.getString(R.string.session_id), sessionId)
        editor.putString(context.getString(R.string.password), password)
        editor.apply()
    }

    sealed class State {
        object ShowLoading : State()
        object HideLoading : State()
        object FailedLoading : State()
        object WrongDataProvided : State()
        object Result : State()
    }
}