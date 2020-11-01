package com.teyyub.listes.network

import com.teyyub.listes.model.Doable

class Book (private val title: String, private val authors: List<String>?, private val description: String? ) {

    fun toDoable(): Doable {

        val details = if (!authors.isNullOrEmpty()) {
            val authorsString = authors.toString().let { it.substring(1, it.length - 1) }
            "$authorsString\n${description ?: ""}"
        } else {
            description ?: ""
        }

        return Doable(Doable.DOABLE_BOOK, title, details, false, "")
    }
}