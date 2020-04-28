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
import com.example.movieproject.view_model.MoviesListViewModel
import com.example.movieproject.view_model.ViewModelProviderFactory


class FragmentFeed: Fragment(), MovieAdapter.RvItemClickListener {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private  lateinit var recyclerView: RecyclerView
    private  var adapter: MovieAdapter? = null

    private lateinit var moviesListViewModel: MoviesListViewModel
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,savedInstanceState: Bundle?): View? {

      return inflater.inflate(R.layout.fragment_feed,container,false)
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
        moviesListViewModel = ViewModelProvider(this, viewModelProviderFactory).
            get(MoviesListViewModel::class.java)
    }

    private fun bindViews(view: View) = with(view) {
        recyclerView = findViewById(R.id.recy_feed)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            adapter?.clearAll()
            getMovies()
        }
    }

    private fun initAdapter() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter =
            this.context.let { MovieAdapter(itemClickListener = this) }
        recyclerView.adapter = adapter
    }

    private fun getMovies() {
        moviesListViewModel.getMovies()
        moviesListViewModel.liveData.observe(this, Observer { result ->
            when (result) {
                is MoviesListViewModel.State.ShowLoading -> {
                    swipeRefreshLayout.isRefreshing = true
                }
                is MoviesListViewModel.State.HideLoading -> {
                    swipeRefreshLayout.isRefreshing = false
                }
                is MoviesListViewModel.State.Update -> {

                }
                is MoviesListViewModel.State.Result -> {
                    adapter?.movies = result.moviesList
                    adapter?.notifyDataSetChanged()
                }
            }
        })
    }


    override fun itemClick(position: Int, movie: Movie) {
        val intent = Intent(context, SingleMovieActivity::class.java)
        intent.putExtra("movie_id", movie.id)
        startActivity(intent)
    }

    override fun addToFavourites(position: Int, item: Movie) {
        moviesListViewModel.addToFavourites(item)
    }
}
