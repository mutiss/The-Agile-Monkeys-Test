package com.carlosblaya.theagilemonkeystest.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "like")
data class Like(
    @PrimaryKey
    var trackId: Long = 0,
    var trackName: String? = ""
)



