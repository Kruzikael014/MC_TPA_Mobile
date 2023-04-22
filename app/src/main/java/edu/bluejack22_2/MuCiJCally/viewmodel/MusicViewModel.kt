package edu.bluejack22_2.MuCiJCally.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bluejack22_2.MuCiJCally.model.Music
import edu.bluejack22_2.MuCiJCally.model.Playlist
import edu.bluejack22_2.MuCiJCally.repository.MusicRepository

class MusicViewModel : ViewModel() {

    private val _musics = MutableLiveData<List<Music>>()
    val musics: LiveData<List<Music>> get() = _musics

    fun uploadMusic(musicTitle: String, musicURI: Uri, coverURI: Uri, uploaderID: String) {
        MusicRepository.uploadMusic(musicTitle, musicURI, coverURI, uploaderID)
    }

    fun getPlaylistMusic(musics: ArrayList<String>) {
        MusicRepository.getPlaylistMusic(musics) { playlistMusics ->
            _musics.postValue(playlistMusics)
        }
    }

}