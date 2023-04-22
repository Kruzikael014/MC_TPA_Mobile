package edu.bluejack22_2.MuCiJCally

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import edu.bluejack22_2.MuCiJCally.model.Playlist
import edu.bluejack22_2.MuCiJCally.model.adapters.PlaylistDetailAdapter
import edu.bluejack22_2.MuCiJCally.services.MusicService
import edu.bluejack22_2.MuCiJCally.utility.LayoutAssembler
import edu.bluejack22_2.MuCiJCally.viewmodel.MusicViewModel

class PlaylistDetailActivity : AppCompatActivity() {

    private lateinit var playlist:Playlist
    private lateinit var playlistTitle: TextView
    private lateinit var playlistDetailPLayallButton: ImageView
    private lateinit var playlistDetailPlayallLbl: TextView
    private lateinit var playlistDetailRecyclerView: RecyclerView
    private lateinit var playlistDetailAdapter: PlaylistDetailAdapter
    private lateinit var musicViewModel:MusicViewModel

    private fun init() {
        playlist = this.intent.getSerializableExtra("extras") as Playlist
        playlistTitle = findViewById(R.id.playlistDetail_nameLbl)
        playlistDetailPlayallLbl = findViewById(R.id.playlistDetail_playAllLbl)
        playlistDetailPLayallButton = findViewById(R.id.playlistDetail_playAllbutton)
        playlistDetailRecyclerView = findViewById(R.id.playlistdetail_recyclerView)
        playlistDetailAdapter = PlaylistDetailAdapter(this)
        musicViewModel = ViewModelProvider(this)[MusicViewModel::class.java]
        bindValue()
    }

    private fun bindValue() {
        playlistTitle.text = playlist.playlistName
        playlistDetailRecyclerView.layoutManager = LinearLayoutManager(this)
        playlistDetailRecyclerView.adapter = playlistDetailAdapter
        musicViewModel.musics.observe(this, Observer { playlistMusic ->
            playlistDetailAdapter.updatePlaylistDetail(playlistMusic)
        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist_detail)
        init()
        LayoutAssembler(this, R.id.header_playlistdetail).initHeader()
        LayoutAssembler(this, R.id.footer_playlistdetail).initFooter()
        musicViewModel.getPlaylistMusic(playlist.musics)
    }
}