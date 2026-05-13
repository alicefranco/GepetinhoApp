package com.example.gepetinho.presentation.list

import com.example.gepetinho.domain.model.Pokemon

data class ListUiState(
    val items: List<Pokemon> = emptyList(),
    val searchQuery: String = "",
    val showFavoritesOnly: Boolean = false,
    val isLoading: Boolean = false,
    val isLoadingNextPage: Boolean = false,
    val canLoadMore: Boolean = true,
    val errorMessage: String? = null
)
