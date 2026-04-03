package com.example.pokemonapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.pokemonapp.pokemon.domain.model.PokemonListItem
import com.example.pokemonapp.pokemon.presentation.home.PokemonHomeContentState
import com.example.pokemonapp.pokemon.presentation.home.PokemonHomeScreen
import com.example.pokemonapp.pokemon.presentation.home.PokemonHomeState
import com.example.pokemonapp.ui.theme.PokemonAppTheme
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PokemonHomeInstrumentedTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loadingState_isDisplayed() {
        composeTestRule.setContent {
            PokemonAppTheme {
                PokemonHomeScreen(
                    state = PokemonHomeState(
                        contentState = PokemonHomeContentState.Loading
                    )
                )
            }
        }

        composeTestRule.onNodeWithText("Pokemon").assertIsDisplayed()
        composeTestRule.onNodeWithText("Loading the first page of Pokemon...").assertIsDisplayed()
    }

    @Test
    fun successState_showsPokemonNames() {
        composeTestRule.setContent {
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
                                    name = "charmander",
                                    detailUrl = "https://pokeapi.co/api/v2/pokemon/4/"
                                )
                            )
                        )
                    )
                )
            }
        }

        composeTestRule.onNodeWithText("First page").assertIsDisplayed()
        composeTestRule.onNodeWithText("bulbasaur").assertIsDisplayed()
        composeTestRule.onNodeWithText("charmander").assertIsDisplayed()
    }
}
