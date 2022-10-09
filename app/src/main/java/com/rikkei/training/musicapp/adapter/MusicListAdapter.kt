package com.rikkei.training.musicapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rikkei.training.musicapp.databinding.SingerRecyclerItemBinding
import com.rikkei.training.musicapp.model.Song
import com.rikkei.training.musicapp.model.SongDetail

class MusicListAdapter :
    RecyclerView.Adapter<MusicListAdapter.ListViewHolder>() {

    private var viewPool = RecyclerView.RecycledViewPool()
    lateinit var ctx: Context

    class ListViewHolder(binding: SingerRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val title = binding.titleSong
        val recyclerView = binding.listSongArtist
    }

    var dataset = ArrayList<SongDetail>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        ctx = parent.context
        return ListViewHolder(
            SingerRecyclerItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    fun update(position: Int, newCartItem: ArrayList<Song>, listener: MusicAdapter.OnItemClickListener) {
        dataset[position].listSong = newCartItem
        dataset[position].listener = listener
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.title.text = dataset[position].title
        val layoutManager = LinearLayoutManager(ctx)
        layoutManager.initialPrefetchItemCount = dataset[position].listSong.size

        val childAdapter = MusicAdapter()
        childAdapter.dataset = dataset[position].listSong
        childAdapter.notifyDataSetChanged()
        childAdapter.setOnItemClickListener(dataset[position].listener!!)
        holder.recyclerView.layoutManager = layoutManager
        holder.recyclerView.adapter = childAdapter
        holder.recyclerView.setRecycledViewPool(viewPool)
    }

    override fun getItemCount(): Int = dataset.size
}