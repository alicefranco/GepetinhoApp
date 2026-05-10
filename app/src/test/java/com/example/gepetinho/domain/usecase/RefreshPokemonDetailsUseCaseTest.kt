package com.example.gepetinho.domain.usecase

import com.example.gepetinho.domain.model.Pokemon
import com.example.gepetinho.domain.model.PokemonDetails
import com.example.gepetinho.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RefreshPokemonDetailsUseCaseTest {

    @Test
    fun `invoke refreshes pokemon details with requested id`() = runTest {
        val repository = FakePokemonRepository()
        val useCase = RefreshPokemonDetailsUseCase(repository)

        useCase(pokemonId = 25)

        assertEquals(25, repository.refreshedPokemonDetailsId)
    }

    private class FakePokemonRepository : PokemonRepository {
        var refreshedPokemonDetailsId: Int? = null

        override fun observePokemon(): Flow<List<Pokemon>> = emptyFlow()

        override fun observePokemonDetails(pokemonId: Int): Flow<PokemonDetails?> = emptyFlow()

        override suspend fun refreshPokemon(limit: Int, offset: Int) = Unit

        override suspend fun refreshPokemonDetails(pokemonId: Int) {
            refreshedPokemonDetailsId = pokemonId
        }

        override suspend fun toggleFavorite(pokemonId: Int) = Unit
    }
}
