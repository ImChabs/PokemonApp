package com.example.pokemonapp.pokemon.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PokemonLookupResponseDto(
    val id: Int,
    val name: String
)
