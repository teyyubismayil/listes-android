package com.teyyub.listes.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.teyyub.listes.model.Thing

@Dao
interface ThingDao{

    @Query("SELECT * FROM thing WHERE what = :what AND isDone = :isDone")
    fun getThings(what: String, isDone: Boolean): LiveData<List<Thing>>

    @Query("SELECT * FROM thing")
    fun getAllThings(): LiveData<List<Thing>>

    @Insert
    fun addThing(thing: Thing)

    @Update
    fun updateThing(thing: Thing)

    @Delete
    fun deleteThing(thing: Thing)

    @Query("DELETE FROM thing")
    fun nukeTable()
}