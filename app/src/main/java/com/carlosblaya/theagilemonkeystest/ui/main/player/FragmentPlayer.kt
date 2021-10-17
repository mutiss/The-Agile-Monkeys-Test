package com.carlosblaya.theagilemonkeystest.ui.main.player

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.IBinder
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.carlosblaya.theagilemonkeystest.R
import com.carlosblaya.theagilemonkeystest.databinding.FragmentPlayerBinding
import com.carlosblaya.theagilemonkeystest.domain.model.Song
import com.carlosblaya.theagilemonkeystest.ui.base.BaseFragment
import com.carlosblaya.theagilemonkeystest.ui.main.MainActivity
import com.carlosblaya.theagilemonkeystest.ui.search.songs.adapter.SongListAdapter
import com.carlosblaya.theagilemonkeystest.util.*
import com.carlosblaya.theagilemonkeystest.util.streaming.PlayerService
import com.carlosblaya.theagilemonkeystest.util.streaming.player.PlayerHolder
import com.google.android.exoplayer2.Player
import org.koin.android.viewmodel.ext.android.viewModel

class FragmentPlayer : BaseFragment<FragmentPlayerBinding, PlayerViewModel>() {

    override val viewModel: PlayerViewModel by viewModel()

    override fun layout(): Int = R.layout.fragment_player

    //Streaming Audio
    lateinit var mPlayerHolder: PlayerHolder
    var mCurrentSongPlaying: Song? = null
    var binded = false

    //Animation
    lateinit var pulse: Animation
    lateinit var discAnimation: ObjectAnimator
    var actualColorLayoutPlayer: Int? = Color.BLACK

    override fun init() {
        pulse = AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.heartbeat_big
        )
        discAnimation = ObjectAnimator.ofFloat(
            binding.ivAlbum,
            "rotation", 0f, 360f
        )
        discAnimation.repeatCount = ObjectAnimator.INFINITE
        discAnimation.repeatMode = ObjectAnimator.RESTART
        discAnimation.duration = 2000
        discAnimation.interpolator = LinearInterpolator()
    }


    /**
     * Connection with PlayerService when clicking on a song
     */
    fun connectPlayerService(
        song: Song,
        positionAdapter: Int,
        songListAdapter: SongListAdapter
    ) {
        mCurrentSongPlaying = song

        if (binding.llPlayer.visibility != View.VISIBLE)
            binding.llPlayer.slideToTop()

        setVisibilityPlayerView(song, songListAdapter, positionAdapter)

        if (connection.isServiceConnected()) {
            if (binded) {
                doUnbind()
            }
        }

        //Calling PlayerService and passing data to PlayerNotification
        val intent = Intent(requireContext(), PlayerService::class.java)
        intent.putExtra("title", song.trackName)
        intent.putExtra("image", song.artworkUrl100)
        intent.putExtra("previewUrl", song.previewUrl)
        requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    /**
     * Create our connection to the service to be used in our bindService call.
     */
    private val connection = object : ServiceConnection {

        private var isServiceConnected = false

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceConnected = false
            binded = false
        }

        /**
         * Called after a successful bind with our PlayerService.
         */
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            isServiceConnected = true
            binded = true
            if (service is PlayerService.PlayerServiceBinder) {
                mPlayerHolder = service.getPlayerHolderInstance()
                mPlayerHolder.audioFocusPlayer.addListener(object :
                    Player.DefaultEventListener() {
                    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                        if (playWhenReady && playbackState == Player.STATE_READY) {
                            binding.equaliser.playAnimation()
                            discAnimation.start()
                        } else if (playWhenReady && playbackState == Player.STATE_ENDED) {
                            stopPlayer()
                        } else if (playWhenReady && playbackState == Player.STATE_BUFFERING) {
                            // buffering (plays when data available)
                        } else if (playWhenReady) {
                            // stopped from notification
                            // or ended (plays when seek away from end)
                            stopPlayer()
                        } else {
                            // player paused in any state
                            binding.equaliser.pauseAnimation()
                            discAnimation.pause()
                        }
                    }
                })
            }
        }

        fun isServiceConnected(): Boolean {
            return isServiceConnected
        }
    }

    /**
     * Unbind connection and player service
     */
    fun doUnbind() {
        requireActivity().unbindService(connection)
        requireActivity().stopService(Intent(requireActivity(), PlayerService::class.java))
        binded = false
    }

    /**
     * Setting all values to bottom player view
     */
    fun setVisibilityPlayerView(
        song: Song,
        songListAdapter: SongListAdapter,
        positionAdapter: Int
    ) {

        (requireActivity() as MainActivity).binding.flContainer.setPadding(
            0,
            0,
            0,
            80.px
        ) //Space for player view

        binding.equaliser.playAnimation()

        binding.ivAlbum.fromUrl(song.artworkUrl100 ?: "")

        changeBackgroundColorPlayerView(song)

        discAnimation.start()

        binding.tvNameArtist.text = song.artistName
        binding.tvNameSong.text = song.trackName
        binding.tvNameSong.isSelected = true

        if (viewModel.checkIfLikeExist(song.trackId)) {
            song.isLiked = true
            setAppearenceLiked()
        } else {
            song.isLiked = false
            setAppearenceNotLiked()
        }

        binding.llLike.setOnClickListener {
            if (song.isLiked) {
                viewModel.deleteLike(song)
                song.isLiked = false
                setAppearenceNotLiked()
            } else {
                viewModel.saveLike(song)
                song.isLiked = true
                setAppearenceLiked()
            }
            binding.ivFavourite.startAnimation(pulse)
            songListAdapter.notifyItemChanged(positionAdapter)
        }
    }

    /**
     * Changing background color of player view with transition using dark vibrant color of cover
     */
    fun changeBackgroundColorPlayerView(song: Song) {

        Glide.with(this)
            .asBitmap()
            .load(song.artworkUrl100 ?: "")
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    Palette.Builder(resource).generate {
                        it?.let { palette ->
                            val auxActualColor: Int? = actualColorLayoutPlayer

                            val colorNotification = palette.getDarkVibrantColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.colorPrimaryDark
                                )
                            )

                            actualColorLayoutPlayer = colorNotification

                            val colorAnimation =
                                ValueAnimator.ofObject(
                                    ArgbEvaluator(),
                                    auxActualColor,
                                    colorNotification
                                )
                            colorAnimation.duration = 750 // milliseconds

                            colorAnimation.addUpdateListener { animator ->
                                binding.llPlayer.setBackgroundColor(animator.animatedValue as Int)
                            }
                            colorAnimation.start()
                        }
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

    /**
     * Stop player view and animations, and unbind connection
     */
    fun stopPlayer() {
        (requireActivity() as MainActivity).binding.flContainer.setPadding(
            0,
            0,
            0,
            0
        ) // Removing space given for player view
        binding.equaliser.cancelAnimation()
        discAnimation.cancel()
        mPlayerHolder.audioFocusPlayer.playWhenReady = false
        binding.llPlayer.slideToBottom()
        binding.llPlayer.gone()
        mCurrentSongPlaying = null
        if (binded)
            doUnbind()

    }

    fun checkIfAudioIsRunning() {
        if (binding.llPlayer.visibility == View.VISIBLE) {
            stopPlayer()
        }
    }

    fun setSameStatusPlayerViewAsAdapterLiked(song: Song) {
        if (binding.llPlayer.isVisible) {
            if (mCurrentSongPlaying != null) {
                if (mCurrentSongPlaying?.trackId == song.trackId) {
                    if (song.isLiked) {
                        song.isLiked = true
                        setAppearenceLiked()
                    } else {
                        song.isLiked = false
                        setAppearenceNotLiked()
                    }
                    binding.ivFavourite.startAnimation(pulse)
                }
            }

        }
    }

    fun setAppearenceLiked() {
        binding.ivFavourite.setImageResource(R.drawable.outline_favorite_24)
        binding.ivFavourite.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorLike
            ), android.graphics.PorterDuff.Mode.SRC_IN
        )
    }

    fun setAppearenceNotLiked() {
        binding.ivFavourite.setImageResource(R.drawable.outline_favorite_border_24)
        binding.ivFavourite.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                R.color.white
            ), android.graphics.PorterDuff.Mode.SRC_IN
        )
    }

}