package com.example.gepetinho.presentation.list

import com.example.gepetinho.domain.model.Pokemon
import com.example.gepetinho.domain.usecase.GetPokemonUseCase
import com.example.gepetinho.domain.usecase.TogglePokemonFavoriteUseCase
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
class ListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `uiState exposes pokemon from repository`() = runTest {
        val pokemon = listOf(
            Pokemon(1, "bulbasaur", null, isFavorite = false),
            Pokemon(25, "pikachu", null, isFavorite = true)
        )
        val repository = FakePokemonRepository(pokemon = pokemon)
        val viewModel = createViewModel(repository)
        collectUiState(viewModel)

        val uiState = viewModel.uiState.value
        assertEquals(pokemon, uiState.items)
        assertFalse(uiState.isLoading)
        assertNull(uiState.errorMessage)
    }

    @Test
    fun `search query filters pokemon by name and id`() = runTest {
        val repository = FakePokemonRepository(
            pokemon = listOf(
                Pokemon(1, "bulbasaur", null, isFavorite = false),
                Pokemon(25, "pikachu", null, isFavorite = true)
            )
        )
        val viewModel = createViewModel(repository)
        collectUiState(viewModel)

        viewModel.onSearchQueryChanged("25")

        assertEquals(listOf(Pokemon(25, "pikachu", null, isFavorite = true)), viewModel.uiState.value.items)
        assertEquals("25", viewModel.uiState.value.searchQuery)
    }

    @Test
    fun `favorites only filters non favorite pokemon`() = runTest {
        val favorite = Pokemon(25, "pikachu", null, isFavorite = true)
        val repository = FakePokemonRepository(
            pokemon = listOf(
                Pokemon(1, "bulbasaur", null, isFavorite = false),
                favorite
            )
        )
        val viewModel = createViewModel(repository)
        collectUiState(viewModel)

        viewModel.onFavoritesOnlyChanged(true)

        assertEquals(listOf(favorite), viewModel.uiState.value.items)
        assertEquals(true, viewModel.uiState.value.showFavoritesOnly)
    }

    @Test
    fun `refreshPokemon shows error when repository refresh fails`() = runTest {
        val repository = FakePokemonRepository(
            pokemon = emptyList(),
            refreshPokemonFailure = IllegalStateException("Network error")
        )
        val viewModel = createViewModel(repository)
        collectUiState(viewModel)

        viewModel.refreshPokemon()

        val uiState = viewModel.uiState.value
        assertEquals("Could not refresh Pokemon. Showing saved results.", uiState.errorMessage)
        assertFalse(uiState.isLoading)
    }

    @Test
    fun `toggleFavorite delegates selected id`() = runTest {
        val repository = FakePokemonRepository(pokemon = emptyList())
        val viewModel = createViewModel(repository)
        collectUiState(viewModel)

        viewModel.toggleFavorite(25)

        assertEquals(25, repository.toggledPokemonId)
    }

    @Test
    fun `clearError removes current error`() = runTest {
        val repository = FakePokemonRepository(
            pokemon = emptyList(),
            refreshPokemonFailure = IllegalStateException("Network error")
        )
        val viewModel = createViewModel(repository)
        collectUiState(viewModel)

        viewModel.refreshPokemon()
        viewModel.clearError()

        assertNull(viewModel.uiState.value.errorMessage)
    }

    private fun createViewModel(repository: FakePokemonRepository): ListViewModel {
        return ListViewModel(
            getPokemonUseCase = GetPokemonUseCase(repository),
            togglePokemonFavoriteUseCase = TogglePokemonFavoriteUseCase(repository),
            pokemonRepository = repository
        )
    }

    private fun TestScope.collectUiState(viewModel: ListViewModel) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
    }
}
