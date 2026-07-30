package com.example.data.remote

import com.squareup.moshi.Json
import retrofit2.http.GET
import retrofit2.http.Query

data class TmdbMovieDto(
    val id: Int,
    val title: String,
    val overview: String,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "backdrop_path") val backdropPath: String?,
    @Json(name = "vote_average") val voteAverage: Double,
    @Json(name = "release_date") val releaseDate: String?,
    @Json(name = "genre_ids") val genreIds: List<Int>? = null
)

data class TmdbResponseDto(
    val page: Int,
    val results: List<TmdbMovieDto>
)

interface TmdbApiService {
    @GET("3/movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): TmdbResponseDto

    @GET("3/movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): TmdbResponseDto

    @GET("3/search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): TmdbResponseDto
}
