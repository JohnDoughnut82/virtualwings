package com.example.virtualwing.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualwing.R
import com.example.virtualwing.data.UserProfile

class JoinRequestsDialog (
    context: Context,
    private val requests: List<UserProfile>,
    private val onApprove: (UserProfile) -> Unit,
    private val onDeny: (UserProfile) -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_join_requests)

        val recyclerView = findViewById<RecyclerView>(R.id.rv_join_requests)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = JoinRequestsAdapter(requests, onApprove, onDeny)
    }
}