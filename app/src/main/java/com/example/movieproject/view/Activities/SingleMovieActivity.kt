package com.example.movieproject.view.Activities

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.kino.MovieClasses.Producers
import com.example.movieproject.model.Database.MovieDao
import com.example.movieproject.model.Database.MovieDatabase
import com.example.movieproject.model.MovieClasses.Genre
import com.example.movieproject.model.MovieClasses.Movie
import com.example.movieproject.R
import com.example.movieproject.model.MovieDBApiKey
import com.example.movieproject.model.ServiceBuilder
import com.example.movieproject.view_model.SingleMovieViewModel
import com.example.movieproject.view_model.ViewModelProviderFactory
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class SingleMovieActivity : AppCompatActivity() , CoroutineScope {

    private var movieDao: MovieDao? = null

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


    private lateinit var progressBar: ProgressBar
    private lateinit var backBtn: ImageButton
    private lateinit var poster: ImageView
    private lateinit var posterFull: ImageView
    private lateinit var title: TextView
    private lateinit var releaseYear: TextView
    private lateinit var releaseDate: TextView
    private lateinit var duration: TextView
    private lateinit var plot: TextView
    private lateinit var rating: TextView
    private lateinit var budget: TextView
    private lateinit var revenue: TextView
    private lateinit var genres: TextView
    private lateinit var producers: TextView
    private lateinit var mainLayout: ConstraintLayout
    private lateinit var singleMovieViewModel: SingleMovieViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_movie);

        initViewModel()
        bindViews()

        val movieID = intent.getIntExtra("movie_id", 1);
        getMovie(movieID)
    }

    private fun initViewModel() {
        val viewModelProviderFactory = ViewModelProviderFactory(context = this)
        singleMovieViewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(SingleMovieViewModel::class.java)
    }

    private fun bindViews() {
        mainLayout = findViewById(R.id.mainLayout)
        mainLayout.visibility = View.INVISIBLE;

        progressBar = findViewById(R.id.progressBar)
        backBtn = findViewById(R.id.btnBack)
        poster = findViewById(R.id.ivBanner)
        title = findViewById(R.id.tvMovieTitle)
        releaseYear = findViewById(R.id.tvReleaseYear)
        releaseDate = findViewById(R.id.tvReleaseDate)
        posterFull = findViewById(R.id.ivFull)
        duration = findViewById(R.id.tvDuration)
        plot = findViewById(R.id.tvPlot)
        rating = findViewById(R.id.tvRating)
        budget = findViewById(R.id.tvBudget)
        revenue = findViewById(R.id.tvRevenue)
        genres = findViewById(R.id.tvGenre)
        producers = findViewById(R.id.tvProducers)

        backBtn.setOnClickListener{
            finish();
        }
    }

    private fun fillInfo(singleMovie: Movie) {
        mainLayout.visibility = View.VISIBLE
        title.text = singleMovie.title
        releaseYear.text = singleMovie.releaseDate.substring(0, 4)
        releaseDate.text = singleMovie.releaseDate
        duration.text = "${singleMovie.runtime.toString()} ${getString(R.string.min)}"
        plot.text = singleMovie.overview
        rating.text = singleMovie.voteAverage.toString()
        budget.text = "${singleMovie.budget.toString()} ${getString(R.string.dollar)}"
        revenue.text = "${singleMovie.revenue.toString()} ${getString(R.string.dollar)}"
        genres.text = genresToString(singleMovie.genres)
        producers.text = setProdNames(singleMovie.producers)

        Picasso.get()
            .load("https://image.tmdb.org/t/p/w500" + singleMovie.posterPath)
            .into(poster)
        Picasso.get()
            .load("https://image.tmdb.org/t/p/w500" + singleMovie.posterPath)
            .into(posterFull)
    }

    private fun getMovie(id: Int) {
        singleMovieViewModel.getMovie(id)
        singleMovieViewModel.liveData.observe(this, Observer { result ->
            when(result) {
                is SingleMovieViewModel.State.HideLoading -> {
                    progressBar.visibility = View.GONE
                }
                is SingleMovieViewModel.State.Result -> {
                    mainLayout.visibility = View.VISIBLE
                    if (result.movie != null) {
                        fillInfo(result.movie)
                    }
                }
            }
        })
    }

    private fun genresToString(genres: List<Genre>?): String {
        var names = ""
            for (genre in genres!!) names+= "${genre.name}, "
        return names;
    }

    private fun setProdNames(producers: List<Producers>?) : String {
        var producersName = ""
            for (prod in producers!!) producersName+= "${prod.name}, "
        return producersName;
    }


}
