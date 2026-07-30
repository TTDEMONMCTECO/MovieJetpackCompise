package com.example.presentation.aimatch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "USER" or "CINEBOT"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class AiMatchUiState(
    val messages: List<ChatMessage> = listOf(
        ChatMessage(
            sender = "CINEBOT",
            text = "🎬 **Welcome to CineBot AI!**\nI'm your personal movie assistant. Tell me what mood you're in, your favorite actors, or ask for a custom watchlist recommendation!"
        )
    ),
    val inputText: String = "",
    val isLoading: Boolean = false
)

class AiMatchViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiMatchUiState())
    val uiState: StateFlow<AiMatchUiState> = _uiState.asStateFlow()

    fun onInputChanged(newText: String) {
        _uiState.value = _uiState.value.copy(inputText = newText)
    }

    fun sendMessage(textToSend: String = _uiState.value.inputText) {
        if (textToSend.isBlank()) return

        val userMsg = ChatMessage(sender = "USER", text = textToSend)
        val currentMsgs = _uiState.value.messages + userMsg

        _uiState.value = _uiState.value.copy(
            messages = currentMsgs,
            inputText = "",
            isLoading = true
        )

        viewModelScope.launch {
            val response = repository.getAiRecommendation(textToSend)
            val botMsg = ChatMessage(sender = "CINEBOT", text = response)
            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages + botMsg,
                isLoading = false
            )
        }
    }
}
