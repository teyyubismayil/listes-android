package com.teyyub.listes

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teyyub.listes.model.Thing
import com.teyyub.listes.repository.NetworkRepository
import com.teyyub.listes.repository.NetworkResult
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class AddSearchViewModel(
    what: String
) : ViewModel() {

    private val searchStream = AddFragment.queryStream
    private val showPopularsStream = AddFragment.showPopularsStream

    private val disposables = CompositeDisposable()

    //LiveData to which we post populars result
    private val _popularsLiveData: MutableLiveData<List<Thing>> = MutableLiveData()

    //LiveData to which we post search result
    private val _searchedLiveData: MutableLiveData<List<Thing>> = MutableLiveData()

    //LiveData to which we post value if something went wrong
    private val _errorLiveData: MutableLiveData<Unit> = MutableLiveData()

    //LiveData to which we will post true when we need to show loading indicator
    //and false if we need to hide it
    private val _loadingLiveData: MutableLiveData<Boolean> = MutableLiveData()

    //We expose to fragment LiveData, not MutableLiveData
    val popularsLiveData: LiveData<List<Thing>>
        get() = _popularsLiveData
    val searchedLiveData: LiveData<List<Thing>>
        get() = _searchedLiveData
    val errorLiveData: LiveData<Unit>
        get() = _errorLiveData
    val loadingLiveData: LiveData<Boolean>
        get() = _loadingLiveData

    init {
        //We listen to showPopularsStream of AddFragment
        //When it emits we retrieve populars and post result to fragment
        //We check if we have cached populars
        //If not we fetch from network and save it in cache
        showPopularsStream
            .flatMapSingle {
                if (popularsCache[what]?.things.isNullOrEmpty()) {
                    //We use _loadingLiveData to show and hide loading indicator
                    NetworkRepository.getPopulars(what)
                        .doOnSubscribe { _loadingLiveData.postValue(true) }
                        .doOnEvent { _, _ -> _loadingLiveData.postValue(false) }
                        .doOnSuccess {
                            //Saving in cache
                            if (it is NetworkResult.Success) {
                                popularsCache[what] = it
                            }
                        }
                        .subscribeOn(Schedulers.io())
                } else {
                    //Returning cached result
                    Observable.just(popularsCache[what])
                        .firstOrError()
                }
            }
            .doOnNext {
                if (it is NetworkResult.Failure) {
                    _errorLiveData.postValue(Unit)
                }
            }
            .filter {
                it is NetworkResult.Success
            }
            .map {
                (it as NetworkResult.Success).things
            }
            .subscribeBy(
                onNext = {
                    _popularsLiveData.postValue(it)
                },
                onError = {
                    _errorLiveData.postValue(Unit)
                }
            )
            .addTo(disposables)

        //We listen to queryStream of AddFragment
        //When it emits string value we make search and post result to fragment
        searchStream
            .flatMapSingle { name ->
                NetworkRepository.find(what, name)
                    .doOnSubscribe { _loadingLiveData.postValue(true) }
                    .doOnEvent { _, _ -> _loadingLiveData.postValue(false) }
                    .subscribeOn(Schedulers.io())
            }
            .doOnNext {
                if (it is NetworkResult.Failure) {
                    _errorLiveData.postValue(Unit)
                }
            }
            .filter {
                it is NetworkResult.Success
            }
            .map {
                (it as NetworkResult.Success).things
            }
            .subscribeBy(
                onNext = {
                    _searchedLiveData.postValue(it)
                },
                onError = {
                    _errorLiveData.postValue(Unit)
                    Log.e("teyyubc", "", it)
                }
            )
            .addTo(disposables)
    }

    override fun onCleared() {
        super.onCleared()
        //Disposing subscriptions
        disposables.dispose()
    }

    companion object {
        //Here we will cache populars result to use during app lifecycle
        //So we will fetch popular books and popular movies once during app lifecycle
        private val popularsCache = mutableMapOf(
            Pair(movie, NetworkResult.Success(emptyList())),
            Pair(book, NetworkResult.Success(emptyList()))
        )
    }
}