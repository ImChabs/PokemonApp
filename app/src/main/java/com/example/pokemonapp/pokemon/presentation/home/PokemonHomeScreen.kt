package com.example.pokemonapp.pokemon.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pokemonapp.R
import com.example.pokemonapp.ui.theme.PokemonAppTheme

@Composable
fun PokemonHomeRoot(
    viewModel: PokemonHomeViewModel = viewModel()
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
            .verticalScroll(rememberScrollState())
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
        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.pokemon_home_status_title),
                    style = MaterialTheme.typography.titleMedium
                )
                FoundationStatusRow(
                    label = stringResource(R.string.foundation_networking_label),
                    isReady = state.networkingConfigured
                )
                FoundationStatusRow(
                    label = stringResource(R.string.foundation_timeout_label),
                    isReady = state.timeoutConfigured
                )
                FoundationStatusRow(
                    label = stringResource(R.string.foundation_models_label),
                    isReady = state.pokemonModelsReady
                )
                FoundationStatusRow(
                    label = stringResource(R.string.foundation_state_label),
                    isReady = state.requestStateReady
                )
            }
        }
        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.pokemon_home_config_title),
                    style = MaterialTheme.typography.titleMedium
                )
                FoundationValueRow(
                    label = stringResource(R.string.foundation_base_url_label),
                    value = state.apiBaseUrl
                )
                FoundationValueRow(
                    label = stringResource(R.string.foundation_timeout_label),
                    value = stringResource(
                        R.string.foundation_timeout_value,
                        state.timeoutMillis.toString()
                    )
                )
            }
        }
        Text(
            text = stringResource(R.string.pokemon_home_next_block),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun FoundationStatusRow(
    label: String,
    isReady: Boolean
) {
    FoundationValueRow(
        label = label,
        value = stringResource(
            if (isReady) {
                R.string.foundation_status_ready
            } else {
                R.string.foundation_status_pending
            }
        )
    )
}

@Composable
private fun FoundationValueRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PokemonHomeScreenPreview() {
    PokemonAppTheme {
        PokemonHomeScreen(
            state = PokemonHomeState(
                apiBaseUrl = "https://pokeapi.co/api/v2",
                timeoutMillis = 15000L,
                networkingConfigured = true,
                timeoutConfigured = true,
                pokemonModelsReady = true,
                requestStateReady = true
            )
        )
    }
}
