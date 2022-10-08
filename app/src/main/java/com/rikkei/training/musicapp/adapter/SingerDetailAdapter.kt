package com.rikkei.training.musicapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rikkei.training.musicapp.databinding.SingerRecyclerItemBinding
import com.rikkei.training.musicapp.model.SingerDetail

class SingerDetailAdapter :
    RecyclerView.Adapter<SingerDetailAdapter.SingerDetailViewModel>() {

    private var viewPool = RecyclerView.RecycledViewPool()
    private lateinit var ctx: Context
    var dataset= ArrayList<SingerDetail>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

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
        val layoutManagers = LinearLayoutManager(ctx)
        layoutManagers.initialPrefetchItemCount = dataset[position].listSong.size
        val childAdapter = MusicAdapter()
        childAdapter.dataset = dataset[position].listSong
        childAdapter.notifyDataSetChanged()
        childAdapter.setOnItemClickListener(object : MusicAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {

            }
        })
        holder.recyclerView.apply {
            layoutManager = layoutManagers
            holder.recyclerView.adapter = childAdapter
            holder.recyclerView.setRecycledViewPool(viewPool)
        }

    }

    override fun getItemCount(): Int = dataset.size
}