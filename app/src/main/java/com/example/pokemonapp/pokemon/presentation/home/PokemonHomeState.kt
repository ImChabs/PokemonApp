package com.example.pokemonapp.pokemon.presentation.home

import androidx.annotation.StringRes
import com.example.pokemonapp.pokemon.domain.model.PokemonListItem

data class PokemonHomeState(
    val contentState: PokemonHomeContentState = PokemonHomeContentState.Loading,
    val searchQuery: String = "",
    val searchState: PokemonSearchState = PokemonSearchState.Idle
)

sealed interface PokemonHomeContentState {
    data object Loading : PokemonHomeContentState

    data class Success(
        val items: List<PokemonListItem>
    ) : PokemonHomeContentState

    data class Error(
        @param:StringRes val messageRes: Int
    ) : PokemonHomeContentState
}

sealed interface PokemonSearchState {
    data object Idle : PokemonSearchState

    data object Loading : PokemonSearchState

    data class Success(
        val item: PokemonListItem
    ) : PokemonSearchState

    data class Empty(
        val query: String
    ) : PokemonSearchState

    data class Error(
        @param:StringRes val messageRes: Int
    ) : PokemonSearchState
}
