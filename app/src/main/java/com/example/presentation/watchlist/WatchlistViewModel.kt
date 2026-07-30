package com.example.presentation.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.WatchlistEntity
import com.example.domain.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class WatchlistTab {
    WATCHLIST, FAVORITES, WATCHED
}

data class WatchlistUiState(
    val activeTab: WatchlistTab = WatchlistTab.WATCHLIST,
    val items: List<WatchlistEntity> = emptyList(),
    val totalCount: Int = 0,
    val favoriteCount: Int = 0,
    val watchedCount: Int = 0
)

class WatchlistViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val _activeTab = MutableStateFlow(WatchlistTab.WATCHLIST)

    val uiState: StateFlow<WatchlistUiState> = combine(
        _activeTab,
        repository.getWatchlist(),
        repository.getFavorites(),
        repository.getWatched(),
        _activeTab.flatMapLatest { tab ->
            when (tab) {
                WatchlistTab.WATCHLIST -> repository.getWatchlist()
                WatchlistTab.FAVORITES -> repository.getFavorites()
                WatchlistTab.WATCHED -> repository.getWatched()
            }
        }
    ) { tab, watchlist, favorites, watched, activeItems ->
        WatchlistUiState(
            activeTab = tab,
            items = activeItems,
            totalCount = watchlist.size,
            favoriteCount = favorites.size,
            watchedCount = watched.size
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WatchlistUiState()
    )

    fun selectTab(tab: WatchlistTab) {
        _activeTab.value = tab
    }

    fun toggleFavorite(movieId: Int, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(movieId, !currentStatus)
        }
    }

    fun toggleWatched(movieId: Int, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleWatched(movieId, !currentStatus)
        }
    }

    fun removeFromWatchlist(movieId: Int) {
        viewModelScope.launch {
            // Re-use toggle logic or delete
            val cur = uiState.value.items.find { it.id == movieId }
            if (cur != null) {
                repository.toggleWatchlist(
                    com.example.domain.model.Movie(
                        id = cur.id,
                        title = cur.title,
                        overview = cur.overview,
                        posterUrl = cur.posterUrl,
                        backdropUrl = cur.backdropUrl,
                        rating = cur.rating,
                        releaseYear = cur.releaseYear
                    )
                )
            }
        }
    }
}
