package com.carlosblaya.theagilemonkeystest.ui.main.player

import com.carlosblaya.theagilemonkeystest.data.database.AppDatabase
import com.carlosblaya.theagilemonkeystest.data.response.mapper.SongListMapper
import com.carlosblaya.theagilemonkeystest.domain.model.Song
import com.carlosblaya.theagilemonkeystest.ui.base.BaseViewModel

class PlayerViewModel(
    private val database:AppDatabase,
) : BaseViewModel() {

    var songListMapper: SongListMapper = SongListMapper()

    fun checkIfLikeExist(idSong:Long):Boolean{
        return database.likeDao.exists(idSong)
    }

    fun saveLike(song: Song) {
        database.likeDao.insert(songListMapper.toLikeItemEntity(song))
    }

    fun deleteLike(song: Song) {
        database.likeDao.delete(songListMapper.toLikeItemEntity(song))
    }

}