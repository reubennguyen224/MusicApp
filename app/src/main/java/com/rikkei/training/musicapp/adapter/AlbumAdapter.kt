package com.rikkei.training.musicapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rikkei.training.musicapp.databinding.MusicItemBinding
import com.rikkei.training.musicapp.model.Album

class AlbumAdapter(val dataset: Album,val ctx: Context) :
    RecyclerView.Adapter<AlbumAdapter.MyViewHolder>() {

    private lateinit var mListener: OnClickListener

    class MyViewHolder(binding: MusicItemBinding, listener: OnClickListener) :
        RecyclerView.ViewHolder(binding.root) {
        val imageView = binding.imageMusicItem
        val titleSong = binding.titleMusicItem
        val artist = binding.artistMusicItem

        init {
            binding.root.setOnClickListener {
                listener.onAlbumItemClickListener(adapterPosition)
            }
        }
    }

    interface OnClickListener {
        fun onAlbumItemClickListener(position: Int)
    }

    fun setOnAlbumItemClickListener(listener: OnClickListener) {
        this.mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            MusicItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), mListener
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemId
        holder.titleSong.text = dataset[position].name
        holder.artist.text = dataset[position].singer_name
        Glide.with(ctx)
            .load(dataset[position].image)
            .centerCrop()
            .into(holder.imageView)

    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }


}