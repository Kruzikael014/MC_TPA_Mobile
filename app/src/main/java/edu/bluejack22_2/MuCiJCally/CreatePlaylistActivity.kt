package edu.bluejack22_2.MuCiJCally

import edu.bluejack22_2.MuCiJCally.viewmodel.PlaylistViewModel
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import edu.bluejack22_2.MuCiJCally.model.Account
import edu.bluejack22_2.MuCiJCally.others.Constants
import edu.bluejack22_2.MuCiJCally.others.Type
import edu.bluejack22_2.MuCiJCally.utility.Validator
import edu.bluejack22_2.MuCiJCally.utility.FileHandler
import edu.bluejack22_2.MuCiJCally.utility.PageNavigator
import edu.bluejack22_2.MuCiJCally.viewmodel.AccountViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Vector

class CreatePlaylistActivity : AppCompatActivity() {

    private lateinit var playlistNameInput: EditText
    private lateinit var uploadImageButton: Button
    private lateinit var createPlaylistButton: Button
    private lateinit var createPlaylistBackButton: Button
    private var selectedURI:Uri? = null
    private lateinit var playlistViewModel: PlaylistViewModel
    private var accountViewModel:AccountViewModel = AccountViewModel()
    private var currentUserID:String? = null

    private fun init() {

        playlistViewModel = ViewModelProvider(this).get(PlaylistViewModel::class.java)
        playlistNameInput = findViewById(R.id.playlist_name_edtext)
        uploadImageButton = findViewById(R.id.upload_image_btn)
        createPlaylistButton = findViewById(R.id.btn_create_playlist)
        createPlaylistBackButton = findViewById(R.id.btn_back_create_playlist)
        accountViewModel.getCurrentAccount(this).observe(this) { accountSession ->
            if (accountSession != null && accountSession.success) {
                currentUserID = accountSession.account?.id
            }
        }

        setAction()
    }

    private fun setAction() {

        uploadImageButton.setOnClickListener {
            FileHandler.openFileSystemForImageFile(this@CreatePlaylistActivity, Type.PLAYLIST)
        }

        createPlaylistButton.setOnClickListener {
            val playlistName = playlistNameInput.text.toString()
            // initialize an empty array that will store song id later
            val musics = ArrayList<String>()
            if (!Validator.validatePlaylistForm(playlistName, selectedURI)) {
                Toast.makeText(this, "Field cannot be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TO BE DELETE
            currentUserID = "353a28b8-d833-11ed-afa1-0242ac120002"
            // TO BE DELETE

            playlistViewModel.createPlaylist(playlistName, selectedURI!!, currentUserID!!, musics)
            clear()
            PageNavigator.switchPage(this, HomeActivity::class.java)
            Toast.makeText(this, "Playlist successfully created!", Toast.LENGTH_LONG).show()
        }

        createPlaylistBackButton.setOnClickListener {
            finish()
        }
    }

    private fun clear() {
        playlistNameInput.text.clear()
        selectedURI = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_playlist)
        init()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_CODE_IMAGE_CHOOSER_PLAYLIST && resultCode == Activity.RESULT_OK) {
            selectedURI = data?.data
            Toast.makeText(this, "Successfully uploaded $selectedURI", Toast.LENGTH_LONG).show()
        }
    }

}