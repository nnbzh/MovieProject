package com.example.movieproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.movieproject.MovieClasses.LikedMovie
import com.example.movieproject.MovieClasses.Movie
import com.example.movieproject.MovieClasses.MoviesResponse
import com.example.movieproject.MovieClasses.StatusResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FavouritesFragment: Fragment(), MovieAdapter.RvItemClickListener {


    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var adapter: MovieAdapter? = null
    private var sessionId: String = ""
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.recycle_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE
        )

        if (sharedPreferences.contains(getString(R.string.session_id))) {
            sessionId =
                sharedPreferences.getString(getString(R.string.session_id), "null") as String
        }

        bindViews(view)

        initAdapter()

        getMovies()

    }

    private fun initAdapter() {
        adapter =
            this.context?.let { MovieAdapter(itemClickListener = this) }
        recyclerView.adapter = adapter
    }

    private fun bindViews(view: View) = with(view) {
        recyclerView = findViewById(R.id.like_fragment)
        recyclerView.layoutManager = LinearLayoutManager(context)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        swipeRefreshLayout.setOnRefreshListener {
            adapter?.clearAll()
            getMovies()
        }
    }

    override fun itemClick(position: Int, movie: Movie) {
        val intent = Intent(context, SingleMovieActivity::class.java)
        intent.putExtra("movie_id", movie.id)
        startActivity(intent)
    }

    private fun getMovies() {

       ServiceBuilder.getPostApi().getFavouriteMovies(MovieDBApiKey, sessionId)
            .enqueue(object : Callback<MoviesResponse> {
                override fun onFailure(call: Call<MoviesResponse>, t: Throwable) {
                    swipeRefreshLayout.isRefreshing = false
                }

                override fun onResponse(
                    call: Call<MoviesResponse>,
                    response: Response<MoviesResponse>) {

                    if (response.isSuccessful) {
                        val movies: MoviesResponse? = response.body()
                        if (movies?.movieList?.isNullOrEmpty()!!) {
                            swipeRefreshLayout.isRefreshing = false
                        } else {
                            adapter?.movies = movies?.movieList
                            if (adapter?.movies != null) {
                                for (movie in adapter?.movies as MutableList<Movie>) {
                                    movie.isClicked = true
                                }
                            }
                            adapter?.notifyDataSetChanged()
                            swipeRefreshLayout.isRefreshing = false
                        }
                    }
                }
            })
    }

    override fun addToFavourites(position: Int, item: Movie) {
        lateinit var likedMovie: LikedMovie

        if (!item.isClicked) {
            item.isClicked = true
            likedMovie = LikedMovie("movie", item.id, item.isClicked)
            ServiceBuilder.getPostApi().addRemoveFavourites(MovieDBApiKey, sessionId, likedMovie)
                .enqueue(object : Callback<StatusResponse> {
                    override fun onFailure(call: Call<StatusResponse>, t: Throwable) {}

                    override fun onResponse(
                        call: Call<StatusResponse>,
                        response: Response<StatusResponse>
                    ) {
                    }
                })
        } else {
            item.isClicked = false
            likedMovie = LikedMovie("movie", item.id, item.isClicked)
            likedMovie.selectedStatus = item.isClicked
            ServiceBuilder.getPostApi().addRemoveFavourites(MovieDBApiKey, sessionId, likedMovie)
                .enqueue(object : Callback<StatusResponse> {
                    override fun onFailure(call: Call<StatusResponse>, t: Throwable)
                    {Toast.makeText(context, "rate operation failed", Toast.LENGTH_SHORT).show()}

                    override fun onResponse(
                        call: Call<StatusResponse>,
                        response: Response<StatusResponse>
                    ) {
                    }
                })
        }
    }
}