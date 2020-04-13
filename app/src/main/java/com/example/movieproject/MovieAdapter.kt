package com.example.movieproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso

class MovieAdapter(
    var movieList: List<Movie>? = null,
    var itemClickListener: rvItemClickListener? = null
): RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.moviedb_feed, parent, false);
        return MovieViewHolder(view);
    }

    override fun getItemCount(): Int = movieList?.size ?:0

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    inner class MovieViewHolder(private val view : View): RecyclerView.ViewHolder(itemView) {
        fun bind(movie: Movie?) {
            val title = view.findViewById<TextView>(R.id.movieName)
            val rating = view.findViewById<TextView>(R.id.rating)
            val releaseDate = view.findViewById<TextView>(R.id.movieYear)
            val poster = view.findViewById<ImageView>(R.id.poster)

            Picasso.get()
                .load("https://image.tmdb.org/t/p/w500" + movie.posterPath)
                .into(poster)
            view.setOnClickListener {
                if (movie != null) {
                    itemClickListener?.itemClick(adapterPosition, movie)
                }
            }
        }
    }

    interface rvItemClickListener {
        fun itemClick(position: Int, movie: Movie)
    }
}