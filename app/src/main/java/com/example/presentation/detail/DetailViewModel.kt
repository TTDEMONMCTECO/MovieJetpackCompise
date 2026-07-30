package com.example.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.UserRatingEntity
import com.example.domain.model.MovieDetail
import com.example.domain.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DetailUiState(
    val detail: MovieDetail? = null,
    val isInWatchlist: Boolean = false,
    val isFavorite: Boolean = false,
    val isWatched: Boolean = false,
    val userRating: UserRatingEntity? = null,
    val activeTrailerTitle: String? = null,
    val isLoading: Boolean = true
)

class DetailViewModel(
    private val movieId: Int,
    private val repository: MovieRepository
) : ViewModel() {

    private val _activeTrailerTitle = MutableStateFlow<String?>(null)

    val uiState: StateFlow<DetailUiState> = combine(
        repository.getMovieDetail(movieId),
        repository.isInWatchlist(movieId),
        repository.getFavorites(),
        repository.getUserRating(movieId),
        _activeTrailerTitle
    ) { detail, inWatchlist, favoritesList, userRating, activeTrailer ->
        DetailUiState(
            detail = detail,
            isInWatchlist = inWatchlist,
            isFavorite = favoritesList.any { it.id == movieId },
            userRating = userRating,
            activeTrailerTitle = activeTrailer,
            isLoading = detail == null
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DetailUiState(isLoading = true)
    )

    fun toggleWatchlist() {
        val detail = uiState.value.detail ?: return
        viewModelScope.launch {
            repository.toggleWatchlist(detail.movie)
        }
    }

    fun toggleFavorite() {
        val detail = uiState.value.detail ?: return
        val currentFav = uiState.value.isFavorite
        viewModelScope.launch {
            repository.toggleFavorite(detail.movie.id, !currentFav)
        }
    }

    fun submitUserRating(stars: Float, reviewText: String) {
        viewModelScope.launch {
            repository.saveUserRating(movieId, stars, reviewText)
        }
    }

    fun playTrailer(title: String) {
        _activeTrailerTitle.value = title
    }

    fun closeTrailer() {
        _activeTrailerTitle.value = null
    }
}
