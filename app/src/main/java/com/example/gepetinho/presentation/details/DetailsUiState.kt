package com.example.gepetinho.presentation.details

import com.example.gepetinho.domain.model.PokemonDetails

data class DetailsUiState(
    val pokemon: PokemonDetails? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
