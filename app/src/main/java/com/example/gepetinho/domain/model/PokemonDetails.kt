package com.example.gepetinho.domain.model

data class PokemonDetails(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val height: Int,
    val weight: Int,
    val baseExperience: Int?,
    val types: List<String>,
    val isFavorite: Boolean
)
