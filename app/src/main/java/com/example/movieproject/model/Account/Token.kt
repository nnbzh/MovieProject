package com.example.movieproject.model.Account

import com.google.gson.annotations.SerializedName

data class Token(
    @SerializedName("request_token") val token: String
)