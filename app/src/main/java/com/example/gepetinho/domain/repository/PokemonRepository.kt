package com.example.gepetinho.domain.repository

import com.example.gepetinho.domain.model.Pokemon
import com.example.gepetinho.domain.model.PokemonDetails
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    fun observePokemon(): Flow<List<Pokemon>>
    fun observePokemonDetails(pokemonId: Int): Flow<PokemonDetails?>
    suspend fun refreshPokemon(limit: Int = DEFAULT_PAGE_SIZE, offset: Int = 0): Boolean
    suspend fun refreshPokemonDetails(pokemonId: Int)
    suspend fun toggleFavorite(pokemonId: Int)

    companion object {
        const val DEFAULT_PAGE_SIZE = 50
    }
}
