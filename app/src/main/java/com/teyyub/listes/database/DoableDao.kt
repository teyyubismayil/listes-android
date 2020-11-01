package com.teyyub.listes.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.teyyub.listes.model.Doable

@Dao
interface DoableDao{

    @Query("SELECT * FROM doable WHERE what = :what AND isDone = :isDone")
    fun getDoables(what: String, isDone: Boolean): LiveData<List<Doable>>

    @Query("SELECT * FROM doable")
    fun getAllDoables(): LiveData<List<Doable>>

    @Insert
    fun addDoable(doable: Doable)

    @Update
    fun updateDoable(doable: Doable)

    @Delete
    fun deleteDoable(doable: Doable)

    @Query("DELETE FROM doable")
    fun nukeTable()
}