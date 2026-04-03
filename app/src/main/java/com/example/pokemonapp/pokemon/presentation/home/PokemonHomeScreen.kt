package com.example.pokemonapp.pokemon.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pokemonapp.pokemon.domain.model.PokemonListItem
import com.example.pokemonapp.R
import com.example.pokemonapp.ui.theme.PokemonAppTheme

@Composable
fun PokemonHomeRoot(
    viewModel: PokemonHomeViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        PokemonHomeScreen(
            state = state,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun PokemonHomeScreen(
    state: PokemonHomeState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.pokemon_home_title),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = stringResource(R.string.pokemon_home_subtitle),
            style = MaterialTheme.typography.bodyLarge
        )

        when (val contentState = state.contentState) {
            is PokemonHomeContentState.Error -> {
                StateMessage(
                    message = stringResource(contentState.messageRes),
                    modifier = Modifier.weight(1f)
                )
            }

            PokemonHomeContentState.Loading -> {
                LoadingState(modifier = Modifier.weight(1f))
            }

            is PokemonHomeContentState.Success -> {
                PokemonListState(
                    items = contentState.items,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun LoadingState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = stringResource(R.string.pokemon_home_loading),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun StateMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun PokemonListState(
    items: List<PokemonListItem>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.pokemon_home_list_title),
            style = MaterialTheme.typography.titleMedium
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = items,
                key = { item -> item.detailUrl }
            ) { item ->
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = item.name,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PokemonHomeScreenPreview() {
    PokemonAppTheme {
        PokemonHomeScreen(
            state = PokemonHomeState(
                contentState = PokemonHomeContentState.Success(
                    items = listOf(
                        PokemonListItem(
                            name = "bulbasaur",
                            detailUrl = "https://pokeapi.co/api/v2/pokemon/1/"
                        ),
                        PokemonListItem(
                            name = "ivysaur",
                            detailUrl = "https://pokeapi.co/api/v2/pokemon/2/"
                        ),
                        PokemonListItem(
                            name = "venusaur",
                            detailUrl = "https://pokeapi.co/api/v2/pokemon/3/"
                        )
                    )
                )
            )
        )
    }
}
