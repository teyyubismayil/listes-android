package com.teyyub.listes.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.teyyub.listes.model.Doable

@Database(entities = [Doable::class], version = 1)
abstract class DoableDatabase: RoomDatabase() {
    abstract fun doableDao(): DoableDao
}