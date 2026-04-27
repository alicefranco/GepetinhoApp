package com.example.gepetinho.domain.usecase

import com.example.gepetinho.domain.repository.PokemonRepository
import javax.inject.Inject

class TogglePokemonFavoriteUseCase @Inject constructor(
    private val pokemonRepository: PokemonRepository
) {
    suspend operator fun invoke(pokemonId: Int) {
        pokemonRepository.toggleFavorite(pokemonId)
    }
}
