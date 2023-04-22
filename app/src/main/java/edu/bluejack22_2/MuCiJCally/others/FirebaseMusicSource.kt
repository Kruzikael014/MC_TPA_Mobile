package edu.bluejack22_2.MuCiJCally.others

import Utility
import Utility.toMediaMetadataCompat
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import androidx.core.net.toUri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import edu.bluejack22_2.MuCiJCally.model.Music
import edu.bluejack22_2.MuCiJCally.others.State.*
import edu.bluejack22_2.MuCiJCally.repository.MusicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseMusicSource @Inject constructor(
    private val musicDatabase: MusicRepository
) {

    var songs = emptyList<MediaMetadataCompat>()

    suspend fun fetchMediaMetadata(musicList: ArrayList<String>) = withContext(Dispatchers.IO) {
        state = STATE_INITIALIZING
        var allSong: List<Music>? = null
        musicDatabase.getPlaylistMusic(musicList) { musicList ->
            allSong = musicList
        }
        songs = allSong!!.map { song ->
            song.toMediaMetadataCompat()
        }
        state = STATE_INITIALIZED
    }

    fun asMediaSource(dataSourceFactory: DefaultDataSource.Factory): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        songs.forEach { song ->
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(song.getString(METADATA_KEY_MEDIA_URI).toUri()))
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }

    fun asMediaItems() = songs.map { song ->
        val desc = MediaDescriptionCompat.Builder()
            .setMediaId(song.description.mediaId) // song.getString(METADATA_KEY_MEDIA_ID)
            .setTitle(song.description.title) //song.getString(METADATA_KEY_TITLE)
            .setMediaUri(song.description.mediaUri) // song.getString(METADATA_KEY_MEDIA_URI).toUri()
            .setIconUri(song.description.iconUri) // song.getString(METADATA_KEY_ALBUM_ART_URI).toUri()
            .setSubtitle(song.getString(METADATA_KEY_ARTIST))
            .build()
        MediaBrowserCompat.MediaItem(desc, FLAG_PLAYABLE)
    }.toMutableList()

    private val onReadyListener = mutableListOf<(Boolean) -> Unit>()

    private var state: State = STATE_CREATED
        set(value) {
            if (value == STATE_INITIALIZED || value == STATE_ERROR) {
                synchronized(onReadyListener) {
                    field = value
                    onReadyListener.forEach { listener ->
                        listener(state == STATE_INITIALIZED)
                    }
                }
            } else {
                field = value
            }
        }

    fun whenReady(action: (Boolean) -> Unit): Boolean {
        return if (state == STATE_CREATED || state == STATE_INITIALIZING) {
            onReadyListener += action
            false
        } else {
            action(state == STATE_INITIALIZED)
            true
        }
    }

}


enum class State {
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR
}