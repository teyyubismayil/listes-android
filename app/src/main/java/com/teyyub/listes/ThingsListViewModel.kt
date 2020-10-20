package com.teyyub.listes

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.teyyub.listes.model.Thing
import com.teyyub.listes.repository.DatabaseRepository

class ThingsListViewModel(
    what: String,
    isDone: Boolean
): ViewModel() {

    private val databaseRepository = DatabaseRepository.get()

    val thingsLiveData: LiveData<List<Thing>> = databaseRepository.getThings(what, isDone)

    fun didThing(thing: Thing) {
        thing.isDone = true
        databaseRepository.updateThing(thing)
    }

    fun deleteThing(thing: Thing) {
        databaseRepository.deleteThing(thing)
    }
}