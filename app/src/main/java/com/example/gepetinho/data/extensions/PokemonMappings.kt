package com.example.gepetinho.data.extensions

import com.example.gepetinho.data.local.entity.PokemonDetailsEntity
import com.example.gepetinho.data.local.entity.PokemonEntity
import com.example.gepetinho.data.remote.dto.PokemonDetailsDto
import com.example.gepetinho.data.remote.dto.PokemonListItemDto

fun PokemonListItemDto.toEntity(
    isFavorite: Boolean = false
): PokemonEntity {
    val pokemonId = url.extractPokemonId()

    return PokemonEntity(
        pokemonId = pokemonId,
        name = name,
        imageUrl = pokemonId.toArtworkUrl(),
        isFavorite = isFavorite
    )
}

fun PokemonDetailsDto.toEntity(
    isFavorite: Boolean = false
): PokemonDetailsEntity {
    return PokemonDetailsEntity(
        pokemonId = id,
        name = name,
        imageUrl = officialArtworkUrl(),
        height = height,
        weight = weight,
        baseExperience = baseExperience,
        types = types
            .sortedBy { it.slot }
            .joinToString(separator = ",") { it.type.name },
        isFavorite = isFavorite
    )
}

fun PokemonDetailsDto.officialArtworkUrl(): String? {
    return sprites.other?.officialArtwork?.frontDefault ?: sprites.frontDefault
}

fun Int.toArtworkUrl(): String {
    return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$this.png"
}

private fun String.extractPokemonId(): Int {
    return trimEnd('/')
        .substringAfterLast('/')
        .toInt()
}
