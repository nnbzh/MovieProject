package com.example.movieproject.model.Database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.movieproject.model.MovieClasses.MovieStatus


@Dao
interface MovieStatusDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovieStatus(movieState: MovieStatus)

    @Query("SELECT * FROM movie_statuses")
    fun getMovieStatuses(): List<MovieStatus>

    @Query("DELETE FROM movie_statuses")
    fun deleteAll()
}