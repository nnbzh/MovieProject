package com.example.movieproject.view_model


import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.example.movieproject.model.Database.MovieDao
import com.example.movieproject.model.Database.MovieDatabase
import com.example.movieproject.model.Database.MovieStatusDao
import com.example.movieproject.model.MovieClasses.Movie
import com.example.movieproject.R
import com.example.movieproject.model.MovieClasses.LikedMovie
import com.example.movieproject.model.MovieClasses.MovieStatus
import com.example.movieproject.model.MovieDBApiKey
import com.example.movieproject.model.ServiceBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class FavMovieListViewModel(private val context: Context) : CentralViewModel() {

    private var movieDao: MovieDao = MovieDatabase.getDatabase(context = context).movieDao()
    private var movieStatusDao: MovieStatusDao =
        MovieDatabase.getDatabase(context = context).movieStatusDao()

    private lateinit var sharedPref: SharedPreferences
    private lateinit var sessionId: String

    val liveData = MutableLiveData<State>()

    init {
        getSharedPreferences()
    }

    private fun getSharedPreferences() {

        sharedPref = context.getSharedPreferences(
            context.getString(R.string.preference_file),
            Context.MODE_PRIVATE
        )
        if (sharedPref.contains(context.getString(R.string.session_id))) {
            sessionId = sharedPref.getString(
                context.getString(R.string.session_id),
                "null"
            ) as String
        }
    }

    fun getMovies() {
        launch {
            liveData.value = State.ShowLoading
            withContext(Dispatchers.IO) {
                refreshFavourites()
            }
            val favMovies = withContext(Dispatchers.IO) {
                try {
                    delay(500)
                    val response =
                        ServiceBuilder.getPostApi().getFavouriteMovies(MovieDBApiKey, sessionId)
                    if (response.isSuccessful) {
                        val movies = response.body()?.movieList
                        if (!movies.isNullOrEmpty()) {
                            for (movie in movies) {
                                movie.isClicked = true
                            }
                        }
                        return@withContext movies
                    } else {
                        return@withContext movieDao?.getFavouriteMovies() ?: emptyList()
                    }
                } catch (e: Exception) {
                    return@withContext movieDao?.getFavouriteMovies() ?: emptyList()
                }
            }
            liveData.value = State.HideLoading
            liveData.value = State.Result(favMovies)
        }
    }
    private fun refreshFavourites() {
        val movies = movieStatusDao?.getMovieStatuses()
        if (!movies.isNullOrEmpty()) {
            for (movie in movies) {
                val likedMovie = LikedMovie(
                    movieId = movie.movieId,
                    selectedStatus = movie.selectedStatus
                )
                addRemoveFavourites(likedMovie)
            }
        }
        movieStatusDao?.deleteAll()
    }

    private fun addRemoveFavourites(likedMovie: LikedMovie) {
        launch {
            try {
                val response = ServiceBuilder.getPostApi().addRemoveFavourites(MovieDBApiKey, sessionId, likedMovie)
                if (response.isSuccessful) {
                }
            } catch (e:Exception) {
                withContext(Dispatchers.IO) {
                    movieDao?.updateMovieIsCLicked(
                        likedMovie.selectedStatus,
                        likedMovie.movieId
                    )
                    val moviesStatus = MovieStatus(likedMovie.movieId, likedMovie.selectedStatus)
                    movieStatusDao?.insertMovieStatus(moviesStatus)
                }
            }
        }
    }
    fun addToFavourites(item: Movie) {
        lateinit var likedMovie: LikedMovie

        if (!item.isClicked) {
            item.isClicked = true
            likedMovie = LikedMovie("movie", item.id, item.isClicked)
        } else {
            item.isClicked = false
            likedMovie = LikedMovie("movie", item.id, item.isClicked)
            likedMovie.selectedStatus = item.isClicked
        }
        addRemoveFavourites(likedMovie)
    }



    sealed class State {
        object Update : State()
        object ShowLoading : State()
        object HideLoading : State()
        data class Result(val moviesList: List<Movie>?) : State()
    }
}
