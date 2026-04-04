package com.example.pokemonapp.pokemon.data.remote

import com.example.pokemonapp.core.data.network.safeGet
import com.example.pokemonapp.core.domain.DataError
import com.example.pokemonapp.core.domain.Result
import com.example.pokemonapp.core.domain.map
import com.example.pokemonapp.pokemon.data.remote.dto.PokemonLookupResponseDto
import com.example.pokemonapp.pokemon.data.remote.dto.PokemonListResponseDto
import com.example.pokemonapp.pokemon.domain.PokemonRemoteDataSource
import com.example.pokemonapp.pokemon.domain.model.PokemonListItem
import com.example.pokemonapp.pokemon.domain.model.PokemonListPage
import io.ktor.client.HttpClient

class KtorPokemonRemoteDataSource(
    private val httpClient: HttpClient
) : PokemonRemoteDataSource {

    override suspend fun getPokemonPage(
        limit: Int,
        offset: Int
    ): Result<PokemonListPage, DataError.Network> {
        return httpClient.safeGet<PokemonListResponseDto>(
            route = "pokemon",
            queryParameters = mapOf(
                "limit" to limit,
                "offset" to offset
            )
        ).map(PokemonListResponseDto::toPokemonListPage)
    }

    override suspend fun getPokemonByName(
        name: String
    ): Result<PokemonListItem, DataError.Network> {
        return httpClient.safeGet<PokemonLookupResponseDto>(
            route = "pokemon/$name"
        ).map(PokemonLookupResponseDto::toPokemonListItem)
    }
}
