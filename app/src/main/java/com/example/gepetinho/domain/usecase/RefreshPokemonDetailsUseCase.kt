package com.example.gepetinho.domain.usecase

import com.example.gepetinho.domain.repository.PokemonRepository
import javax.inject.Inject

class RefreshPokemonDetailsUseCase @Inject constructor(
    private val pokemonRepository: PokemonRepository
) {
    suspend operator fun invoke(pokemonId: Int) {
        pokemonRepository.refreshPokemonDetails(pokemonId)
    }
}
