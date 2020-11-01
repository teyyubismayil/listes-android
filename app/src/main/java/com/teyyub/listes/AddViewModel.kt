package com.teyyub.listes

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teyyub.listes.model.Doable
import com.teyyub.listes.repository.DatabaseRepository
import com.teyyub.listes.repository.NetworkRepository
import com.teyyub.listes.repository.NetworkResult
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

//This ViewModel will be shared between AddActivity, AddManualFragment and AddSearchFragment
class AddViewModel(val what: String): ViewModel() {

    private val disposables = CompositeDisposable()

    //LiveData to which we post populars result
    private val _popularsLiveData: MutableLiveData<List<Doable>> = MutableLiveData()

    //LiveData to which we post search result
    private val _searchedLiveData: MutableLiveData<List<Doable>> = MutableLiveData()

    //LiveData to which we post value if something went wrong
    private val _errorLiveData: MutableLiveData<Unit> = MutableLiveData()

    //LiveData to which we will post true when we need to show loading indicator
    //and false if we need to hide it
    private val _loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()

    //We expose to fragment LiveData, not MutableLiveData
    val popularsLiveData: LiveData<List<Doable>>
        get() = _popularsLiveData
    val searchedLiveData: LiveData<List<Doable>>
        get() = _searchedLiveData
    val errorLiveData: LiveData<Unit>
        get() = _errorLiveData
    val loadingLiveData: LiveData<Boolean>
        get() = _loadingLiveData

    fun showPopulars() {
        //We check does we have cached populars result
        //else we fetch from network and cache it
        //We use _loadingLiveData to show and hide loading indicator
        if (popularsCache[what].isNullOrEmpty()) {
            NetworkRepository.getPopulars(what)
                .doOnSubscribe { _loadingLiveData.postValue(true) }
                .doOnEvent { _, _ -> _loadingLiveData.postValue(false) }
                .doOnSuccess {
                    //Saving in cache
                    if (it is NetworkResult.Success) {
                        popularsCache[what] = it.doables
                    } else {
                        _errorLiveData.postValue(Unit)
                    }
                }
                .filter {
                    it is NetworkResult.Success
                }
                .map {
                    (it as NetworkResult.Success).doables
                }
                .subscribeOn(Schedulers.io())
                .subscribeBy(
                    onSuccess = {
                        _popularsLiveData.postValue(it)
                    },
                    onError = {
                        _errorLiveData.postValue(Unit)
                    }
                )
                .addTo(disposables)
        } else {
            _popularsLiveData.postValue(popularsCache[what])
        }
    }

    fun searchFor(searchText: String) {
        NetworkRepository.find(what, searchText)
            .doOnSubscribe { _loadingLiveData.postValue(true) }
            .doOnEvent { _, _ -> _loadingLiveData.postValue(false) }
            .doOnSuccess {
                if (it is NetworkResult.Failure) {
                    _errorLiveData.postValue(Unit)
                }
            }
            .filter {
                it is NetworkResult.Success
            }
            .map {
                (it as NetworkResult.Success).doables
            }
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onSuccess = {
                    _searchedLiveData.postValue(it)
                },
                onError = {
                    _errorLiveData.postValue(Unit)
                }
            )
            .addTo(disposables)
    }

    //Checking if name written by user is valid
    fun isNameValid(text: Editable?): Boolean {
        return text != null && text.isNotEmpty()
    }

    //Adding new Doaable object to database
    fun addDoable(name: String, details: String) {
        val newDoable = Doable(what, name, details, false, "")
        DatabaseRepository.get().addDoable(newDoable)
    }

    fun addDoable(doable: Doable) {
        DatabaseRepository.get().addDoable(doable)
    }

    override fun onCleared() {
        super.onCleared()
        //Disposing subscriptions
        disposables.dispose()
    }

    companion object {
        //Here we will cache populars result to use during app lifecycle
        //So we will fetch popular books and popular movies once during app lifecycle
        private val popularsCache = mutableMapOf<String, List<Doable>>(
            Pair(Doable.DOABLE_MOVIE, emptyList()),
            Pair(Doable.DOABLE_BOOK, emptyList())
        )
    }
}