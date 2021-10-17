package com.carlosblaya.theagilemonkeystest.ui.search.albums

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.carlosblaya.theagilemonkeystest.data.response.AlbumListResponse
import com.carlosblaya.theagilemonkeystest.domain.usecases.GetAlbumsUseCase
import com.carlosblaya.theagilemonkeystest.ui.base.BaseViewModel
import com.carlosblaya.theagilemonkeystest.util.Resource
import kotlinx.coroutines.Dispatchers

class AlbumsViewModel(
    private val albumsUseCase: GetAlbumsUseCase
) : BaseViewModel() {

    var idArtist: Long = 0

    fun getArtistAlbums(): LiveData<Resource<AlbumListResponse>> = liveData(Dispatchers.IO) {
        emit(Resource.loading(null))
        emit(albumsUseCase.getAlbums(idArtist))
    }

}