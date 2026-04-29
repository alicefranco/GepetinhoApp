package com.example.gepetinho.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gepetinho.domain.model.Pokemon
import com.example.gepetinho.domain.repository.PokemonRepository
import com.example.gepetinho.domain.usecase.GetPokemonUseCase
import com.example.gepetinho.domain.usecase.TogglePokemonFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ListViewModel @Inject constructor(
    getPokemonUseCase: GetPokemonUseCase,
    private val togglePokemonFavoriteUseCase: TogglePokemonFavoriteUseCase,
    private val pokemonRepository: PokemonRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val showFavoritesOnly = MutableStateFlow(false)
    private val isLoading = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ListUiState> = combine(
        getPokemonUseCase()
            .catch {
                errorMessage.value = "Could not load saved Pokemon."
                emit(emptyList())
            },
        searchQuery,
        showFavoritesOnly,
        isLoading,
        errorMessage
    ) { items, query, favoritesOnly, loading, error ->
        ListUiState(
            items = items.filterForUi(query, favoritesOnly),
            searchQuery = query,
            showFavoritesOnly = favoritesOnly,
            isLoading = loading,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ListUiState(isLoading = true)
    )

    init {
        refreshPokemon()
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }

    fun onFavoritesOnlyChanged(showOnlyFavorites: Boolean) {
        showFavoritesOnly.value = showOnlyFavorites
    }

    fun refreshPokemon() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            runCatching {
                pokemonRepository.refreshPokemon()
            }.onFailure {
                errorMessage.value = "Could not refresh Pokemon. Showing saved results."
            }

            isLoading.value = false
        }
    }

    fun toggleFavorite(pokemonId: Int) {
        viewModelScope.launch {
            runCatching {
                togglePokemonFavoriteUseCase(pokemonId)
            }.onFailure {
                errorMessage.value = "Could not update favorite."
            }
        }
    }

    fun clearError() {
        errorMessage.update { null }
    }
}

private fun List<Pokemon>.filterForUi(
    searchQuery: String,
    showFavoritesOnly: Boolean
): List<Pokemon> {
    val normalizedQuery = searchQuery.trim().lowercase()

    return filter { pokemon ->
        val matchesFavoriteFilter = !showFavoritesOnly || pokemon.isFavorite
        val matchesSearch = normalizedQuery.isBlank() ||
            pokemon.name.contains(normalizedQuery, ignoreCase = true) ||
            pokemon.id.toString().contains(normalizedQuery)

        matchesFavoriteFilter && matchesSearch
    }
}
