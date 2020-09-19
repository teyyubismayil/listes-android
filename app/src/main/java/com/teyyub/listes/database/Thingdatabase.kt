package com.teyyub.listes.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.teyyub.listes.model.Thing

@Database(entities = [Thing::class], version = 1)
abstract class Thingdatabase: RoomDatabase() {
    abstract fun thingDao(): ThingDao
}