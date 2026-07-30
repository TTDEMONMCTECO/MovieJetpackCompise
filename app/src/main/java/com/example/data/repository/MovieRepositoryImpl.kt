package com.example.data.repository

import com.example.data.local.MovieDatabase
import com.example.data.local.SearchHistoryEntity
import com.example.data.local.UserRatingEntity
import com.example.data.local.WatchlistEntity
import com.example.data.remote.GeminiMovieService
import com.example.domain.model.CastMember
import com.example.domain.model.Movie
import com.example.domain.model.MovieDetail
import com.example.domain.model.Review
import com.example.domain.model.VideoTrailer
import com.example.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class MovieRepositoryImpl(
    private val database: MovieDatabase,
    private val geminiService: GeminiMovieService = GeminiMovieService()
) : MovieRepository {

    private val watchlistDao = database.watchlistDao()
    private val userRatingDao = database.userRatingDao()
    private val searchHistoryDao = database.searchHistoryDao()

    // Curated rich movie catalog with high-definition artwork URLs and full movie details
    private val curatedMovies = listOf(
        Movie(
            id = 1,
            title = "Dune: Part Two",
            overview = "Paul Atreides unites with Chani and the Fremen while seeking revenge against the conspirators who destroyed his family. Facing a choice between the love of his life and the fate of the universe.",
            posterUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1200&auto=format&fit=crop&q=80",
            rating = 8.6,
            releaseYear = "2024",
            durationMinutes = 166,
            genres = listOf("Sci-Fi", "Adventure", "Action"),
            isFeatured = true,
            category = "Trending",
            ageRating = "PG-13",
            director = "Denis Villeneuve",
            voteCount = 2840
        ),
        Movie(
            id = 2,
            title = "Oppenheimer",
            overview = "The story of American scientist J. Robert Oppenheimer and his role in the development of the atomic bomb during World War II.",
            posterUrl = "https://images.unsplash.com/photo-1440404653325-ab127d49abc1?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=1200&auto=format&fit=crop&q=80",
            rating = 8.9,
            releaseYear = "2023",
            durationMinutes = 180,
            genres = listOf("Biography", "Drama", "History"),
            isFeatured = true,
            category = "Top Rated",
            ageRating = "R",
            director = "Christopher Nolan",
            voteCount = 3910
        ),
        Movie(
            id = 3,
            title = "Spider-Man: Across the Spider-Verse",
            overview = "Miles Morales catapults across the Multiverse, where he encounters a team of Spider-People charged with protecting its very existence.",
            posterUrl = "https://images.unsplash.com/photo-1635805737707-575885ab0820?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=1200&auto=format&fit=crop&q=80",
            rating = 8.7,
            releaseYear = "2023",
            durationMinutes = 140,
            genres = listOf("Animation", "Action", "Adventure"),
            isFeatured = true,
            category = "Trending",
            ageRating = "PG",
            director = "Joaquim Dos Santos",
            voteCount = 2150
        ),
        Movie(
            id = 4,
            title = "Interstellar",
            overview = "When Earth becomes uninhabitable, a team of ex-NASA pilots and researchers undertake a perilous space mission through a wormhole.",
            posterUrl = "https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?w=1200&auto=format&fit=crop&q=80",
            rating = 8.7,
            releaseYear = "2014",
            durationMinutes = 169,
            genres = listOf("Sci-Fi", "Drama", "Adventure"),
            isFeatured = false,
            category = "Top Rated",
            ageRating = "PG-13",
            director = "Christopher Nolan",
            voteCount = 5200
        ),
        Movie(
            id = 5,
            title = "The Dark Knight",
            overview = "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological tests of his ability to fight injustice.",
            posterUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1514539079130-25950c84af65?w=1200&auto=format&fit=crop&q=80",
            rating = 9.0,
            releaseYear = "2008",
            durationMinutes = 152,
            genres = listOf("Action", "Crime", "Drama"),
            isFeatured = false,
            category = "Top Rated",
            ageRating = "PG-13",
            director = "Christopher Nolan",
            voteCount = 8900
        ),
        Movie(
            id = 6,
            title = "Blade Runner 2049",
            overview = "Young Blade Runner K's discovery of a long-buried secret leads him to track down former Blade Runner Rick Deckard, who's been missing for thirty years.",
            posterUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=1200&auto=format&fit=crop&q=80",
            rating = 8.0,
            releaseYear = "2017",
            durationMinutes = 164,
            genres = listOf("Sci-Fi", "Mystery", "Thriller"),
            isFeatured = false,
            category = "Trending",
            ageRating = "R",
            director = "Denis Villeneuve",
            voteCount = 3100
        ),
        Movie(
            id = 7,
            title = "Cyberpunk Neo Metropolis",
            overview = "In a futuristic neon-drenched metropolis, a rogue hacker uncover a shadow AI conspiracy threatening to rewire human memory.",
            posterUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=1200&auto=format&fit=crop&q=80",
            rating = 8.4,
            releaseYear = "2025",
            durationMinutes = 135,
            genres = listOf("Sci-Fi", "Action", "Cyberpunk"),
            isFeatured = true,
            category = "Coming Soon",
            ageRating = "PG-13",
            director = "Aria Vance",
            voteCount = 890
        ),
        Movie(
            id = 8,
            title = "The Grand Budapest Hotel",
            overview = "A writer encounters the owner of a high-class European hotel who tells him of his early years as a lobby boy during the hotel's glorious golden age.",
            posterUrl = "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?w=1200&auto=format&fit=crop&q=80",
            rating = 8.1,
            releaseYear = "2014",
            durationMinutes = 99,
            genres = listOf("Comedy", "Adventure"),
            isFeatured = false,
            category = "Popular",
            ageRating = "R",
            director = "Wes Anderson",
            voteCount = 2700
        )
    )

    override fun getMoviesByCategory(category: String): Flow<List<Movie>> = flow {
        if (category == "All") {
            emit(curatedMovies)
        } else if (category == "Trending" || category == "Top Rated" || category == "Coming Soon" || category == "Popular") {
            val filtered = curatedMovies.filter { it.category == category || it.isFeatured }
            emit(if (filtered.isNotEmpty()) filtered else curatedMovies)
        } else {
            val filtered = curatedMovies.filter { movie ->
                movie.genres.any { it.equals(category, ignoreCase = true) }
            }
            emit(if (filtered.isNotEmpty()) filtered else curatedMovies)
        }
    }

    override fun getMovieDetail(movieId: Int): Flow<MovieDetail?> = flow {
        val movie = curatedMovies.find { it.id == movieId } ?: curatedMovies.first()
        val cast = listOf(
            CastMember(101, "Timothée Chalamet", "Paul Atreides", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=300&auto=format&fit=crop&q=80"),
            CastMember(102, "Zendaya", "Chani", "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=300&auto=format&fit=crop&q=80"),
            CastMember(103, "Rebecca Ferguson", "Lady Jessica", "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=300&auto=format&fit=crop&q=80"),
            CastMember(104, "Javier Bardem", "Stilgar", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&auto=format&fit=crop&q=80"),
            CastMember(105, "Austin Butler", "Feyd-Rautha", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=300&auto=format&fit=crop&q=80")
        )
        val trailers = listOf(
            VideoTrailer("t1", "Official Teaser Trailer", "Way9Dexny3w", "Trailer"),
            VideoTrailer("t2", "Final Cinematic Trailer", "qEVUtrk8_B4", "Trailer"),
            VideoTrailer("t3", "Behind The Scenes & VFX", "7d_jQycdQGo", "Featurette")
        )
        val reviews = listOf(
            Review("r1", "CinemaVeritas", "An absolute cinematic landmark. The sound design, visuals, and emotional weight elevate this beyond standard sci-fi.", 9.5, "Yesterday"),
            Review("r2", "FilmFanatic99", "Masterful direction and epic scope. Denis Villeneuve continues to deliver jaw-dropping cinema.", 9.0, "3 days ago")
        )
        val similar = curatedMovies.filter { it.id != movie.id }.take(4)
        val aiInsight = "✨ **CineVerse AI Insight:** Viewers who loved ${movie.title} praised its breathtaking scale, world-building, and masterful soundtrack score. Recommended for fans of atmospheric sci-fi and epic character arcs."

        emit(MovieDetail(movie, cast, trailers, reviews, similar, aiInsight))
    }

    override fun searchMovies(query: String, selectedGenre: String?): Flow<List<Movie>> = flow {
        var results = curatedMovies
        if (query.isNotBlank()) {
            results = results.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.overview.contains(query, ignoreCase = true) ||
                        it.director.contains(query, ignoreCase = true)
            }
        }
        if (!selectedGenre.isNullOrBlank() && selectedGenre != "All") {
            results = results.filter { m ->
                m.genres.any { it.equals(selectedGenre, ignoreCase = true) }
            }
        }
        emit(results)
    }

    override fun getWatchlist(): Flow<List<WatchlistEntity>> = watchlistDao.getAllWatchlist()

    override fun getFavorites(): Flow<List<WatchlistEntity>> = watchlistDao.getFavorites()

    override fun getWatched(): Flow<List<WatchlistEntity>> = watchlistDao.getWatched()

    override fun isInWatchlist(movieId: Int): Flow<Boolean> = watchlistDao.isInWatchlist(movieId)

    override suspend fun toggleWatchlist(movie: Movie) {
        val exists = watchlistDao.isInWatchlist(movie.id).firstOrNull() ?: false
        if (exists) {
            watchlistDao.removeFromWatchlist(movie.id)
        } else {
            watchlistDao.insertToWatchlist(
                WatchlistEntity(
                    id = movie.id,
                    title = movie.title,
                    overview = movie.overview,
                    posterUrl = movie.posterUrl,
                    backdropUrl = movie.backdropUrl,
                    rating = movie.rating,
                    releaseYear = movie.releaseYear,
                    genresCsv = movie.genres.joinToString(",")
                )
            )
        }
    }

    override suspend fun toggleFavorite(movieId: Int, isFavorite: Boolean) {
        watchlistDao.setFavoriteStatus(movieId, isFavorite)
    }

    override suspend fun toggleWatched(movieId: Int, isWatched: Boolean) {
        watchlistDao.setWatchedStatus(movieId, isWatched)
    }

    override fun getUserRating(movieId: Int): Flow<UserRatingEntity?> =
        userRatingDao.getUserRatingForMovie(movieId)

    override suspend fun saveUserRating(movieId: Int, ratingStars: Float, reviewText: String) {
        userRatingDao.saveUserRating(
            UserRatingEntity(movieId = movieId, ratingStars = ratingStars, userReviewText = reviewText)
        )
    }

    override fun getRecentSearches(): Flow<List<SearchHistoryEntity>> = searchHistoryDao.getRecentSearches()

    override suspend fun addSearchQuery(query: String) {
        if (query.isNotBlank()) {
            searchHistoryDao.insertSearchQuery(SearchHistoryEntity(query = query.trim()))
        }
    }

    override suspend fun clearSearchHistory() {
        searchHistoryDao.clearHistory()
    }

    override suspend fun getAiRecommendation(prompt: String): String {
        return geminiService.generateMovieAdvice(prompt)
    }
}
