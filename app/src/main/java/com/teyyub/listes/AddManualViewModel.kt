package com.teyyub.listes

import android.text.Editable
import androidx.lifecycle.ViewModel
import com.teyyub.listes.model.Thing
import com.teyyub.listes.repository.DatabaseRepository

class AddManualViewModel(
    private val what: String
): ViewModel() {

    //Checking if name written by user is valid
    fun isNameValid(text: Editable?): Boolean {
        return text != null && text.isNotEmpty()
    }

    //Adding new Thing object to database
    fun addThing(name: String, details: String) {
        val newThing = Thing(what, name, details, false)
        DatabaseRepository.get().addThing(newThing)
    }
}