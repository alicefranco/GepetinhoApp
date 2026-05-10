package com.example.gepetinho.domain.usecase

import com.example.gepetinho.testing.FakePokemonRepository
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
}
