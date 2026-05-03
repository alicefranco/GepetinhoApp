package com.example.gepetinho.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "favorite_pokemon",
    primaryKeys = ["userId", "pokemonId"]
)
data class FavoritePokemonEntity(
    val userId: String,
    val pokemonId: Int
)
