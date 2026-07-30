package com.example.domain.repository

import com.example.data.local.SearchHistoryEntity
import com.example.data.local.UserRatingEntity
import com.example.data.local.WatchlistEntity
import com.example.domain.model.Movie
import com.example.domain.model.MovieDetail
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getMoviesByCategory(category: String): Flow<List<Movie>>
    fun getMovieDetail(movieId: Int): Flow<MovieDetail?>
    fun searchMovies(query: String, selectedGenre: String?): Flow<List<Movie>>
    
    // Watchlist & Favorites
    fun getWatchlist(): Flow<List<WatchlistEntity>>
    fun getFavorites(): Flow<List<WatchlistEntity>>
    fun getWatched(): Flow<List<WatchlistEntity>>
    fun isInWatchlist(movieId: Int): Flow<Boolean>
    suspend fun toggleWatchlist(movie: Movie)
    suspend fun toggleFavorite(movieId: Int, isFavorite: Boolean)
    suspend fun toggleWatched(movieId: Int, isWatched: Boolean)

    // User Ratings
    fun getUserRating(movieId: Int): Flow<UserRatingEntity?>
    suspend fun saveUserRating(movieId: Int, ratingStars: Float, reviewText: String)

    // Search History
    fun getRecentSearches(): Flow<List<SearchHistoryEntity>>
    suspend fun addSearchQuery(query: String)
    suspend fun clearSearchHistory()

    // AI
    suspend fun getAiRecommendation(prompt: String): String
}
