package com.teyyub.listes.network

import com.teyyub.listes.book
import com.teyyub.listes.model.Thing

class Book (private val title: String, private val authors: List<String>?, private val description: String? ) {

    fun toThing(): Thing {

        var details: String

        if (!authors.isNullOrEmpty()) {
            val authorsString = authors.toString().let { it.substring(1, it.length - 1) }
            details = "$authorsString\n${description ?: ""}"
        } else {
            details = description ?: ""
        }

        return Thing(book, title, details, false)
    }
}