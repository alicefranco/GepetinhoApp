package com.example.gepetinho.domain.usecase

import com.example.gepetinho.domain.model.Pokemon
import com.example.gepetinho.domain.model.PokemonDetails
import com.example.gepetinho.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetPokemonUseCaseTest {

    @Test
    fun `invoke observes pokemon from repository`() = runTest {
        val pokemon = listOf(
            Pokemon(
                id = 1,
                name = "bulbasaur",
                imageUrl = "https://example.com/bulbasaur.png",
                isFavorite = false
            ),
            Pokemon(
                id = 4,
                name = "charmander",
                imageUrl = "https://example.com/charmander.png",
                isFavorite = true
            )
        )
        val repository = FakePokemonRepository(pokemon = pokemon)
        val useCase = GetPokemonUseCase(repository)

        val result = useCase().single()

        assertEquals(pokemon, result)
    }

    private class FakePokemonRepository(
        private val pokemon: List<Pokemon>
    ) : PokemonRepository {
        override fun observePokemon(): Flow<List<Pokemon>> = flowOf(pokemon)

        override fun observePokemonDetails(pokemonId: Int): Flow<PokemonDetails?> {
            error("Not used in this test.")
        }

        override suspend fun refreshPokemon(limit: Int, offset: Int) = Unit

        override suspend fun refreshPokemonDetails(pokemonId: Int) = Unit

        override suspend fun toggleFavorite(pokemonId: Int) = Unit
    }
}
