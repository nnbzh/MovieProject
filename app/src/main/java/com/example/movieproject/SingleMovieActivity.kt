package com.example.movieproject

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.movieproject.MovieClasses.SingleMovie
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.single_movie.*
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class SingleMovieActivity : AppCompatActivity() {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_movie);

        val movieID = intent.getIntExtra("movie_id", 1);

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
        getMovie(movieID)

    }

    private fun getMovie(id: Int) {
        ServiceBuilder.getPostApi().getMovie(id, MovieDBApiKey).enqueue(object: Callback<SingleMovie> {
            override fun onFailure(call: Call<SingleMovie>, t: Throwable) {
                progressBar.visibility = View.GONE
            }

            override fun onResponse(call: Call<SingleMovie>, response: Response<SingleMovie>) {
                progressBar.visibility = View.GONE
                val singleMovie = response.body()
                if (singleMovie != null) {
                    title.text = singleMovie.title
                    releaseYear.text = singleMovie.releaseDate.substring(0,4)
                    releaseDate.text = singleMovie.releaseDate
                    duration.text = singleMovie.runtime.toString() + " min"
                    plot.text = singleMovie.overview
                    rating.text = singleMovie.voteAverage.toString()
                    budget.text = singleMovie.budget.toString() + " $"
                    revenue.text = singleMovie.revenue.toString() + " $"
                    genres.text = ""
                    producers.text = ""

                    for (i in singleMovie.genres.indices) {
                        if (i == 0) {
                            genres.append(singleMovie.genres[i].name.toString());
                        } else {
                            genres.append(", " + singleMovie.genres[i].name.toString());
                        }
                    }

                    for (i in singleMovie.producers.indices) {
                        if (i == 0) {
                            producers.append(singleMovie.producers[i].name.toString());
                        } else {
                            producers.append(", " + singleMovie.producers[i].name.toString());
                        }
                    }


                    Picasso.get()
                        .load("https://image.tmdb.org/t/p/w500" + singleMovie.posterPath)
                        .into(poster)
                    Picasso.get()
                        .load("https://image.tmdb.org/t/p/w500" + singleMovie.posterPath)
                        .into(posterFull)

                }
            }
        })
    }

}
