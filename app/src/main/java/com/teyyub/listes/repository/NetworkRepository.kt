package com.teyyub.listes.repository

import com.teyyub.listes.model.Doable
import com.teyyub.listes.network.BookApi
import com.teyyub.listes.network.BookResponse
import com.teyyub.listes.network.MovieApi
import com.teyyub.listes.network.MovieResponse
import io.reactivex.Single
import retrofit2.Response
import java.lang.Exception
import java.lang.IllegalArgumentException

object NetworkRepository {

    private val movieApi = MovieApi.create()
    private val bookApi = BookApi.create()

    fun find(what: String, name: String): Single<NetworkResult> {
        return when (what) {
            Doable.DOABLE_MOVIE -> findMovies(name)
            Doable.DOABLE_BOOK -> findBooks(name)
            else -> throw IllegalArgumentException()
        }
    }

    fun getPopulars(what: String): Single<NetworkResult> {
        return when (what) {
            Doable.DOABLE_MOVIE -> popularMovies()
            Doable.DOABLE_BOOK -> popularBooks()
            else -> throw IllegalArgumentException()
        }
    }

    private fun findMovies(name: String): Single<NetworkResult> =
        movieApi.findMovies(name).map(this::mapMovieResponse)

    private fun popularMovies(): Single<NetworkResult> =
        movieApi.popularMovies().map(this::mapMovieResponse)

    private fun findBooks(name: String): Single<NetworkResult> =
        bookApi.findBooks(name).map(this::mapBookResponse)

    private fun popularBooks(): Single<NetworkResult> =
        bookApi.popularBooks().map(this::mapBookResponse)

    //In these methods we map Response object to Network result
    //If response code was unsuccessful or response body is null we return NetworkResult.Failure
    //else NetworkResult.Success with List of Doables
    private fun mapMovieResponse(response: Response<MovieResponse>): NetworkResult {
        return when (response.code()) {
            in 200..300 -> {
                val body = response.body()
                if (body != null) {
                    NetworkResult.Success(body.results.map { it.toDoable() })
                } else {
                    NetworkResult.Failure(NetworkError.ServerFailure)
                }
            }
            else -> NetworkResult.Failure(NetworkError.ServerFailure)
        }
    }

    private fun mapBookResponse(response: Response<BookResponse>): NetworkResult {
        return when (response.code()) {
            in 200..300 -> {
                val body = response.body()
                if (body != null) {
                    NetworkResult.Success(body.items.map { it.volumeInfo.toDoable() })
                } else {
                    NetworkResult.Failure(NetworkError.ServerFailure)
                }
            }
            else -> NetworkResult.Failure(NetworkError.ServerFailure)
        }
    }
}

//This sealed class used to wrap Network response
sealed class NetworkResult {
    class Success(val doables: List<Doable>) : NetworkResult()
    class Failure(val error: NetworkError) : NetworkResult()
}

//Class for representing Network error
sealed class NetworkError : Exception() {
    object ServerFailure : NetworkError()
}

