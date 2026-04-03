package com.example.pokemonapp.core.data.network

import com.example.pokemonapp.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object HttpClientFactory {
    fun create(
        engineFactory: HttpClientEngineFactory<*> = Android
    ): HttpClient {
        return HttpClient(engineFactory) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        explicitNulls = false
                    }
                )
            }
            install(HttpTimeout) {
                requestTimeoutMillis = BuildConfig.NETWORK_TIMEOUT_MILLIS
                connectTimeoutMillis = BuildConfig.NETWORK_TIMEOUT_MILLIS
                socketTimeoutMillis = BuildConfig.NETWORK_TIMEOUT_MILLIS
            }
            defaultRequest {
                accept(ContentType.Application.Json)
            }
        }
    }
}
