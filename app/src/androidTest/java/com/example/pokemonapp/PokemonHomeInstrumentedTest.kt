package com.example.pokemonapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PokemonHomeInstrumentedTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun foundationScreen_isDisplayedInMainActivity() {
        composeTestRule.onNodeWithText("Pokemon App Foundation").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ktor client and JSON serialization").assertIsDisplayed()
        composeTestRule.onNodeWithText("Next up: paginated Pokemon list and name search.").assertIsDisplayed()
    }
}
