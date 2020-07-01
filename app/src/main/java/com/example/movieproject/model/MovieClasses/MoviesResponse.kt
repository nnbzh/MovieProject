package com.example.movieproject.model.MovieClasses
import com.example.movieproject.model.MovieClasses.Movie
import com.google.gson.annotations.SerializedName

class MoviesResponse (
    @SerializedName("results") val movieList: List<Movie>
)