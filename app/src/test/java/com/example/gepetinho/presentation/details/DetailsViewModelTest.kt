package com.example.gepetinho.presentation.details

import androidx.lifecycle.SavedStateHandle
import com.example.gepetinho.domain.model.PokemonDetails
import com.example.gepetinho.domain.usecase.GetPokemonDetailsUseCase
import com.example.gepetinho.domain.usecase.RefreshPokemonDetailsUseCase
import com.example.gepetinho.domain.usecase.TogglePokemonFavoriteUseCase
import com.example.gepetinho.navigation.Screen
import com.example.gepetinho.presentation.signup.MainDispatcherRule
import com.example.gepetinho.testing.FakePokemonRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `uiState exposes pokemon details from repository`() = runTest {
        val details = pokemonDetails()
        val repository = FakePokemonRepository(details = details)
        val viewModel = createViewModel(repository)
        collectUiState(viewModel)

        val uiState = viewModel.uiState.value
        assertEquals(details, uiState.pokemon)
        assertFalse(uiState.isLoading)
        assertNull(uiState.errorMessage)
        assertEquals(25, repository.refreshedPokemonDetailsId)
    }

    @Test
    fun `refreshPokemonDetails shows error when refresh fails`() = runTest {
        val repository = FakePokemonRepository(
            details = pokemonDetails(),
            refreshPokemonDetailsFailure = IllegalStateException("Network error")
        )
        val viewModel = createViewModel(repository)
        collectUiState(viewModel)

        viewModel.refreshPokemonDetails()

        val uiState = viewModel.uiState.value
        assertEquals("Could not refresh Pokemon details. Showing saved details.", uiState.errorMessage)
        assertFalse(uiState.isLoading)
    }

    @Test
    fun `toggleFavorite delegates pokemon id`() = runTest {
        val repository = FakePokemonRepository(details = pokemonDetails())
        val viewModel = createViewModel(repository)
        collectUiState(viewModel)

        viewModel.toggleFavorite()

        assertEquals(25, repository.toggledPokemonId)
    }

    @Test
    fun `clearError removes current error`() = runTest {
        val repository = FakePokemonRepository(
            details = pokemonDetails(),
            refreshPokemonDetailsFailure = IllegalStateException("Network error")
        )
        val viewModel = createViewModel(repository)
        collectUiState(viewModel)

        viewModel.refreshPokemonDetails()
        viewModel.clearError()

        assertNull(viewModel.uiState.value.errorMessage)
    }

    private fun createViewModel(repository: FakePokemonRepository): DetailsViewModel {
        return DetailsViewModel(
            savedStateHandle = SavedStateHandle(
                mapOf(Screen.Details.POKEMON_ID_ARG to 25)
            ),
            getPokemonDetailsUseCase = GetPokemonDetailsUseCase(repository),
            togglePokemonFavoriteUseCase = TogglePokemonFavoriteUseCase(repository),
            refreshPokemonDetailsUseCase = RefreshPokemonDetailsUseCase(repository)
        )
    }

    private fun TestScope.collectUiState(viewModel: DetailsViewModel) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
    }

    private fun pokemonDetails(): PokemonDetails {
        return PokemonDetails(
            id = 25,
            name = "pikachu",
            imageUrl = null,
            height = 4,
            weight = 60,
            baseExperience = 112,
            types = listOf("electric"),
            isFavorite = true
        )
    }
}
