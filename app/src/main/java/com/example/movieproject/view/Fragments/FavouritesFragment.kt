package com.example.movieproject.view.Fragments


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.movieproject.view.MovieAdapter
import com.example.movieproject.R
import com.example.movieproject.view.Activities.SingleMovieActivity
import com.example.movieproject.model.MovieClasses.Movie
import com.example.movieproject.view_model.FavMovieListViewModel
import com.example.movieproject.view_model.ViewModelProviderFactory
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class FavouritesFragment: Fragment(),
    MovieAdapter.RvItemClickListener, CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var adapter: MovieAdapter? = null
    private lateinit var favMovieListViewModel: FavMovieListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.recycle_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        bindViews(view)
        initAdapter()
        getMovies()

    }
    private fun initViewModel() {
        val viewModelProviderFactory = ViewModelProviderFactory(context = requireActivity())
        favMovieListViewModel = ViewModelProvider(this, viewModelProviderFactory).
            get(FavMovieListViewModel::class.java)
    }

    private fun initAdapter() {
        adapter =
            this.context?.let {
                MovieAdapter(
                    itemClickListener = this
                )
            }
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
        favMovieListViewModel.getMovies()
        favMovieListViewModel.liveData.observe(this, Observer { result ->
            when (result) {
                is FavMovieListViewModel.State.ShowLoading -> {
                    swipeRefreshLayout.isRefreshing = true
                }
                is FavMovieListViewModel.State.HideLoading -> {
                    swipeRefreshLayout.isRefreshing = false
                }
                is FavMovieListViewModel.State.Result -> {
                    adapter?.movies = result.moviesList
                    adapter?.notifyDataSetChanged()
                }
            }
        })
    }



    override fun addToFavourites(position: Int, item: Movie) {
        favMovieListViewModel.addToFavourites(item)
        swipeRefreshLayout.isRefreshing = true
        adapter?.clearAll()
        getMovies()
    }

}