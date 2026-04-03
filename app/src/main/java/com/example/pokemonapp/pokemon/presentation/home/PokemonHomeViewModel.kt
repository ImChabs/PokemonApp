package com.example.pokemonapp.pokemon.presentation.home

import androidx.lifecycle.ViewModel
import com.example.pokemonapp.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PokemonHomeViewModel : ViewModel() {
    private val _state = MutableStateFlow(
        PokemonHomeState(
            apiBaseUrl = BuildConfig.POKE_API_BASE_URL,
            timeoutMillis = BuildConfig.NETWORK_TIMEOUT_MILLIS,
            networkingConfigured = true,
            timeoutConfigured = true,
            pokemonModelsReady = true,
            requestStateReady = true
        )
    )
    val state = _state.asStateFlow()
}
