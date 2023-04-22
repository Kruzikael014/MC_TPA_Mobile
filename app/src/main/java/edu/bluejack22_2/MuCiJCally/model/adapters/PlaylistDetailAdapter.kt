package edu.bluejack22_2.MuCiJCally.model.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import edu.bluejack22_2.MuCiJCally.R
import edu.bluejack22_2.MuCiJCally.model.Music
import edu.bluejack22_2.MuCiJCally.services.MusicService
import edu.bluejack22_2.MuCiJCally.utility.MusicPlayer
import edu.bluejack22_2.MuCiJCally.viewmodel.AccountViewModel
import javax.inject.Inject


class PlaylistDetailAdapter @Inject constructor(
    private val glide: RequestManager
) :
    RecyclerView.Adapter<PlaylistDetailAdapter.PlaylistDetailViewHolder>() {

    private val musics = mutableListOf<Music>()

    class PlaylistDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songCover: ImageView = itemView.findViewById(R.id.musicItem_cover)
        val songTitle: TextView = itemView.findViewById(R.id.musicItem_title)
        val songUploader: TextView = itemView.findViewById(R.id.musicItem_uploader)
        val songPlayButton: ImageView = itemView.findViewById(R.id.musicItem_playButton)
        val songDuration: TextView = itemView.findViewById(R.id.musicItem_duration)
    }

    override fun onBindViewHolder(
        holder: PlaylistDetailAdapter.PlaylistDetailViewHolder,
        position: Int
    ) {
        // Get music value
        val music = musics[position]
        // Bind cover
        Glide.with(holder.itemView)
            .load(music.coverURL)
            .into(holder.songCover)
        // Bind title
        holder.songTitle.text = music.title
        // Bind uploader
        val accountViewModel: AccountViewModel = AccountViewModel()
        accountViewModel.getAccountByID(music.uploaderID) { account ->
            if (account != null) {
                holder.songUploader.text = String.format(
                    "%s%s",
                    holder.itemView.context.getString(R.string.music_uploader_const),
                    " ${account.username}"
                )
            } else {
                holder.songUploader.text = String.format(
                    "%s%s",
                    holder.itemView.context.getString(R.string.music_uploader_const),
                    " Anonymous"
                )
            }
        }
        // Set action if Play Button clicked
        holder.songPlayButton.setOnClickListener {

        }
        // Bind duration
        Utility.getDurationFromFirebaseUrl(music.musicURL) { duration ->
            holder.songDuration.text = duration
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistDetailAdapter.PlaylistDetailViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.music_item, parent, false)
        return PlaylistDetailViewHolder(view)
    }

    override fun getItemCount(): Int {
        return musics.size
    }

    fun updatePlaylistDetail(newMusics: List<Music>) {
        musics.clear()
        musics.addAll(newMusics)
        notifyDataSetChanged()
    }

}