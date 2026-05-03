package com.example.gepetinho.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gepetinho.data.local.entity.FavoritePokemonEntity
import com.example.gepetinho.data.local.entity.PokemonDetailsEntity
import com.example.gepetinho.data.local.entity.PokemonEntity

@Database(
    entities = [
        PokemonEntity::class,
        PokemonDetailsEntity::class,
        FavoritePokemonEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class GepetinhoDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
}
