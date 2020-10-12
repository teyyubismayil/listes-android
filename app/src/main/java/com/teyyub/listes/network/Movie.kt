package com.teyyub.listes.network

import com.teyyub.listes.model.Thing
import com.teyyub.listes.movie

data class Movie(val title: String, val overview: String?, val release_date: String?){

    fun toThing(): Thing {
        var details: String

        if (!release_date.isNullOrEmpty()) {
            val releaseYear = release_date.substring(0, 4)
            details = "$releaseYear\n$overview"
        } else {
            details = overview ?: ""
        }
        return Thing(movie, title, details, false)
    }
}