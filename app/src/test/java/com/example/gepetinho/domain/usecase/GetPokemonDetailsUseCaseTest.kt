package com.example.gepetinho.domain.usecase

import com.example.gepetinho.domain.model.PokemonDetails
import com.example.gepetinho.testing.FakePokemonRepository
import kotlinx.coroutines.flow.first
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

        val result = useCase(pokemonId = 25).first()

        assertEquals(25, repository.observedPokemonDetailsId)
        assertEquals(details, result)
    }
}
