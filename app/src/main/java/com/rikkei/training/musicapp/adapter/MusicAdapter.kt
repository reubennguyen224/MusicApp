package com.rikkei.training.musicapp.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.rikkei.training.musicapp.ui.moduleMusic.PlayMusicFragment
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.databinding.MusicItemBinding
import com.rikkei.training.musicapp.model.Song
import com.rikkei.training.musicapp.utils.ItemMoveCallback
import java.util.*

class MusicAdapter(private var dataset: ArrayList<Song>):
    RecyclerView.Adapter<MusicAdapter.MyViewHolder>(), ItemMoveCallback.ItemTouchHelperContact {

    private lateinit var mListener: OnItemClickListener

    class MyViewHolder(binding:MusicItemBinding, listener: OnItemClickListener) : RecyclerView.ViewHolder(binding.root) {
        val imageView = binding.imageMusicItem
        val titleSong = binding.titleMusicItem
        val artist = binding.artistMusicItem
        val view = binding.root

        init {
            binding.root.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        //ctx = parent.context
        return MyViewHolder(MusicItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), mListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemId
        holder.titleSong.text = dataset[position].thisTile
        holder.artist.text = dataset[position].thisArtist
        Glide.with(holder.view.context)
            .load(dataset[position].imageUri)
            .apply(RequestOptions().placeholder(R.drawable.splash_logo).fitCenter())
            .centerCrop()
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateMusicList(searchList: ArrayList<Song>){
        dataset = ArrayList()
        dataset.addAll(searchList)
        notifyDataSetChanged()
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        if(fromPosition < toPosition){
            for (i in fromPosition .. toPosition){
                Collections.swap(dataset, i, i + 1)
                Collections.swap(PlayMusicFragment.song, i, i + 1)
            }
        } else{
            for (i in fromPosition downTo toPosition step 1){
                Collections.swap(dataset, i, i - 1)
                Collections.swap(PlayMusicFragment.song, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onRowSelected(viewHolder: MyViewHolder) {
        viewHolder.view.setBackgroundColor(Color.argb(10,0,0,0))
    }

    override fun onRowClear(myViewHolder: MyViewHolder) {
        myViewHolder.view.setBackgroundColor(Color.argb(0,0,0,0))
    }

}