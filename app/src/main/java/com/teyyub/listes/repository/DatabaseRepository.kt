package com.teyyub.listes.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.teyyub.listes.database.ThingDao
import com.teyyub.listes.database.Thingdatabase
import com.teyyub.listes.model.Thing
import java.util.concurrent.Executors

private const val DATABASE_NAME = "thing-database"

//Repository for working with database. It is singleton so constructor is private
class DatabaseRepository private constructor(context: Context) {

    //ThingDatabase implementation
    private val database: Thingdatabase = Room.databaseBuilder(
        context.applicationContext,
        Thingdatabase::class.java,
        DATABASE_NAME
    ).build()

    //ThingDao implementation
    private val thingDao: ThingDao = database.thingDao()

    //Executor for performing tasks in background
    private val executor = Executors.newSingleThreadExecutor()

    //functions for working with database
    fun getThings(what: String, isDone: Boolean): LiveData<List<Thing>> =
        thingDao.getThings(what, isDone)

    fun getAllThings(): LiveData<List<Thing>> = thingDao.getAllThings()

    fun addThing(thing: Thing) {
        executor.execute {
            thingDao.addThing(thing)
        }
    }

    fun updateThing(thing: Thing) {
        executor.execute {
            thingDao.updateThing(thing)
        }
    }

    fun deleteThing(thing: Thing) {
        executor.execute {
            thingDao.deleteThing(thing)
        }
    }

    fun nukeTable() {
        executor.execute {
            thingDao.nukeTable()
        }
    }

    companion object {
        //ThingRepository is singleton
        private var INSTANCE: DatabaseRepository? = null

        //it is initialized in application's onCreate() function
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = DatabaseRepository(context)
            }
        }

        //for getting the instance of ThingRepository
        fun get(): DatabaseRepository {
            return INSTANCE ?: throw IllegalStateException("CrimeRepository must be initialized")
        }
    }
}