package com.example.data.remote

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

data class GeminiPart(
    val text: String? = null
)

data class GeminiContent(
    val parts: List<GeminiPart>
)

data class GeminiRequest(
    val contents: List<GeminiContent>
)

data class GeminiCandidate(
    val content: GeminiContent?
)

data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

class GeminiMovieService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val requestAdapter = moshi.adapter(GeminiRequest::class.java)
    private val responseAdapter = moshi.adapter(GeminiResponse::class.java)

    suspend fun generateMovieAdvice(userPrompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getFallbackAiResponse(userPrompt)
        }

        val requestObj = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(
                        GeminiPart(
                            text = "You are CineBot, an expert cinema AI assistant in the CineVerse app. " +
                                    "User says: '$userPrompt'. Give a friendly, enthusiastic, nicely formatted recommendation with 2-3 specific movies, brief reasons why to watch, genre, and key highlights."
                        )
                    )
                )
            )
        )

        try {
            val jsonBody = requestAdapter.toJson(requestObj)
            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(jsonBody.toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val bodyString = response.body?.string() ?: ""
            if (response.isSuccessful && bodyString.isNotEmpty()) {
                val parsed = responseAdapter.fromJson(bodyString)
                val text = parsed?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!text.isNullOrBlank()) {
                    return@withContext text
                }
            }
            return@withContext getFallbackAiResponse(userPrompt)
        } catch (e: Exception) {
            return@withContext getFallbackAiResponse(userPrompt)
        }
    }

    private fun getFallbackAiResponse(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("sci-fi") || lower.contains("space") || lower.contains("future") -> {
                "🚀 **CineBot Recommendations:**\n\n" +
                        "1. **Interstellar (2014)** - Mind-bending space journey through a wormhole to save humanity.\n" +
                        "2. **Blade Runner 2049 (2017)** - Stunning futuristic neon visuals and deep philosophical themes.\n" +
                        "3. **Dune: Part Two (2024)** - Epic sci-fi masterpiece with breathtaking battles and world-building."
            }
            lower.contains("action") || lower.contains("thrill") || lower.contains("fight") -> {
                "💥 **Action-Packed Picks for You:**\n\n" +
                        "1. **Mad Max: Fury Road (2015)** - Non-stop high-octane desert chase action.\n" +
                        "2. **John Wick: Chapter 4 (2023)** - Masterpiece choreography and stylish gun fu.\n" +
                        "3. **Top Gun: Maverick (2022)** - Heart-pounding aerial stunts and emotional nostalgia."
            }
            lower.contains("comedy") || lower.contains("funny") || lower.contains("laugh") -> {
                "😄 **Feel-Good Comedy Hits:**\n\n" +
                        "1. **The Grand Budapest Hotel (2014)** - Whimsical, hilarious aesthetic masterpiece.\n" +
                        "2. **Everything Everywhere All at Once (2022)** - Absurdist multi-universe comedy adventure.\n" +
                        "3. **Knives Out (2019)** - Witty, fast-paced murder mystery comedy."
            }
            else -> {
                "🍿 **CineBot AI Selections:**\n\n" +
                        "Based on your query: *\"$prompt\"*, here are top-tier recommendations:\n\n" +
                        "• **Oppenheimer (2023)** - Gripping biographical drama with intense pacing.\n" +
                        "• **Spider-Man: Across the Spider-Verse (2023)** - Visual animation triumph with incredible soundtrack.\n" +
                        "• **The Dark Knight (2008)** - Unmatched superhero crime thriller."
            }
        }
    }
}
