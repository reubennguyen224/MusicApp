package com.rikkei.training.musicapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rikkei.training.musicapp.databinding.SingerRecyclerItemBinding
import com.rikkei.training.musicapp.model.SingerDetail

class SingerDetailAdapter(private val dataset: ArrayList<SingerDetail>) :
    RecyclerView.Adapter<SingerDetailAdapter.SingerDetailViewModel>() {

    private var viewPool = RecyclerView.RecycledViewPool()
    private lateinit var ctx: Context

    class SingerDetailViewModel(binding: SingerRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val title = binding.titleSong
        val recyclerView = binding.listSongArtist
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingerDetailViewModel {
        ctx = parent.context
        return SingerDetailViewModel(
            SingerRecyclerItemBinding.inflate(
                LayoutInflater.from(ctx),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SingerDetailViewModel, position: Int) {
        holder.title.text = dataset[position].title
        val layoutManager = LinearLayoutManager(ctx)
        layoutManager.initialPrefetchItemCount = dataset[position].listSong.size
        val childAdapter = MusicAdapter(dataset[position].listSong)
        childAdapter.setOnItemClickListener(object : MusicAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {

            }
        })
        holder.recyclerView.layoutManager = layoutManager
        holder.recyclerView.adapter = childAdapter
        holder.recyclerView.setRecycledViewPool(viewPool)
    }

    override fun getItemCount(): Int = dataset.size
}