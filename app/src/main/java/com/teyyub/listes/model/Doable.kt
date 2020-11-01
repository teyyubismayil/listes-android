package com.teyyub.listes.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

//Model for goal, book, or movie
@Entity
data class Doable (
    var what: String, //is book, movie or goal
    var name: String,
    var details: String, //additional information
    var isDone: Boolean,
    var imageUrl: String
) {
    @PrimaryKey(autoGenerate = true)
    var doableId: Int = 0 //id of doable

    companion object {
        //Possible 'what' property values of Doable object
        const val DOABLE_GOAL = "Goal"
        const val DOABLE_BOOK = "Book"
        const val DOABLE_MOVIE = "Movie"
    }
}