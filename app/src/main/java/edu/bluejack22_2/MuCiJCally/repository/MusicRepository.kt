package edu.bluejack22_2.MuCiJCally.repository

import Utility
import android.net.Uri
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import edu.bluejack22_2.MuCiJCally.model.Music
import edu.bluejack22_2.MuCiJCally.utility.FirebaseHandler

object MusicRepository {

    val firebase = FirebaseHandler.musicRef

    fun uploadMusic(musicTitle: String, musicURI: Uri, coverURI: Uri, uploaderID: String) {
        val id: String = firebase.push().key!!
        var musicURL: String? = null
        var coverURL: String? = null
        val musicExt: String? = Utility.getFileExtensionFromUri(musicURI)
        val coverExt: String? = Utility.getFileExtensionFromUri(coverURI)
        val musicRef = FirebaseHandler.getStorageReference()
            .child("musics")
            .child("$id$musicExt")
        val coverRef = FirebaseHandler.getStorageReference()
            .child("cover")
            .child("musics")
            .child("$id$coverExt")
        val uploadTask = musicRef.putFile(musicURI)
        uploadTask.addOnSuccessListener { _ ->
            musicRef.downloadUrl.addOnSuccessListener { downloadURL ->
                musicURL = downloadURL.toString()
                val uploadTask = coverRef.putFile(coverURI)
                uploadTask.addOnSuccessListener { _ ->
                    coverRef.downloadUrl.addOnSuccessListener { downloadURL ->
                        coverURL = downloadURL.toString()
                        FirebaseHandler.writeMusic(Music(id, musicTitle, musicURL!!, coverURL!!, uploaderID), id)
                    }.addOnFailureListener {
                        println("Failed to get uploaded music URL!")
                    }
                }.addOnFailureListener {
                    println("Failed to run upload task!")
                }
            }.addOnFailureListener {
                println("Failed to get uploaded music URL!")
            }
        }.addOnFailureListener {
            println("Failed to run upload task!")
        }
    }

    fun getPlaylistMusic(musics: ArrayList<String>, callback: (List<Music>) -> Unit) {
        val musicList = mutableListOf<Music>()
        for (musicID in musics) {
            firebase.child(musicID).addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val music = snapshot.getValue(Music::class.java)
                    music?.let {
                        musicList.add(music)
                    }
                    if (musicList.size == musics.size) {
                        callback(musicList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Failed to get the playlist musics!")
                }
            })
        }
    }

}