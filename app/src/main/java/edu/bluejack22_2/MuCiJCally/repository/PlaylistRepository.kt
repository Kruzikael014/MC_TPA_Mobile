import android.net.Uri
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import edu.bluejack22_2.MuCiJCally.model.Playlist
import edu.bluejack22_2.MuCiJCally.utility.FirebaseHandler
import java.util.*
import kotlin.collections.ArrayList

object PlaylistRepository {

    fun insertPlaylist(
        playlistName: String,
        playlistCover: Uri,
        uploaderID: String,
        musics: ArrayList<String>
    ) {
        FirebaseHandler.uploadPlaylist(playlistCover, playlistName, uploaderID, musics)
    }

    fun getPlaylists(accountID: String, callback: (List<Playlist>) -> Unit) {
        val playlistsRef = FirebaseHandler.playlistRef
        playlistsRef.orderByChild("accountID")
            .equalTo(accountID)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val playlists = mutableListOf<Playlist>()
                    for (snapshot in dataSnapshot.children) {
                        val playlist = snapshot.getValue(Playlist::class.java)
                        playlist?.let {
                            playlists.add(playlist)
                        }
                    }
                    callback(playlists)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("Failed to fetch data from server!")
                }
            })
    }

}
