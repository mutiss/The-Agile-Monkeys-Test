package com.carlosblaya.theagilemonkeystest.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.carlosblaya.theagilemonkeystest.data.database.entities.Like

@Dao
interface LikeDao : BaseDao<Like> {

    @Query("SELECT * FROM like WHERE trackId = :id")
    fun exists(id: Long): Boolean

}