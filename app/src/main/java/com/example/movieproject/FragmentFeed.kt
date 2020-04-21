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


class FragmentFeed: Fragment(), MovieAdapter.rvItemClickListener {

    private var relativeLayout: RelativeLayout? = null
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private  lateinit var recyclerView: RecyclerView
    private lateinit var sharedPreferences: SharedPreferences
    private  var adapter: MovieAdapter? = null
    private  lateinit var sessionId: String
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
           getString(R.string.preference_file), Context.MODE_PRIVATE
        )

        if (sharedPreferences.contains(getString(R.string.session_id))) {
            sessionId = sharedPreferences.getString(getString(R.string.session_id), "null") as String
        }

        bindViews(view)
        initAdapter()

        swipeRefreshLayout.setOnRefreshListener {
            adapter?.clearAll()
            getMovies()
        }

        getMovies()
    }
    private fun initAdapter() {
        recyclerView.layoutManager=LinearLayoutManager(context)
        adapter =
            this.context.let { MovieAdapter(itemClickListener = this) }
        recyclerView.adapter = adapter
    }
    private fun bindViews(view: View) {
        recyclerView=view.findViewById(R.id.recy_feed)
        swipeRefreshLayout=view.findViewById(R.id.swipeRefreshLayout)
    }

    override fun itemClick(position: Int, movie: Movie) {
        val intent = Intent(context, SingleMovieActivity::class.java)
        intent.putExtra("movie_id", movie.id)
        startActivity(intent)
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
//                        if (movies?.movieList?.size == 0) {
//                            swipeRefreshLayout.isRefreshing = false
//                        }
                        if (movies != null) {
                            for (movie: Movie in movies.movieList) {
                                likeStatus(movie)
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

    override fun addToFavourites(position: Int, item: Movie) {

        lateinit var likedMovie: LikedMovie

        if(!item.isClicked){
            item.isClicked=true
            likedMovie = LikedMovie("movie", item.id, item.isClicked)

            ServiceBuilder.getPostApi().addRemoveFavourites(MovieDBApiKey,sessionId,likedMovie)
                .enqueue(object : Callback<StatusResponse>{

                override fun onFailure(call: Call<StatusResponse>, t: Throwable)
                {Toast.makeText(context, "rate operation failed", Toast.LENGTH_SHORT).show()}

                override fun onResponse(
                    call: Call<StatusResponse>,
                    response: Response<StatusResponse>
                ) {

                }
            })

        } else {
            item.isClicked = false
            likedMovie= LikedMovie("movie", item.id, item.isClicked)

                ServiceBuilder.getPostApi().addRemoveFavourites(MovieDBApiKey, sessionId, likedMovie)
                .enqueue(object : Callback<StatusResponse> {
                    override fun onFailure(call: Call<StatusResponse>, t: Throwable) {}
                    override fun onResponse(
                        call: Call<StatusResponse>,
                        response: Response<StatusResponse>
                    ) {
                    }
                })
        }


    }

    fun likeStatus(movie: Movie){
        ServiceBuilder.getPostApi().getMovieStates(movie.id, MovieDBApiKey, sessionId)
            .enqueue(object: Callback<MovieStatus> {
            override fun onFailure(call: Call<MovieStatus>, t: Throwable) {}
            override fun onResponse(call: Call<MovieStatus>, response: Response<MovieStatus>) {
                if (response.isSuccessful) {
                    val movieStatus = response.body()
                    if (movieStatus != null) {
                        movie.isClicked = movieStatus.selectedStatus
                        adapter?.notifyDataSetChanged()
                    }
                }
            }
        })
    }
}
