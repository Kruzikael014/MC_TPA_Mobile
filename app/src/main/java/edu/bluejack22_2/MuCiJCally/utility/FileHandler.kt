package edu.bluejack22_2.MuCiJCally.utility

import android.app.Activity
import android.content.Intent
import edu.bluejack22_2.MuCiJCally.others.Constants
import edu.bluejack22_2.MuCiJCally.others.Type

object FileHandler {
    fun openFileSystemForImageFile(activity: Activity, type: Type) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        val requestCode = if (type == Type.PLAYLIST) Constants.REQUEST_CODE_IMAGE_CHOOSER_PLAYLIST else Constants.REQUEST_CODE_IMAGE_CHOOSER_MUSIC
        activity.startActivityForResult(Intent.createChooser(intent, "Choose File"), requestCode)
    }
    fun openFileSystemForMusicFile(activity:Activity) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "audio/*"
        activity.startActivityForResult(Intent.createChooser(intent, "Choose File"), Constants.REQUEST_CODE_MUSIC_CHOOSER_MUSIC)
    }
}