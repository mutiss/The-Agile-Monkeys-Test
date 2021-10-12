package com.carlosblaya.theagilemonkeystest.ui.search.songs.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.carlosblaya.theagilemonkeystest.R
import com.carlosblaya.theagilemonkeystest.databinding.ItemSongBinding
import com.carlosblaya.theagilemonkeystest.domain.model.Song
import com.carlosblaya.theagilemonkeystest.util.fromUrl
import com.carlosblaya.theagilemonkeystest.util.gone
import com.carlosblaya.theagilemonkeystest.util.show


class SongListAdapter(private val context: Context, private val list: MutableList<Song>,private val likedListener:SongLikedListener, private val onItemClick: (item: Song, position:Int) -> Unit)
    : RecyclerView.Adapter<SongListAdapter.AlbumViewHolder>() {

    private val pulseReverse: Animation = AnimationUtils.loadAnimation(
        context,
        R.anim.heartbeat_reverse
    )

    private val pulseBig: Animation = AnimationUtils.loadAnimation(
        context,
        R.anim.heartbeat_big
    )

    interface SongLikedListener{
        fun onSongLiked(song:Song)
    }

    class AlbumViewHolder(val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root) {
        fun render(likedListener:SongLikedListener, position: Int, pulse:Animation, pulseReverse:Animation, context: Context, item: Song, onItemClick: (item: Song, position:Int) -> Unit) {

            if(item.trackName != null){
                binding.tvNameSong.text = item.trackName
            }
            binding.ivAlbum.fromUrl(item.artworkUrl100 ?: "")

            if(item.kind == Song.KIND_TYPE_MUSIC_VIDEO){
                binding.ivVideo.show()
            }else{
                binding.ivVideo.gone()
            }

            itemView.setOnClickListener {
                itemView.startAnimation(pulseReverse)
                onItemClick(item,position)
            }

            if(item.isLiked){
                binding.ivFavourite.setImageResource(R.drawable.outline_favorite_24)
                binding.ivFavourite.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.colorLike
                    ), android.graphics.PorterDuff.Mode.SRC_IN
                )
            }else{
                binding.ivFavourite.setImageResource(R.drawable.outline_favorite_border_24)
                binding.ivFavourite.setColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.white
                    ), android.graphics.PorterDuff.Mode.SRC_IN
                )
            }

            likedListener.let {
                binding.llLike.setOnClickListener {
                    binding.ivFavourite.startAnimation(pulse)
                    likedListener.onSongLiked(item)
                }
            }
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(context), parent, false)
        return AlbumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val item = list[position]
        holder.render(likedListener,position,pulseBig,pulseReverse,context, item, onItemClick)
    }

    fun addAll(itemList: MutableList<Song>) {
        list.addAll(itemList)
        notifyDataSetChanged()
    }

}