package com.rikkei.training.musicapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.databinding.ItemArtistBinding
import com.rikkei.training.musicapp.model.Artist

class ArtistAdapter (private val artistList: ArrayList<Artist>) :
    RecyclerView.Adapter<ArtistAdapter.MyViewHolder>() {

    private lateinit var ctx: Context
    private lateinit var mListener:OnItemClickListener

    class MyViewHolder(binding: ItemArtistBinding, listener: OnItemClickListener) : RecyclerView.ViewHolder(binding.root) {
        val avatarArtist = binding.avatarArtist
        val nameArtist  = binding.nameArtist

        init {
            binding.root.setOnClickListener {
                listener.onArtistClickListener(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        ctx = parent.context
        return MyViewHolder(ItemArtistBinding.inflate(LayoutInflater.from(parent.context), parent, false), mListener)
    }

    interface OnItemClickListener{
        fun onArtistClickListener(position: Int)
    }

    fun setOnArtistClickListener(listener: OnItemClickListener){
        mListener = listener
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.nameArtist.text = artistList[position].name
        if (artistList[position].name == "Various Artist")
            Glide.with(ctx)
                .load(R.drawable.ic_va_gr)
                .centerCrop()
                .into(holder.avatarArtist)
        else if (artistList[position].avatarID == null)
            Glide.with(ctx)
                .load(R.drawable.ic_singer)
                .fitCenter()
                .into(holder.avatarArtist)
        else
            Glide.with(ctx)
                .load(artistList[position].avatarID)
                .centerInside()
                .into(holder.avatarArtist)
    }

    override fun getItemCount(): Int {
        return artistList.size
    }
}