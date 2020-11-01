package com.teyyub.listes

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.teyyub.listes.model.Doable
import com.teyyub.listes.repository.DatabaseRepository

class DoablesListViewModel(
    what: String,
    isDone: Boolean
): ViewModel() {

    private val databaseRepository = DatabaseRepository.get()

    val doablesLiveData: LiveData<List<Doable>> = databaseRepository.getDoables(what, isDone)

    fun didDoable(doable: Doable) {
        doable.isDone = true
        databaseRepository.updateDoable(doable)
    }

    fun deleteDoable(doable: Doable) {
        databaseRepository.deleteDoable(doable)
    }
}