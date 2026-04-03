package com.example.pokemonapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.pokemonapp.core.data.network.HttpClientFactory
import com.example.pokemonapp.pokemon.data.remote.KtorPokemonRemoteDataSource
import com.example.pokemonapp.pokemon.presentation.home.PokemonHomeRoot
import com.example.pokemonapp.pokemon.presentation.home.PokemonHomeViewModel
import com.example.pokemonapp.ui.theme.PokemonAppTheme

class MainActivity : ComponentActivity() {
    private val pokemonRemoteDataSource by lazy {
        KtorPokemonRemoteDataSource(
            httpClient = HttpClientFactory.create()
        )
    }

    private val pokemonHomeViewModel by viewModels<PokemonHomeViewModel> {
        PokemonHomeViewModel.factory(
            pokemonRemoteDataSource = pokemonRemoteDataSource
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokemonAppTheme {
                PokemonHomeRoot(viewModel = pokemonHomeViewModel)
            }
        }
    }
}
