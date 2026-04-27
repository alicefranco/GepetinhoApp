package com.example.gepetinho.data.repository

import com.example.gepetinho.data.extensions.toDomain
import com.example.gepetinho.data.extensions.toEntity
import com.example.gepetinho.data.local.PokemonDao
import com.example.gepetinho.data.remote.PokemonApiService
import com.example.gepetinho.domain.model.Pokemon
import com.example.gepetinho.domain.model.PokemonDetails
import com.example.gepetinho.domain.repository.PokemonRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PokemonRepositoryImpl @Inject constructor(
    private val pokemonApiService: PokemonApiService,
    private val pokemonDao: PokemonDao
) : PokemonRepository {

    override fun observePokemon(): Flow<List<Pokemon>> {
        return pokemonDao.observePokemon().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observePokemonDetails(pokemonId: Int): Flow<PokemonDetails?> {
        return pokemonDao.observePokemonDetails(pokemonId).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun refreshPokemon(limit: Int, offset: Int) {
        val favoriteIds = pokemonDao.getFavoritePokemonIds().toSet()
        val response = pokemonApiService.getPokemonList(limit = limit, offset = offset)
        val entities = response.results.map { dto ->
            dto.toEntity(isFavorite = dto.url.extractPokemonId() in favoriteIds)
        }

        pokemonDao.upsertPokemon(entities)
    }

    override suspend fun refreshPokemonDetails(pokemonId: Int) {
        val pokemonName = pokemonDao.getPokemonById(pokemonId)?.name ?: pokemonId.toString()
        val isFavorite = pokemonDao.getPokemonById(pokemonId)?.isFavorite
            ?: pokemonDao.getPokemonDetailsById(pokemonId)?.isFavorite
            ?: false
        val details = pokemonApiService.getPokemonDetails(pokemonName = pokemonName)

        pokemonDao.upsertPokemonDetails(details.toEntity(isFavorite = isFavorite))
    }

    override suspend fun toggleFavorite(pokemonId: Int) {
        pokemonDao.toggleFavorite(pokemonId)
    }

    private fun String.extractPokemonId(): Int {
        return trimEnd('/')
            .substringAfterLast('/')
            .toInt()
    }
}
