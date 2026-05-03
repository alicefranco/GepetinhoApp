package com.example.gepetinho.data.local.model

data class PokemonDetailsWithFavorite(
    val pokemonId: Int,
    val name: String,
    val imageUrl: String?,
    val height: Int,
    val weight: Int,
    val baseExperience: Int?,
    val types: String,
    val isFavorite: Boolean
)
