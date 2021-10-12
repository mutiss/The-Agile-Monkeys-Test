package com.carlosblaya.theagilemonkeystest.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.carlosblaya.theagilemonkeystest.data.database.dao.LikeDao
import com.carlosblaya.theagilemonkeystest.data.database.entities.Like

@Database(
    entities = [
        Like::class
    ], version = 1, exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {
    abstract val likeDao: LikeDao
}

