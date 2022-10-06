package com.rikkei.training.musicapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.databinding.AlbumItemBinding
import com.rikkei.training.musicapp.model.Artist

class NewSingerAdapter:
    RecyclerView.Adapter<NewSingerAdapter.MyViewHolder>() {

    lateinit var ctx: Context
    var dataset = ArrayList<Artist>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class MyViewHolder(binding: AlbumItemBinding, data: ArrayList<Artist>) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        val imageView = binding.imageMusicItem
        val titleSong = binding.titleMusicItem
        override fun onClick(v: View?) {

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        ctx = parent.context
        return MyViewHolder(AlbumItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), dataset)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemId
        holder.titleSong.text = dataset[position].name
        if (dataset[position].name == "Various Artist")
            Glide.with(ctx)
                .load(R.drawable.ic_va_gr)
                .centerCrop()
                .into(holder.imageView)
        else
            Glide.with(ctx)
                .load(R.drawable.ic_singer)
                .fitCenter()
                .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }


}