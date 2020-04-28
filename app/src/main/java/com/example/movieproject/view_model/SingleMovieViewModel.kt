package com.example.movieproject.view_model

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.example.movieproject.R
import com.example.movieproject.model.Database.MovieDao
import com.example.movieproject.model.Database.MovieDatabase
import com.example.movieproject.model.Database.MovieStatusDao
import com.example.movieproject.model.MovieClasses.Movie
import com.example.movieproject.model.MovieDBApiKey
import com.example.movieproject.model.ServiceBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class SingleMovieViewModel(private val context: Context) : CentralViewModel() {

    private var movieDao: MovieDao = MovieDatabase.getDatabase(context = context).movieDao()

    val liveData = MutableLiveData<SingleMovieViewModel.State>()

    fun getMovie(id: Int) {
        launch {
            val movieDetail = withContext(Dispatchers.IO) {
                try {
                    val response = ServiceBuilder.getPostApi().getMovie(id,
                        MovieDBApiKey
                    )
                    if (response.isSuccessful) {
                        val movie = response.body()
                        movie?.runtime?.let { movieDao?.updateMovieRuntime(it, id) }
                        return@withContext movie
                    } else {
                        return@withContext movieDao?.getMovie(id)
                    }
                } catch (e: Exception) {
                    movieDao?.getMovie(id)
                }
            }
            val singleMovie: Movie = movieDetail as Movie
            liveData.value = State.HideLoading
            liveData.value = State.Result(singleMovie)
        }

    }

    sealed class State {
        object ShowLoading: State()
        object HideLoading : State()
        data class Result(val movie: Movie?) : State()
    }

}