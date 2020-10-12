package com.teyyub.listes.application

import android.app.Application
import com.teyyub.listes.repository.DatabaseRepository

class ListesApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        //Initializing repository which is singleton
        DatabaseRepository.initialize(this)
    }
}