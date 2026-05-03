package com.example.gepetinho.data.local.model

data class PokemonWithFavorite(
    val pokemonId: Int,
    val name: String,
    val imageUrl: String?,
    val isFavorite: Boolean
)
