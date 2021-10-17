package com.carlosblaya.theagilemonkeystest.domain.usecases

import com.carlosblaya.theagilemonkeystest.data.response.SongListResponse
import com.carlosblaya.theagilemonkeystest.domain.repository.SongListRepository
import com.carlosblaya.theagilemonkeystest.util.Resource

class GetSongsUseCase(private val songsRepository: SongListRepository){
    suspend fun getSongs(idAlbum:Long): Resource<SongListResponse> {
        return songsRepository.getAlbumSongs(idAlbum)
    }
}