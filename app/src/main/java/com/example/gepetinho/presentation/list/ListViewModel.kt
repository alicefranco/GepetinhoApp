package com.example.gepetinho.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gepetinho.domain.model.Pokemon
import com.example.gepetinho.domain.repository.PokemonRepository
import com.example.gepetinho.domain.repository.PokemonRepository.Companion.DEFAULT_PAGE_SIZE
import com.example.gepetinho.domain.usecase.GetPokemonUseCase
import com.example.gepetinho.domain.usecase.TogglePokemonFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ListViewModel @Inject constructor(
    private val getPokemonUseCase: GetPokemonUseCase,
    private val togglePokemonFavoriteUseCase: TogglePokemonFavoriteUseCase,
    private val pokemonRepository: PokemonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListUiState(isLoading = false))
    val uiState: StateFlow<ListUiState> = _uiState

    private var allPokemon: List<Pokemon> = emptyList()
    private var nextOffset = 0

    init {
        observePokemon()
        refreshPokemon()
    }

    private fun observePokemon() {
        viewModelScope.launch {
            getPokemonUseCase()
                .catch {
                    updateError("Could not load saved Pokemon.")
                    emit(emptyList())
                }
                .collect { pokemon ->
                    allPokemon = pokemon
                    updateFilteredItems()
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update {
            it.copy(searchQuery = query)
        }
        updateFilteredItems()
    }

    fun onFavoritesOnlyChanged(showOnlyFavorites: Boolean) {
        _uiState.update {
            it.copy(showFavoritesOnly = showOnlyFavorites)
        }
        updateFilteredItems()
    }

    fun refreshPokemon() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            runCatching {
                pokemonRepository.refreshPokemon(
                    limit = DEFAULT_PAGE_SIZE,
                    offset = 0
                )
            }.onSuccess { hasMore ->
                nextOffset = DEFAULT_PAGE_SIZE

                _uiState.update {
                    it.copy(canLoadMore = hasMore)
                }
            }.onFailure {
                updateError("Could not refresh Pokemon.")
            }

            _uiState.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun loadNextPage() {
        val state = _uiState.value

        if (
            state.isLoading ||
            state.isLoadingNextPage ||
            !state.canLoadMore
        ) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoadingNextPage = true,
                    errorMessage = null
                )
            }

            val pageOffset = nextOffset

            runCatching {
                pokemonRepository.refreshPokemon(
                    limit = DEFAULT_PAGE_SIZE,
                    offset = pageOffset
                )
            }.onSuccess { hasMore ->
                nextOffset += DEFAULT_PAGE_SIZE

                _uiState.update {
                    it.copy(canLoadMore = hasMore)
                }
            }.onFailure {
                updateError("Could not load more Pokemon.")
            }

            _uiState.update {
                it.copy(isLoadingNextPage = false)
            }
        }
    }

    fun toggleFavorite(pokemonId: Int) {
        viewModelScope.launch {
            runCatching {
                togglePokemonFavoriteUseCase(pokemonId)
            }.onFailure {
                updateError("Could not update favorite.")
            }
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }

    private fun updateFilteredItems() {
        val state = _uiState.value

        val filtered = allPokemon.filterForUi(
            searchQuery = state.searchQuery,
            showFavoritesOnly = state.showFavoritesOnly
        )

        _uiState.update {
            it.copy(items = filtered)
        }
    }

    private fun updateError(message: String) {
        _uiState.update {
            it.copy(errorMessage = message)
        }
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
