package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class WatchlistEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String,
    val backdropUrl: String,
    val rating: Double,
    val releaseYear: String,
    val genresCsv: String,
    val isFavorite: Boolean = false,
    val isWatched: Boolean = false,
    val addedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_ratings")
data class UserRatingEntity(
    @PrimaryKey val movieId: Int,
    val ratingStars: Float,
    val userReviewText: String,
    val updatedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey val query: String,
    val timestamp: Long = System.currentTimeMillis()
)
