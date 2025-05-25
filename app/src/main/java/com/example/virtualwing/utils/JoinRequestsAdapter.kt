package com.example.virtualwing.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualwing.R
import com.example.virtualwing.data.UserProfile

class JoinRequestsAdapter(
    private val requests: List<UserProfile>,
    private val onApprove: (UserProfile) -> Unit,
    private val onDeny: (UserProfile) -> Unit,
) : RecyclerView.Adapter<JoinRequestsAdapter.RequestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_join_request, parent, false)
        return RequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        holder.bind(requests[position])
    }

    override fun getItemCount(): Int = requests.size

    inner class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name = itemView.findViewById<TextView>(R.id.tv_name)
        private val hours = itemView.findViewById<TextView>(R.id.tv_flight_hours)
        private val approve = itemView.findViewById<Button>(R.id.btn_approve)
        private val deny = itemView.findViewById<Button>(R.id.btn_deny)

        fun bind(userProfile: UserProfile) {
            name.text = userProfile.name
            hours.text = "Flight Hours: ${userProfile.totalFlightHours}"
            approve.setOnClickListener { onApprove(userProfile) }
            deny.setOnClickListener { onDeny(userProfile) }
        }
    }
}