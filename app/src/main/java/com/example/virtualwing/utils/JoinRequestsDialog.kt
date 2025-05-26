package com.example.virtualwing.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.compose.animation.core.animateDpAsState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualwing.R
import com.example.virtualwing.data.UserProfile

class JoinRequestsDialog (
    context: Context,
    requests: List<UserProfile>,
    private val onApprove: (UserProfile) -> Unit,
    private val onDeny: (UserProfile) -> Unit
) : Dialog(context) {

    private val mutableRequests = requests.toMutableList()
    private lateinit var adapter: JoinRequestsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_join_requests)

        val recyclerView = findViewById<RecyclerView>(R.id.rv_join_requests)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = JoinRequestsAdapter(mutableRequests,
            onApprove = { user ->
                onApprove(user)
                adapter.removeUser(user)

                if (mutableRequests.isEmpty()) dismiss()
            },
            onDeny = { user ->
                onDeny(user)
                adapter.removeUser(user)

                if (mutableRequests.isEmpty()) dismiss()
            })
        recyclerView.adapter = adapter
    }
}