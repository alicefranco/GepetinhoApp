package com.example.gepetinho.domain.usecase

import com.example.gepetinho.testing.FakePokemonRepository
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
}
