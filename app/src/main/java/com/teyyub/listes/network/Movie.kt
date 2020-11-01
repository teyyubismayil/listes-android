package com.teyyub.listes.network

import com.teyyub.listes.model.Doable

data class Movie(val title: String, val overview: String?, val release_date: String?, val poster_path: String?){

    fun toDoable(): Doable {

        val details = if (!release_date.isNullOrEmpty()) {
            val releaseYear = release_date.substring(0, 4)
            "$releaseYear\n${overview ?: ""}"
        } else {
            overview ?: ""
        }

        val imageUrl = "https://image.tmdb.org/t/p/w185/$poster_path"

        return Doable(Doable.DOABLE_MOVIE, title, details, false, imageUrl)
    }
}
