package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {
    @Query("SELECT * FROM watchlist ORDER BY addedTimestamp DESC")
    fun getAllWatchlist(): Flow<List<WatchlistEntity>>

    @Query("SELECT * FROM watchlist WHERE isFavorite = 1 ORDER BY addedTimestamp DESC")
    fun getFavorites(): Flow<List<WatchlistEntity>>

    @Query("SELECT * FROM watchlist WHERE isWatched = 1 ORDER BY addedTimestamp DESC")
    fun getWatched(): Flow<List<WatchlistEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE id = :movieId)")
    fun isInWatchlist(movieId: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToWatchlist(entity: WatchlistEntity)

    @Query("DELETE FROM watchlist WHERE id = :movieId")
    suspend fun removeFromWatchlist(movieId: Int)

    @Query("UPDATE watchlist SET isFavorite = :isFavorite WHERE id = :movieId")
    suspend fun setFavoriteStatus(movieId: Int, isFavorite: Boolean)

    @Query("UPDATE watchlist SET isWatched = :isWatched WHERE id = :movieId")
    suspend fun setWatchedStatus(movieId: Int, isWatched: Boolean)
}

@Dao
interface UserRatingDao {
    @Query("SELECT * FROM user_ratings WHERE movieId = :movieId")
    fun getUserRatingForMovie(movieId: Int): Flow<UserRatingEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserRating(rating: UserRatingEntity)

    @Query("DELETE FROM user_ratings WHERE movieId = :movieId")
    suspend fun deleteUserRating(movieId: Int)
}

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 10")
    fun getRecentSearches(): Flow<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchQuery(query: SearchHistoryEntity)

    @Query("DELETE FROM search_history WHERE query = :query")
    suspend fun deleteSearchQuery(query: String)

    @Query("DELETE FROM search_history")
    suspend fun clearHistory()
}
