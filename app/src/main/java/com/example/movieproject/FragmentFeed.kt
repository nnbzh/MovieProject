package com.example.movieproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

import android.os.Build
import android.os.Bundle
import retrofit2.Call
import retrofit2.Callback
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.movieproject.MovieDBApiKey
import com.example.movieproject.MovieClasses.*
import com.example.movieproject.MovieAdapter
import kotlinx.android.synthetic.main.single_movie.*
import okhttp3.internal.notify
import retrofit2.Response
import java.lang.Exception
import com.example.movieproject.ServiceBuilder


class FragmentFeed: Fragment() {

    private var relativeLayout: RelativeLayout? = null
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var adapter: MovieAdapter
    private lateinit var sessionId: String
    private lateinit var movieList: MutableList<Movie>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      return inflater.inflate(R.layout.fragment_feed,container,false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences(
           "shared_preference", Context.MODE_PRIVATE
        )

        if (sharedPreferences.contains("session_id")) {
            sessionId = sharedPreferences.getString("seesion_id", "null") as String
        }

        recyclerView=view.findViewById(R.id.recy_feed)
        recyclerView.layoutManager=LinearLayoutManager(context)
        swipeRefreshLayout=view.findViewById(R.id.swipeRefreshLayout)

        swipeRefreshLayout.setOnRefreshListener {
            adapter?.clearAll()
            getMovies()
        }

        adapter =
            this.context?.let { MovieAdapter(itemClickListener = this) }
        recyclerView.adapter = adapter
        getMovies()
    }


    private fun getMovies() {
        swipeRefreshLayout.isRefreshing=true
        ServiceBuilder.getPostApi().getPopularMovieList(MovieDBApiKey)
            .enqueue(object : Callback<MoviesResponse> {
                override fun onFailure(call: Call<MoviesResponse>, t: Throwable) {
                    swipeRefreshLayout.isRefreshing = false
                }

                override fun onResponse(
                    call: Call<MoviesResponse>,
                    response: Response<MoviesResponse>
                ) {
                    movieList = mutableListOf()

                    if (response.isSuccessful) {
                        val movies = response.body()
                        if (movies != null) {
                            for (movie: Movie in movies.movieList) {
                                movieList.add(movie)
                            }
                        }
                        adapter?.movies = movieList
                        adapter?.notifyDataSetChanged()
                    }
                    swipeRefreshLayout.isRefreshing = false
                }
            })
    }
}
