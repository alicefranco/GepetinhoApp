package com.example.gepetinho.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gepetinho.data.local.entity.PokemonDetailsEntity
import com.example.gepetinho.data.local.entity.PokemonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {

    @Query("SELECT * FROM pokemon ORDER BY pokemonId ASC")
    fun observePokemon(): Flow<List<PokemonEntity>>

    @Query("SELECT * FROM pokemon_details WHERE pokemonId = :pokemonId")
    fun observePokemonDetails(pokemonId: Int): Flow<PokemonDetailsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPokemon(items: List<PokemonEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPokemonDetails(item: PokemonDetailsEntity)
}
