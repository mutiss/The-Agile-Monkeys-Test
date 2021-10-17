package com.carlosblaya.theagilemonkeystest.domain.usecases

import com.carlosblaya.theagilemonkeystest.data.response.AlbumListResponse
import com.carlosblaya.theagilemonkeystest.domain.repository.AlbumListRepository
import com.carlosblaya.theagilemonkeystest.util.Resource

class GetAlbumsUseCase(private val albumListRepository: AlbumListRepository){
    suspend fun getAlbums(idArtist:Long): Resource<AlbumListResponse> {
        return albumListRepository.getArtistAlbums(idArtist)
    }
}