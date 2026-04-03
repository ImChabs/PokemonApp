package com.example.pokemonapp.core.domain

sealed interface DataError : AppError {
    enum class Network : DataError {
        BAD_REQUEST,
        REQUEST_TIMEOUT,
        UNAUTHORIZED,
        FORBIDDEN,
        NOT_FOUND,
        TOO_MANY_REQUESTS,
        SERVER_ERROR,
        SERIALIZATION,
        NO_INTERNET,
        UNKNOWN
    }
}
