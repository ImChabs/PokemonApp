package com.example.pokemonapp.pokemon.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.pokemonapp.R
import com.example.pokemonapp.core.domain.DataError
import com.example.pokemonapp.core.domain.Result
import com.example.pokemonapp.pokemon.domain.PokemonRemoteDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PokemonHomeViewModel(
    private val pokemonRemoteDataSource: PokemonRemoteDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(PokemonHomeState())
    val state = _state.asStateFlow()

    init {
        loadFirstPage()
    }

    fun onAction(action: PokemonHomeAction) {
        when (action) {
            is PokemonHomeAction.OnSearchQueryChange -> {
                _state.update {
                    it.copy(
                        searchQuery = action.query,
                        searchState = PokemonSearchState.Idle
                    )
                }
            }

            PokemonHomeAction.OnSearchSubmit -> searchPokemonByName()
        }
    }

    private fun loadFirstPage() {
        viewModelScope.launch {
            _state.update {
                it.copy(contentState = PokemonHomeContentState.Loading)
            }

            when (
                val result = pokemonRemoteDataSource.getPokemonPage(
                    limit = PAGE_SIZE,
                    offset = INITIAL_OFFSET
                )
            ) {
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            contentState = PokemonHomeContentState.Error(
                                messageRes = result.error.toMessageRes()
                            )
                        )
                    }
                }

                is Result.Success -> {
                    _state.update {
                        it.copy(
                            contentState = PokemonHomeContentState.Success(
                                items = result.data.items
                            )
                        )
                    }
                }
            }
        }
    }

    private fun searchPokemonByName() {
        val submittedQuery = state.value.searchQuery.trim()
        if (submittedQuery.isBlank()) {
            _state.update {
                it.copy(searchState = PokemonSearchState.Idle)
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(searchState = PokemonSearchState.Loading)
            }

            when (val result = pokemonRemoteDataSource.getPokemonByName(submittedQuery.lowercase())) {
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            searchState = result.error.toSearchState(
                                submittedQuery = submittedQuery
                            )
                        )
                    }
                }

                is Result.Success -> {
                    _state.update {
                        it.copy(
                            searchState = PokemonSearchState.Success(
                                item = result.data
                            )
                        )
                    }
                }
            }
        }
    }

    companion object {
        private const val PAGE_SIZE = 20
        private const val INITIAL_OFFSET = 0

        fun factory(
            pokemonRemoteDataSource: PokemonRemoteDataSource
        ): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    PokemonHomeViewModel(pokemonRemoteDataSource)
                }
            }
        }
    }
}

private fun DataError.Network.toMessageRes(): Int {
    return when (this) {
        DataError.Network.NO_INTERNET -> R.string.pokemon_home_error_no_internet
        DataError.Network.REQUEST_TIMEOUT -> R.string.pokemon_home_error_timeout
        else -> R.string.pokemon_home_error_generic
    }
}

private fun DataError.Network.toSearchState(
    submittedQuery: String
): PokemonSearchState {
    return when (this) {
        DataError.Network.NOT_FOUND -> PokemonSearchState.Empty(query = submittedQuery)
        else -> PokemonSearchState.Error(messageRes = toMessageRes())
    }
}
