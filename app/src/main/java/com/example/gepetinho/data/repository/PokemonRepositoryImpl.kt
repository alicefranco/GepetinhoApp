package com.example.gepetinho.data.repository

import com.example.gepetinho.data.extensions.toDomain
import com.example.gepetinho.data.extensions.toEntity
import com.example.gepetinho.data.local.PokemonDao
import com.example.gepetinho.data.remote.PokemonApiService
import com.example.gepetinho.domain.model.Pokemon
import com.example.gepetinho.domain.model.PokemonDetails
import com.example.gepetinho.domain.repository.AuthRepository
import com.example.gepetinho.domain.repository.PokemonRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PokemonRepositoryImpl @Inject constructor(
    private val pokemonApiService: PokemonApiService,
    private val pokemonDao: PokemonDao,
    private val authRepository: AuthRepository
) : PokemonRepository {

    override fun observePokemon(): Flow<List<Pokemon>> {
        return pokemonDao.observePokemon(currentUserId()).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observePokemonDetails(pokemonId: Int): Flow<PokemonDetails?> {
        return pokemonDao.observePokemonDetails(currentUserId(), pokemonId).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun refreshPokemon(limit: Int, offset: Int): Boolean {
        val response = pokemonApiService.getPokemonList(limit = limit, offset = offset)
        val entities = response.results.map { dto ->
            dto.toEntity()
        }

        pokemonDao.upsertPokemon(entities)
        return response.next != null
    }

    override suspend fun refreshPokemonDetails(pokemonId: Int) {
        val pokemonName = pokemonDao.getPokemonById(pokemonId)?.name ?: pokemonId.toString()
        val details = pokemonApiService.getPokemonDetails(pokemonName = pokemonName)

        pokemonDao.upsertPokemonDetails(details.toEntity())
    }

    override suspend fun toggleFavorite(pokemonId: Int) {
        pokemonDao.toggleFavorite(currentUserId(), pokemonId)
    }

    private fun currentUserId(): String {
        return authRepository.currentUser()?.id
            ?: error("Pokemon favorites require an authenticated user.")
    }
}
