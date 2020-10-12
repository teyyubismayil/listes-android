package com.teyyub.listes.network

import io.reactivex.Single
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface BookApi {

    @GET("volumes")
    fun findBooks(
        @Query("q") name: String,
        @Query("key") key: String = APIKEY,
        @Query("maxResults") count: String = "24"
    ): Single<Response<BookResponse>>

    @GET("volumes")
    fun popularBooks(
        @Query("q") name: String = "a",
        @Query("key") key: String = APIKEY,
        @Query("maxResults") count: String = "24"
    ): Single<Response<BookResponse>>

    companion object {
        private const val APIKEY = "AIzaSyBUfTS2emrB2kleAI88be76hTtEHRBw9sc"

        fun create(): BookApi {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/books/v1/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()

            return retrofit.create(BookApi::class.java)
        }
    }
}