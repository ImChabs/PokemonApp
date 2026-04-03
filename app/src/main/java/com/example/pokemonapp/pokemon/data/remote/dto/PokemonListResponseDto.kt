package com.example.pokemonapp.pokemon.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PokemonListResponseDto(
    val next: String? = null,
    val results: List<PokemonListItemDto> = emptyList()
)

@Serializable
data class PokemonListItemDto(
    val name: String,
    val url: String
)
