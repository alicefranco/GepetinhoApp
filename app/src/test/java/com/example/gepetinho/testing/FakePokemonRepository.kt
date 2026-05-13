package com.example.gepetinho.testing

import com.example.gepetinho.domain.model.Pokemon
import com.example.gepetinho.domain.model.PokemonDetails
import com.example.gepetinho.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakePokemonRepository(
    pokemon: List<Pokemon> = emptyList(),
    details: PokemonDetails? = null,
    private val refreshPokemonFailure: Throwable? = null,
    private val refreshPokemonDetailsFailure: Throwable? = null,
    private val hasMorePokemon: Boolean = false
) : PokemonRepository {

    private val pokemonFlow = MutableStateFlow(pokemon)
    private val detailsFlow = MutableStateFlow(details)

    var observedPokemonDetailsId: Int? = null
        private set
    var refreshedPokemonDetailsId: Int? = null
        private set
    val refreshedPokemonPages = mutableListOf<Pair<Int, Int>>()
    var toggledPokemonId: Int? = null
        private set

    override fun observePokemon(): Flow<List<Pokemon>> = pokemonFlow

    override fun observePokemonDetails(pokemonId: Int): Flow<PokemonDetails?> {
        observedPokemonDetailsId = pokemonId
        return detailsFlow
    }

    override suspend fun refreshPokemon(limit: Int, offset: Int): Boolean {
        refreshedPokemonPages += limit to offset
        refreshPokemonFailure?.let { throw it }
        return hasMorePokemon
    }

    override suspend fun refreshPokemonDetails(pokemonId: Int) {
        refreshedPokemonDetailsId = pokemonId
        refreshPokemonDetailsFailure?.let { throw it }
    }

    override suspend fun toggleFavorite(pokemonId: Int) {
        toggledPokemonId = pokemonId
    }
}
