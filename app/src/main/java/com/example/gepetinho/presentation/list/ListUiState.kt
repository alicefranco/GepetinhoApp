package com.example.gepetinho.presentation.list

import com.example.gepetinho.domain.model.Pokemon

data class ListUiState(
    val items: List<Pokemon> = emptyList(),
    val searchQuery: String = "",
    val showFavoritesOnly: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
