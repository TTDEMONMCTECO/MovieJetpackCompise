package com.example.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SearchHistoryEntity
import com.example.domain.model.Movie
import com.example.domain.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val selectedGenre: String? = null,
    val results: List<Movie> = emptyList(),
    val recentSearches: List<SearchHistoryEntity> = emptyList(),
    val isLoading: Boolean = false
)

class SearchViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val _selectedGenre = MutableStateFlow<String?>("All")

    val uiState: StateFlow<SearchUiState> = combine(
        _query,
        _selectedGenre,
        repository.getRecentSearches(),
        combine(_query, _selectedGenre) { q, genre -> q to genre }
            .flatMapLatest { (q, genre) -> repository.searchMovies(q, genre) }
    ) { query, genre, searches, results ->
        SearchUiState(
            query = query,
            selectedGenre = genre,
            results = results,
            recentSearches = searches,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchUiState()
    )

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun onSearchSubmitted(q: String) {
        _query.value = q
        viewModelScope.launch {
            repository.addSearchQuery(q)
        }
    }

    fun selectGenre(genre: String) {
        _selectedGenre.value = if (_selectedGenre.value == genre) "All" else genre
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearSearchHistory()
        }
    }
}
