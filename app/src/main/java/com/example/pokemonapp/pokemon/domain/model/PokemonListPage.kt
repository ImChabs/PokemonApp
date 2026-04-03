package com.example.pokemonapp.pokemon.domain.model

data class PokemonListPage(
    val items: List<PokemonListItem>,
    val canLoadMore: Boolean
)
