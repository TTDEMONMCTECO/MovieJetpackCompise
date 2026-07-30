package com.example.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Movie
import com.example.domain.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val selectedCategory: String = "All",
    val movies: List<Movie> = emptyList(),
    val featuredMovie: Movie? = null,
    val isLoading: Boolean = false,
    val watchlistIds: Set<Int> = emptySet()
)

class HomeViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow("All")
    private val _watchlistIds = MutableStateFlow<Set<Int>>(emptySet())

    init {
        viewModelScope.launch {
            repository.getWatchlist().collect { list ->
                _watchlistIds.value = list.map { it.id }.toSet()
            }
        }
    }

    val uiState: StateFlow<HomeUiState> = combine(
        _selectedCategory,
        _watchlistIds,
        _selectedCategory.flatMapLatest { category -> repository.getMoviesByCategory(category) }
    ) { category, watchlist, movies ->
        HomeUiState(
            selectedCategory = category,
            movies = movies,
            featuredMovie = movies.firstOrNull { it.isFeatured } ?: movies.firstOrNull(),
            isLoading = false,
            watchlistIds = watchlist
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun toggleWatchlist(movie: Movie) {
        viewModelScope.launch {
            repository.toggleWatchlist(movie)
        }
    }
}
