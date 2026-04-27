package com.example.gepetinho.data.remote

import com.example.gepetinho.data.remote.dto.PokemonDetailsDto
import com.example.gepetinho.data.remote.dto.PokemonListResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonApiService {

    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): PokemonListResponseDto

    @GET("pokemon/{pokemonName}")
    suspend fun getPokemonDetails(
        @Path("pokemonName") pokemonName: String
    ): PokemonDetailsDto
}
