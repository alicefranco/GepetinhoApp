package com.example.gepetinho.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.gepetinho.data.local.entity.FavoritePokemonEntity
import com.example.gepetinho.data.local.entity.PokemonDetailsEntity
import com.example.gepetinho.data.local.entity.PokemonEntity
import com.example.gepetinho.data.local.model.PokemonDetailsWithFavorite
import com.example.gepetinho.data.local.model.PokemonWithFavorite
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {

    @Query(
        """
        SELECT
            pokemon.pokemonId,
            pokemon.name,
            pokemon.imageUrl,
            favorite_pokemon.userId IS NOT NULL AS isFavorite
        FROM pokemon
        LEFT JOIN favorite_pokemon
            ON favorite_pokemon.pokemonId = pokemon.pokemonId
            AND favorite_pokemon.userId = :userId
        ORDER BY pokemon.pokemonId ASC
        """
    )
    fun observePokemon(userId: String): Flow<List<PokemonWithFavorite>>

    @Query(
        """
        SELECT
            pokemon_details.pokemonId,
            pokemon_details.name,
            pokemon_details.imageUrl,
            pokemon_details.height,
            pokemon_details.weight,
            pokemon_details.baseExperience,
            pokemon_details.types,
            favorite_pokemon.userId IS NOT NULL AS isFavorite
        FROM pokemon_details
        LEFT JOIN favorite_pokemon
            ON favorite_pokemon.pokemonId = pokemon_details.pokemonId
            AND favorite_pokemon.userId = :userId
        WHERE pokemon_details.pokemonId = :pokemonId
        """
    )
    fun observePokemonDetails(userId: String, pokemonId: Int): Flow<PokemonDetailsWithFavorite?>

    @Query("SELECT * FROM pokemon WHERE pokemonId = :pokemonId LIMIT 1")
    suspend fun getPokemonById(pokemonId: Int): PokemonEntity?

    @Query("SELECT * FROM favorite_pokemon WHERE userId = :userId AND pokemonId = :pokemonId LIMIT 1")
    suspend fun getFavoritePokemon(userId: String, pokemonId: Int): FavoritePokemonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFavoritePokemon(favorite: FavoritePokemonEntity)

    @Query("DELETE FROM favorite_pokemon WHERE userId = :userId AND pokemonId = :pokemonId")
    suspend fun deleteFavoritePokemon(userId: String, pokemonId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPokemon(items: List<PokemonEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPokemonDetails(item: PokemonDetailsEntity)

    @Transaction
    suspend fun toggleFavorite(userId: String, pokemonId: Int) {
        if (getFavoritePokemon(userId, pokemonId) == null) {
            upsertFavoritePokemon(FavoritePokemonEntity(userId = userId, pokemonId = pokemonId))
        } else {
            deleteFavoritePokemon(userId, pokemonId)
        }
    }
}
