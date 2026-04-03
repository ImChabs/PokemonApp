package com.example.pokemonapp.pokemon.presentation.home

import androidx.annotation.StringRes
import com.example.pokemonapp.pokemon.domain.model.PokemonListItem

data class PokemonHomeState(
    val contentState: PokemonHomeContentState = PokemonHomeContentState.Loading
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
