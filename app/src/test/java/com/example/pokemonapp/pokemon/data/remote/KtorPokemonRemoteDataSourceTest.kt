package com.example.pokemonapp.pokemon.data.remote

import com.example.pokemonapp.core.domain.DataError
import com.example.pokemonapp.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class KtorPokemonRemoteDataSourceTest {

    @Test
    fun getPokemonPage_returnsMappedDomainModels() = runBlocking {
        val httpClient = createHttpClient(
            status = HttpStatusCode.OK,
            body = """
                {
                  "next": "https://pokeapi.co/api/v2/pokemon?offset=20&limit=20",
                  "results": [
                    {
                      "name": "bulbasaur",
                      "url": "https://pokeapi.co/api/v2/pokemon/1/"
                    }
                  ]
                }
            """.trimIndent()
        ) { requestedUrl ->
            assertEquals(
                "https://pokeapi.co/api/v2/pokemon?limit=20&offset=0",
                requestedUrl
            )
        }

        val dataSource = KtorPokemonRemoteDataSource(httpClient)

        val result = dataSource.getPokemonPage(limit = 20, offset = 0)

        assertTrue(result is Result.Success)
        val page = (result as Result.Success).data
        assertEquals(1, page.items.size)
        assertEquals("bulbasaur", page.items.first().name)
        assertTrue(page.canLoadMore)
    }

    @Test
    fun getPokemonPage_mapsServerErrors() = runBlocking {
        val httpClient = createHttpClient(
            status = HttpStatusCode.InternalServerError,
            body = """{"detail":"server error"}"""
        )
        val dataSource = KtorPokemonRemoteDataSource(httpClient)

        val result = dataSource.getPokemonPage(limit = 20, offset = 0)

        assertTrue(result is Result.Error)
        assertEquals(DataError.Network.SERVER_ERROR, (result as Result.Error).error)
    }

    @Test
    fun getPokemonByName_returnsMappedPokemonItem() = runBlocking {
        val httpClient = createHttpClient(
            status = HttpStatusCode.OK,
            body = """
                {
                  "id": 25,
                  "name": "pikachu"
                }
            """.trimIndent()
        ) { requestedUrl ->
            assertEquals(
                "https://pokeapi.co/api/v2/pokemon/pikachu",
                requestedUrl
            )
        }
        val dataSource = KtorPokemonRemoteDataSource(httpClient)

        val result = dataSource.getPokemonByName("pikachu")

        assertTrue(result is Result.Success)
        val item = (result as Result.Success).data
        assertEquals("pikachu", item.name)
        assertEquals("https://pokeapi.co/api/v2/pokemon/25/", item.detailUrl)
    }

    @Test
    fun getPokemonByName_mapsNotFoundErrors() = runBlocking {
        val httpClient = createHttpClient(
            status = HttpStatusCode.NotFound,
            body = """{"detail":"not found"}"""
        )
        val dataSource = KtorPokemonRemoteDataSource(httpClient)

        val result = dataSource.getPokemonByName("missingno")

        assertTrue(result is Result.Error)
        assertEquals(DataError.Network.NOT_FOUND, (result as Result.Error).error)
    }

    private fun createHttpClient(
        status: HttpStatusCode,
        body: String,
        assertRequest: (String) -> Unit = {}
    ): HttpClient {
        val engine = MockEngine { request ->
            assertRequest(request.url.toString())
            respond(
                content = body,
                status = status,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        return HttpClient(engine) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
        }
    }
}
