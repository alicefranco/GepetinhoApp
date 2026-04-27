package com.example.gepetinho.domain.usecase

import com.example.gepetinho.domain.repository.PokemonRepository
import javax.inject.Inject

class GetPokemonUseCase @Inject constructor(
    private val pokemonRepository: PokemonRepository
) {
    operator fun invoke() = pokemonRepository.observePokemon()
}
