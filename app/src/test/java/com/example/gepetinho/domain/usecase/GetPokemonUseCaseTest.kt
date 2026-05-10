package com.example.gepetinho.domain.usecase

import com.example.gepetinho.domain.model.Pokemon
import com.example.gepetinho.testing.FakePokemonRepository
import kotlinx.coroutines.flow.first
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

        val result = useCase().first()

        assertEquals(pokemon, result)
    }
}
