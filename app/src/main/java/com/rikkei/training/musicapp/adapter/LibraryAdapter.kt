package com.rikkei.training.musicapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rikkei.training.musicapp.databinding.LibraryCardItemBinding
import com.rikkei.training.musicapp.model.LibraryCard

class LibraryAdapter :
    RecyclerView.Adapter<LibraryAdapter.MyViewHolder>() {

    private lateinit var mListener: OnItemClickListener
    var dataSet= ArrayList<LibraryCard>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class MyViewHolder(binding: LibraryCardItemBinding, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(binding.root) {
        val imageView = binding.cardItemIcon
        val title = binding.cardItemTitle
        val number = binding.cardNumberItems

        init {
            binding.root.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LibraryCardItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), mListener
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.title.text = dataSet[position].nameCard
        if (dataSet[position].numberItems == 0) holder.number.visibility = View.GONE
        else holder.number.text = dataSet[position].numberItems.toString()
        holder.imageView.setImageResource(dataSet[position].iconId)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }


    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }
}