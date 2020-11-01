package com.teyyub.listes.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.teyyub.listes.database.DoableDao
import com.teyyub.listes.database.DoableDatabase
import com.teyyub.listes.model.Doable
import java.util.concurrent.Executors

private const val DATABASE_NAME = "doable-database"

//Repository for working with database. It is singleton so constructor is private
class DatabaseRepository private constructor(context: Context) {

    //DoableDatabase implementation
    private val database: DoableDatabase = Room.databaseBuilder(
        context.applicationContext,
        DoableDatabase::class.java,
        DATABASE_NAME
    ).build()

    //DoableDao implementation
    private val doableDao: DoableDao = database.doableDao()

    //Executor for performing tasks in background
    private val executor = Executors.newSingleThreadExecutor()

    //functions for working with database
    fun getDoables(what: String, isDone: Boolean): LiveData<List<Doable>> =
        doableDao.getDoables(what, isDone)

    fun getAllDoables(): LiveData<List<Doable>> = doableDao.getAllDoables()

    fun addDoable(doable: Doable) {
        executor.execute {
            doableDao.addDoable(doable)
        }
    }

    fun updateDoable(doable: Doable) {
        executor.execute {
            doableDao.updateDoable(doable)
        }
    }

    fun deleteDoable(doable: Doable) {
        executor.execute {
            doableDao.deleteDoable(doable)
        }
    }

    fun nukeTable() {
        executor.execute {
            doableDao.nukeTable()
        }
    }

    companion object {
        //DatabaseRepository is singleton
        private var INSTANCE: DatabaseRepository? = null

        //it is initialized in application's onCreate() function
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = DatabaseRepository(context)
            }
        }

        //for getting the instance of DatabaseRepository
        fun get(): DatabaseRepository {
            return INSTANCE ?: throw IllegalStateException("DatabaseRepository must be initialized")
        }
    }
}