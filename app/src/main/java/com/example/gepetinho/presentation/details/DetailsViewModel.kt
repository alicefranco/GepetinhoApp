package com.example.gepetinho.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gepetinho.domain.usecase.GetPokemonDetailsUseCase
import com.example.gepetinho.domain.usecase.RefreshPokemonDetailsUseCase
import com.example.gepetinho.domain.usecase.TogglePokemonFavoriteUseCase
import com.example.gepetinho.navigation.Screen
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
class DetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getPokemonDetailsUseCase: GetPokemonDetailsUseCase,
    private val togglePokemonFavoriteUseCase: TogglePokemonFavoriteUseCase,
    private val refreshPokemonDetailsUseCase: RefreshPokemonDetailsUseCase
) : ViewModel() {

    private val pokemonId: Int = checkNotNull(savedStateHandle[Screen.Details.POKEMON_ID_ARG])
    private val isLoading = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<DetailsUiState> = combine(
        getPokemonDetailsUseCase(pokemonId)
            .catch {
                errorMessage.value = "Could not load saved Pokemon details."
                emit(null)
            },
        isLoading,
        errorMessage
    ) { pokemon, loading, error ->
        DetailsUiState(
            pokemon = pokemon,
            isLoading = loading,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DetailsUiState(isLoading = true)
    )

    init {
        refreshPokemonDetails()
    }

    fun refreshPokemonDetails() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null

            runCatching {
                refreshPokemonDetailsUseCase(pokemonId)
            }.onFailure {
                errorMessage.value = "Could not refresh Pokemon details. Showing saved details."
            }

            isLoading.value = false
        }
    }

    fun toggleFavorite() {
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
