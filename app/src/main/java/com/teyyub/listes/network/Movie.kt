package com.teyyub.listes.network

import com.teyyub.listes.model.Thing

data class Movie(val title: String, val overview: String?, val release_date: String?){

    fun toThing(): Thing {

        val details = if (!release_date.isNullOrEmpty()) {
            val releaseYear = release_date.substring(0, 4)
            "$releaseYear\n${overview ?: ""}"
        } else {
            overview ?: ""
        }
        return Thing(Thing.THING_MOVIE, title, details, false)
    }
}