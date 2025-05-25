package com.example.virtualwing.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.virtualwing.R
import com.example.virtualwing.data.Squadron


class SquadronAdapter(
    private val squadronList: List<Squadron>,
    private val onItemClick: (Squadron) -> Unit
) : RecyclerView.Adapter<SquadronAdapter.SquadronViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SquadronViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_squadron, parent, false)
        return SquadronViewHolder(view)
    }

    override fun onBindViewHolder(holder: SquadronViewHolder, position: Int) {
        val squadron = squadronList[position]
        holder.bind(squadron)
        holder.itemView.setOnClickListener { onItemClick(squadron) }
    }

    override fun getItemCount(): Int = squadronList.size

    class SquadronViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.tv_squadron_name)
        private val region: TextView = itemView.findViewById(R.id.tv_squadron_region)

        fun bind(squadron: Squadron) {
            name.text = squadron.name
            region.text = squadron.region

            Glide.with(itemView.context)
                .load(squadron.emblemUrl)
                .placeholder(R.drawable.ic_placeholder_emblem)
                .into(itemView.findViewById(R.id.iv_squadron_emblem))
        }
    }
}