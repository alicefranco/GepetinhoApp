package com.example.gepetinho.domain.usecase

import com.example.gepetinho.domain.model.Pokemon
import com.example.gepetinho.domain.model.PokemonDetails
import com.example.gepetinho.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class TogglePokemonFavoriteUseCaseTest {

    @Test
    fun `invoke toggles favorite for requested id`() = runTest {
        val repository = FakePokemonRepository()
        val useCase = TogglePokemonFavoriteUseCase(repository)

        useCase(pokemonId = 7)

        assertEquals(7, repository.toggledPokemonId)
    }

    private class FakePokemonRepository : PokemonRepository {
        var toggledPokemonId: Int? = null

        override fun observePokemon(): Flow<List<Pokemon>> = emptyFlow()

        override fun observePokemonDetails(pokemonId: Int): Flow<PokemonDetails?> = emptyFlow()

        override suspend fun refreshPokemon(limit: Int, offset: Int) = Unit

        override suspend fun refreshPokemonDetails(pokemonId: Int) = Unit

        override suspend fun toggleFavorite(pokemonId: Int) {
            toggledPokemonId = pokemonId
        }
    }
}
