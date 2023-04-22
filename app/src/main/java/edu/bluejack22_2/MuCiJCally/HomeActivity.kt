package edu.bluejack22_2.MuCiJCally

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import edu.bluejack22_2.MuCiJCally.model.AccountSession
import edu.bluejack22_2.MuCiJCally.model.adapters.PlaylistAdapter
import edu.bluejack22_2.MuCiJCally.utility.LayoutAssembler
import edu.bluejack22_2.MuCiJCally.utility.FirebaseHandler
import edu.bluejack22_2.MuCiJCally.utility.PageNavigator
import edu.bluejack22_2.MuCiJCally.viewmodel.AccountViewModel
import edu.bluejack22_2.MuCiJCally.viewmodel.PlaylistViewModel
import java.util.prefs.PreferenceChangeEvent
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

//    @Inject
//    lateinit var glide:RequestManager

    private lateinit var addButton:ImageView;
    private lateinit var playlistViewModel:PlaylistViewModel
    private lateinit var playlistRecyclerView: RecyclerView
    private lateinit var playlistAdapter:PlaylistAdapter
    private lateinit var accountViewModel: AccountViewModel

    private fun createDropDownMenu(context: Context, imageView: ImageView) {
        val popupMenu = PopupMenu(context, imageView)
        popupMenu.menuInflater.inflate(R.menu.menu_dropdown, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_create_playlist -> {
                    PageNavigator.switchPage(this, CreatePlaylistActivity::class.java)
                    true
                }
                R.id.menu_upload_music -> {
                    PageNavigator.switchPage(this, UploadMusicActivity::class.java)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    fun init() {
        playlistViewModel = ViewModelProvider(this).get(PlaylistViewModel::class.java)
        addButton = findViewById(R.id.add_playlist_button)
        playlistRecyclerView = findViewById(R.id.home_page_recycler)
        playlistAdapter = PlaylistAdapter(this)
        setAction()
    }

    fun setAction() {
        playlistRecyclerView.layoutManager = LinearLayoutManager(this)
        playlistRecyclerView.adapter = playlistAdapter
        addButton.setOnClickListener {
            createDropDownMenu(this, addButton)
        }
        playlistViewModel.playlists.observe(this, Observer { playlists ->
            playlistAdapter.updatePlaylists(playlists)
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        init()
        LayoutAssembler(this, R.id.header_home).initHeader()
        LayoutAssembler(this, R.id.footer_home).initFooter()
        accountViewModel = AccountViewModel()

        val session = accountViewModel.getAccountSession(this)

        if(session?.account != null) {
            playlistViewModel.fetchPlaylists(session.account!!.id)
        }
    }
}
