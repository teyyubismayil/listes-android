package com.teyyub.listes.network

import com.teyyub.listes.model.Thing

class Book (private val title: String, private val authors: List<String>?, private val description: String? ) {

    fun toThing(): Thing {

        val details = if (!authors.isNullOrEmpty()) {
            val authorsString = authors.toString().let { it.substring(1, it.length - 1) }
            "$authorsString\n${description ?: ""}"
        } else {
            description ?: ""
        }

        return Thing(Thing.THING_BOOK, title, details, false)
    }
}