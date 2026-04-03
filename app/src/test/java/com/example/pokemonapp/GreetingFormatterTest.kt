package com.example.pokemonapp

import org.junit.Assert.assertEquals
import org.junit.Test

class GreetingFormatterTest {
    @Test
    fun formatGreeting_returnsNeutralGreeting() {
        assertEquals("Hello Android!", formatGreeting("Android"))
    }
}
