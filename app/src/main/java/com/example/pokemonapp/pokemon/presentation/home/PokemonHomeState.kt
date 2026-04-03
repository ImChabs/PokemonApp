package com.example.pokemonapp.pokemon.presentation.home

data class PokemonHomeState(
    val apiBaseUrl: String = "",
    val timeoutMillis: Long = 0L,
    val networkingConfigured: Boolean = false,
    val timeoutConfigured: Boolean = false,
    val pokemonModelsReady: Boolean = false,
    val requestStateReady: Boolean = false
)
