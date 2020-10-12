package com.teyyub.listes.network

import io.reactivex.Single
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi {

    @GET("search/movie")
    fun findMovies(@Query("query") name: String, @Query("api_key") key: String = APIKEY): Single<Response<MovieResponse>>

    @GET("movie/popular")
    fun popularMovies(@Query("api_key") key: String = APIKEY): Single<Response<MovieResponse>>

    companion object {
        private const val APIKEY = "f6473e4d474a28fd8ca9acd9b4409211"

        fun create(): MovieApi {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()

            return retrofit.create(MovieApi::class.java)
        }
    }
}