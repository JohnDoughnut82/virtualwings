package com.example.virtualwing.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualwing.R
import com.example.virtualwing.data.UserProfile
import com.example.virtualwing.viewmodel.squadron.SquadronViewModel

class MemberAdapter(
    private val currentUser: UserProfile,
    private val viewModel: SquadronViewModel,
    private val onArrangeFlight: (UserProfile) -> Unit
) : ListAdapter<UserProfile, MemberAdapter.MemberViewHolder>(DiffCallBack()) {

    inner class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tv_member_name)
        private val btnMakeAdmin: Button = itemView.findViewById(R.id.btn_make_admin)
        private val btnRemove: Button = itemView.findViewById(R.id.btn_remove)
        private val btnArrangeFlight: Button = itemView.findViewById(R.id.btn_arrange_flight)

        fun bind(member: UserProfile) {
            tvName.text = member.name

            val squadronId = currentUser.squadronId

            btnMakeAdmin.setOnClickListener {
                if (squadronId != null) {
                    viewModel.promoteToAdmin(member.id, squadronId)
                }
            }

            btnRemove.setOnClickListener {
                if (squadronId != null) {
                    viewModel.removeMember(member.id, squadronId)
                }
            }

            btnArrangeFlight.setOnClickListener {
                onArrangeFlight(member)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_member, parent, false)
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallBack : DiffUtil.ItemCallback<UserProfile>() {
        override fun areItemsTheSame(oldItem: UserProfile, newItem: UserProfile) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: UserProfile, newItem: UserProfile) = oldItem == newItem
    }
}