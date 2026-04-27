package com.example.gepetinho.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon")
data class PokemonEntity(
    @PrimaryKey val pokemonId: Int,
    val name: String,
    val imageUrl: String?,
    val isFavorite: Boolean = false
)
