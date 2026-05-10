package com.example.gepetinho.domain.usecase

import com.example.gepetinho.domain.model.Pokemon
import com.example.gepetinho.domain.model.PokemonDetails
import com.example.gepetinho.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetPokemonDetailsUseCaseTest {

    @Test
    fun `invoke observes pokemon details for requested id`() = runTest {
        val details = PokemonDetails(
            id = 25,
            name = "pikachu",
            imageUrl = "https://example.com/pikachu.png",
            height = 4,
            weight = 60,
            baseExperience = 112,
            types = listOf("electric"),
            isFavorite = true
        )
        val repository = FakePokemonRepository(details = details)
        val useCase = GetPokemonDetailsUseCase(repository)

        val result = useCase(pokemonId = 25).single()

        assertEquals(25, repository.observedPokemonDetailsId)
        assertEquals(details, result)
    }

    private class FakePokemonRepository(
        private val details: PokemonDetails?
    ) : PokemonRepository {
        var observedPokemonDetailsId: Int? = null

        override fun observePokemon(): Flow<List<Pokemon>> = emptyFlow()

        override fun observePokemonDetails(pokemonId: Int): Flow<PokemonDetails?> {
            observedPokemonDetailsId = pokemonId
            return flowOf(details)
        }

        override suspend fun refreshPokemon(limit: Int, offset: Int) = Unit

        override suspend fun refreshPokemonDetails(pokemonId: Int) = Unit

        override suspend fun toggleFavorite(pokemonId: Int) = Unit
    }
}
