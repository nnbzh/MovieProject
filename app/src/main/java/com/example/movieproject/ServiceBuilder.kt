package com.example.movieproject

import com.example.movieproject.MovieClasses.LikedMovie
import com.example.movieproject.MovieClasses.MovieStatus
import com.example.movieproject.MovieClasses.MoviesResponse
import com.example.movieproject.MovieClasses.SingleMovie
import com.google.gson.JsonObject
import com.example.movieproject.MovieClasses.StatusResponse
import okhttp3.OkHttpClient

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

object ServiceBuilder {

    private const val BASE_URL = "https://api.themoviedb.org/3/"

    fun getPostApi(): PostApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getOkHttp())
            .build()
        return retrofit.create(PostApi::class.java)
    }

    private fun getOkHttp(): OkHttpClient{
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            return okHttpClient.build()

    }
}

interface PostApi {

    @GET("movie/top_rated")
    fun getPopularMovieList(@Query("api_key") apiKey: String): Call<MoviesResponse>

//    @GET("movie/{id}")
//    fun getMovieById(
//        @Path("id") id: Int,
//        @Query("api_key") apiKe y: String
//    ): Call<MovieDetails>
//
//    @GET("genre/movie/list")
//    fun getGenres(@Query("api_key") apiKey: String): Call<Genres>

//    @GET("authentication/token/new")
//    fun createRequestToken(@Query("api_key") apiKey: String): Call<Token>
//
//    @POST("authentication/token/validate_with_login")
//    fun validateWithLogin(
//        @Query("api_key") apiKey: String,
//        @Body data: LoginValidationData
//    ): Call<Token>
//
//    @POST("authentication/session/new")
//    fun createSession(
//        @Query("api_key") apiKey: String,
//        @Body token: Token
//    ): Call<Session>
    @GET("movie/{movie_id}")
    fun getMovie(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Call<SingleMovie>

    @GET("account/{account_id}/favorite/movies")
    fun getFavouriteMovies(
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String
    ): Call<MoviesResponse>

    @POST("account/{account_id}/favorite")
    fun addRemoveFavourites(
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String,
        @Body fav: LikedMovie
    ): Call<StatusResponse>

    @GET("movie/{movie_id}/account_states")
    fun getMovieStates(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String
    ): Call<MovieStatus>
}