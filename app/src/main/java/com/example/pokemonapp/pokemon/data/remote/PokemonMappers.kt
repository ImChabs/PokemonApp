package com.example.pokemonapp.pokemon.data.remote

import com.example.pokemonapp.core.data.network.constructRoute
import com.example.pokemonapp.pokemon.data.remote.dto.PokemonLookupResponseDto
import com.example.pokemonapp.pokemon.data.remote.dto.PokemonListItemDto
import com.example.pokemonapp.pokemon.data.remote.dto.PokemonListResponseDto
import com.example.pokemonapp.pokemon.domain.model.PokemonListItem
import com.example.pokemonapp.pokemon.domain.model.PokemonListPage

fun PokemonListResponseDto.toPokemonListPage(): PokemonListPage {
    return PokemonListPage(
        items = results.map(PokemonListItemDto::toPokemonListItem),
        canLoadMore = next != null
    )
}

fun PokemonListItemDto.toPokemonListItem(): PokemonListItem {
    return PokemonListItem(
        name = name,
        detailUrl = url
    )
}

fun PokemonLookupResponseDto.toPokemonListItem(): PokemonListItem {
    return PokemonListItem(
        name = name,
        detailUrl = constructRoute("/pokemon/$id/")
    )
}
