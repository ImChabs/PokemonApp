package com.example.pokemonapp.pokemon.presentation.home

sealed interface PokemonHomeAction {
    data class OnSearchQueryChange(val query: String) : PokemonHomeAction

    data object OnSearchSubmit : PokemonHomeAction
}
