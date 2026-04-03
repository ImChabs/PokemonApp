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
