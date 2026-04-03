package com.example.pokemonapp.pokemon.presentation.home

import com.example.pokemonapp.R
import com.example.pokemonapp.core.domain.DataError
import com.example.pokemonapp.core.domain.Result
import com.example.pokemonapp.pokemon.domain.PokemonRemoteDataSource
import com.example.pokemonapp.pokemon.domain.model.PokemonListItem
import com.example.pokemonapp.pokemon.domain.model.PokemonListPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonHomeViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_loadsTheFirstPokemonPage() = runTest {
        val fakeDataSource = FakePokemonRemoteDataSource(
            result = Result.Success(
                PokemonListPage(
                    items = listOf(
                        PokemonListItem(
                            name = "bulbasaur",
                            detailUrl = "https://pokeapi.co/api/v2/pokemon/1/"
                        ),
                        PokemonListItem(
                            name = "ivysaur",
                            detailUrl = "https://pokeapi.co/api/v2/pokemon/2/"
                        )
                    ),
                    canLoadMore = true
                )
            )
        )

        val viewModel = PokemonHomeViewModel(fakeDataSource)

        val contentState = viewModel.state.value.contentState
        assertTrue(contentState is PokemonHomeContentState.Success)
        contentState as PokemonHomeContentState.Success
        assertEquals(listOf("bulbasaur", "ivysaur"), contentState.items.map { it.name })
        assertEquals(20, fakeDataSource.lastRequestedLimit)
        assertEquals(0, fakeDataSource.lastRequestedOffset)
    }

    @Test
    fun init_mapsTimeoutErrorsIntoErrorState() = runTest {
        val fakeDataSource = FakePokemonRemoteDataSource(
            result = Result.Error(DataError.Network.REQUEST_TIMEOUT)
        )

        val viewModel = PokemonHomeViewModel(fakeDataSource)

        val contentState = viewModel.state.value.contentState
        assertTrue(contentState is PokemonHomeContentState.Error)
        contentState as PokemonHomeContentState.Error
        assertEquals(R.string.pokemon_home_error_timeout, contentState.messageRes)
    }
}

private class FakePokemonRemoteDataSource(
    private val result: Result<PokemonListPage, DataError.Network>
) : PokemonRemoteDataSource {

    var lastRequestedLimit: Int? = null
    var lastRequestedOffset: Int? = null

    override suspend fun getPokemonPage(
        limit: Int,
        offset: Int
    ): Result<PokemonListPage, DataError.Network> {
        lastRequestedLimit = limit
        lastRequestedOffset = offset
        return result
    }
}
