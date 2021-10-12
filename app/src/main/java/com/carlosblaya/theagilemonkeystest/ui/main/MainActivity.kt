package com.carlosblaya.theagilemonkeystest.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.carlosblaya.theagilemonkeystest.R
import com.carlosblaya.theagilemonkeystest.databinding.ActivityMainBinding
import com.carlosblaya.theagilemonkeystest.ui.main.player.FragmentPlayer
import com.carlosblaya.theagilemonkeystest.ui.search.albums.FragmentAlbums
import com.carlosblaya.theagilemonkeystest.ui.search.artists.FragmentSearchArtists
import com.carlosblaya.theagilemonkeystest.util.FragmentUtil
import com.carlosblaya.theagilemonkeystest.util.fadeOut
import com.carlosblaya.theagilemonkeystest.util.viewBinding
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    val viewModel: MainViewModel by viewModel()
    val binding by viewBinding(ActivityMainBinding::inflate)

    //Streaming Audio
    var mPlayerFragment: FragmentPlayer? = null

    /**
     * Receiver to get back to app if we click on audio notification
     */
    class LaunchPlayerBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val notifyIntent = Intent(context, MainActivity::class.java)
            notifyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(notifyIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupViews()
        setupClicks()
    }

    private fun setupClicks() {
        binding.headbar.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupViews() {
        FragmentUtil.changeMainFragment(this, FragmentUtil.TAG_SEARCH, null)
        mPlayerFragment = supportFragmentManager.findFragmentById(R.id.playerFragment) as FragmentPlayer
    }

    fun setTitleHeadbar(title: String) {
        binding.headbar.tvTitleHeadbar.text = title
    }

    fun getTitleHeader(): TextView {
        return binding.headbar.tvTitleHeadbar
    }

    fun getImageBack(): ImageView {
        return binding.headbar.ivBack
    }

    override fun onBackPressed() {
        val fm: FragmentManager = supportFragmentManager
        fm.let {
            if (fm.backStackEntryCount > 1) {
                fm.popBackStackImmediate()
                val f: Fragment? = fm.findFragmentById(R.id.fl_container)
                f.let {
                    if (f is FragmentAlbums) {
                        setTitleHeadbar(f.mArtistNameSelected)
                    } else if (f is FragmentSearchArtists) {
                        setTitleHeadbar(resources.getString(R.string.search))
                        binding.headbar.ivBack.fadeOut()
                    }
                }
            } else {
                finish()
            }
        }
    }
}