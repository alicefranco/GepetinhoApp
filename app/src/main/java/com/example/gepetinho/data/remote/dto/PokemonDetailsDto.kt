package com.example.gepetinho.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PokemonDetailsDto(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    @SerializedName("base_experience")
    val baseExperience: Int?,
    val sprites: PokemonSpritesDto,
    val types: List<PokemonTypeSlotDto>
)

data class PokemonSpritesDto(
    @SerializedName("front_default")
    val frontDefault: String?,
    val other: PokemonOtherSpritesDto? = null
)

data class PokemonOtherSpritesDto(
    @SerializedName("official-artwork")
    val officialArtwork: PokemonOfficialArtworkDto? = null
)

data class PokemonOfficialArtworkDto(
    @SerializedName("front_default")
    val frontDefault: String?
)

data class PokemonTypeSlotDto(
    val slot: Int,
    val type: PokemonNamedApiResourceDto
)

data class PokemonNamedApiResourceDto(
    val name: String,
    val url: String
)
