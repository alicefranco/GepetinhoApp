package com.example.gepetinho.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.gepetinho.data.local.entity.PokemonDetailsEntity
import com.example.gepetinho.data.local.entity.PokemonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {

    @Query("SELECT * FROM pokemon ORDER BY pokemonId ASC")
    fun observePokemon(): Flow<List<PokemonEntity>>

    @Query("SELECT * FROM pokemon_details WHERE pokemonId = :pokemonId")
    fun observePokemonDetails(pokemonId: Int): Flow<PokemonDetailsEntity?>

    @Query("SELECT * FROM pokemon WHERE pokemonId = :pokemonId LIMIT 1")
    suspend fun getPokemonById(pokemonId: Int): PokemonEntity?

    @Query("SELECT * FROM pokemon_details WHERE pokemonId = :pokemonId LIMIT 1")
    suspend fun getPokemonDetailsById(pokemonId: Int): PokemonDetailsEntity?

    @Query("SELECT pokemonId FROM pokemon WHERE isFavorite = 1")
    suspend fun getFavoritePokemonIds(): List<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPokemon(items: List<PokemonEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPokemonDetails(item: PokemonDetailsEntity)

    @Query("UPDATE pokemon SET isFavorite = :isFavorite WHERE pokemonId = :pokemonId")
    suspend fun updatePokemonFavorite(pokemonId: Int, isFavorite: Boolean)

    @Query("UPDATE pokemon_details SET isFavorite = :isFavorite WHERE pokemonId = :pokemonId")
    suspend fun updatePokemonDetailsFavorite(pokemonId: Int, isFavorite: Boolean)

    @Transaction
    suspend fun toggleFavorite(pokemonId: Int) {
        val currentFavorite = getPokemonById(pokemonId)?.isFavorite
            ?: getPokemonDetailsById(pokemonId)?.isFavorite
            ?: false
        val updatedFavorite = !currentFavorite

        updatePokemonFavorite(pokemonId, updatedFavorite)
        updatePokemonDetailsFavorite(pokemonId, updatedFavorite)
    }
}
