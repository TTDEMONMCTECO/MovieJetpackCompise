package com.example.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String,
    val backdropUrl: String,
    val rating: Double,
    val releaseYear: String,
    val durationMinutes: Int = 120,
    val genres: List<String> = emptyList(),
    val isFeatured: Boolean = false,
    val category: String = "Trending",
    val ageRating: String = "PG-13",
    val director: String = "Unknown Director",
    val voteCount: Int = 1420
)

data class CastMember(
    val id: Int,
    val name: String,
    val character: String,
    val profileUrl: String
)

data class VideoTrailer(
    val id: String,
    val title: String,
    val youtubeKey: String,
    val type: String = "Trailer"
)

data class Review(
    val id: String,
    val author: String,
    val content: String,
    val rating: Double,
    val date: String
)

data class MovieDetail(
    val movie: Movie,
    val cast: List<CastMember>,
    val trailers: List<VideoTrailer>,
    val reviews: List<Review>,
    val similarMovies: List<Movie>,
    val aiInsight: String = ""
)
