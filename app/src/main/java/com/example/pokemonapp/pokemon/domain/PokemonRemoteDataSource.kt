package com.example.pokemonapp.pokemon.domain

import com.example.pokemonapp.core.domain.DataError
import com.example.pokemonapp.core.domain.Result
import com.example.pokemonapp.pokemon.domain.model.PokemonListPage

interface PokemonRemoteDataSource {
    suspend fun getPokemonPage(
        limit: Int,
        offset: Int
    ): Result<PokemonListPage, DataError.Network>
}
