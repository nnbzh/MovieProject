package com.example.movieproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieproject.MovieClasses.*
import com.squareup.picasso.Picasso

class MovieAdapter(
    var movies: List<Movie>? = null,
    var itemClickListener: rvItemClickListener? = null
): RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    var number = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.moviedb_feed, parent, false)
        return MovieViewHolder(view)
    }

    override fun getItemCount(): Int = movies?.size ?: 0
    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies?. get(position))
    }

    fun clearAll() {
        number = 1
        (movies as? ArrayList<Movie>)?.clear()
        notifyDataSetChanged()
    }

    inner class MovieViewHolder(private val view : View): RecyclerView.ViewHolder(view) {

        fun bind(movie: Movie?) {

            val title = view.findViewById<TextView>(R.id.movieName)
            val rating = view.findViewById<TextView>(R.id.rating)
            val releaseDate = view.findViewById<TextView>(R.id.movieYear)
            val poster = view.findViewById<ImageView>(R.id.poster)
            val like: ImageView = view.findViewById<ImageView>(R.id.like)

            if(movie?.isClicked!!){
                like.setImageResource(R.drawable.liked)
            } else{
                like.setImageResource(R.drawable.like)
            }

            if (movie.position == 0) {
                movie.position = number
                number++
            }

            title.text = movie.title
            rating.text = movie.voteAverage.toString()
            releaseDate.text = movie.releaseDate.substring(0,4)


            Picasso.get()
                .load("https://image.tmdb.org/t/p/w500" + movie.posterPath)
                .into(poster)
            view.setOnClickListener {
                    itemClickListener?.itemClick(adapterPosition, movie)
            }

            like.setOnClickListener {
                itemClickListener?.addToFavourites(adapterPosition, movie)
                if (movie.isClicked) {
                    like.setImageResource(R.drawable.liked)
                } else {
                    like.setImageResource(R.drawable.like)
                }
            }
        }
    }

    interface rvItemClickListener {
        fun itemClick(position: Int, movie: Movie)
        fun addToFavourites(position: Int, item: Movie)
    }
}