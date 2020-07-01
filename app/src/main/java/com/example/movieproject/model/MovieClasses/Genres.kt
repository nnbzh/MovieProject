package com.example.kino.MovieClasses

import com.example.movieproject.model.MovieClasses.Genre
import com.google.gson.annotations.SerializedName

data class Genres (
    @SerializedName("genres") val genres: List<Genre>
)