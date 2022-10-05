package com.rikkei.training.musicapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rikkei.training.musicapp.R
import com.rikkei.training.musicapp.databinding.ChartItemsBinding
import com.rikkei.training.musicapp.model.Song

class ChartAdapter(private val chartList: ArrayList<Song>):
    RecyclerView.Adapter<ChartAdapter.ChartViewModel>() {
    private lateinit var ctx: Context

    class ChartViewModel(binding: ChartItemsBinding): RecyclerView.ViewHolder(binding.root) {
        val positionSong = binding.txtPosition
        val imgSong = binding.imgSong
        val nameSong = binding.txtNameSong
        val nameArtist = binding.txtNameArtist
        var background = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChartViewModel {
        ctx = parent.context
        return ChartViewModel(ChartItemsBinding.inflate(LayoutInflater.from(ctx), parent, false))
    }

    @SuppressLint("UseCompatLoadingForDrawables", "ResourceAsColor")
    override fun onBindViewHolder(holder: ChartViewModel, position: Int) {
        holder.positionSong.text = chartList[position].thisId.toString()
        Glide.with(ctx)
            .load(chartList[position].imageUri)
            .centerCrop()
            .into(holder.imgSong)
        holder.nameSong.text = chartList[position].thisTile
        holder.nameArtist.text = chartList[position].thisArtist

        when (chartList[position].thisId.toInt()) {
            1 -> {
                holder.background.background = ctx.getDrawable(R.drawable.background_1st_chart)!!
                holder.positionSong.setTextColor(R.color.purple_700)
            }
            2 -> {
                holder.background.background = ctx.getDrawable(R.drawable.background_2nd_chart)!!
                holder.positionSong.setTextColor(R.color.yellow)
            }
            3 -> {
                holder.background.background = ctx.getDrawable(R.drawable.background_3rd_chart)!!
                holder.positionSong.setTextColor(R.color.red)
            }
        }
    }

    override fun getItemCount(): Int = chartList.size
}