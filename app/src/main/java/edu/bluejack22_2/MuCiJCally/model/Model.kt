package edu.bluejack22_2.MuCiJCally.model

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

@IgnoreExtraProperties
data class Account(
    val id: String = "",
    val email: String = "",
    val username: String = "",
    var password: String = "",
    var sessionID: String = ""
): Serializable

@IgnoreExtraProperties
data class Music(
    val id: String = "",
    val title: String = "",
    val musicURL: String = "",
    val coverURL: String = "",
    val uploaderID: String = ""
): Serializable

@IgnoreExtraProperties
data class Playlist(
    val id: String = "",
    val playlistName: String = "",
    val accountID: String = "",
    val coverURL: String = "",
    var musics: ArrayList<String> = ArrayList()
) : Serializable
