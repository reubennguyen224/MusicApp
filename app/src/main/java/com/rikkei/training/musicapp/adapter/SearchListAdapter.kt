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

class SearchListAdapter :
    RecyclerView.Adapter<SearchListAdapter.ListViewHolder>() {

    private var viewPool = RecyclerView.RecycledViewPool()
    lateinit var ctx: Context

    class ListViewHolder(binding: SingerRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val title = binding.titleSong
        val recyclerView = binding.listSongArtist
    }

    var dataset = ArrayList<SongDetail>()
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
    private lateinit var childAdapter: MusicAdapter

    fun update(position: Int, listener: MusicAdapter.OnItemClickListener) {
        //dataset[position].listSong = newItem
        dataset[position].listener = listener
        notifyDataSetChanged()
    }

    fun update(position: Int, newItem: ArrayList<Song>) {
        dataset[position].listSong = newItem

        notifyDataSetChanged()
        childAdapter.notifyDataSetChanged()

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.title.text = dataset[position].title
        val layoutManagers = LinearLayoutManager(ctx)
        layoutManagers.initialPrefetchItemCount = dataset[position].listSong.size
        childAdapter = MusicAdapter()
        childAdapter.dataset = dataset[position].listSong
        childAdapter.notifyDataSetChanged()
        dataset[position].listener?.let { childAdapter.setOnItemClickListener(it )}
        holder.recyclerView.apply {
            layoutManager = layoutManagers
            adapter = childAdapter
            setRecycledViewPool(viewPool)
        }
    }

    override fun getItemCount(): Int = dataset.size
}