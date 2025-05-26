package com.example.virtualwing.utils

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualwing.R
import com.example.virtualwing.data.Squadron
import com.example.virtualwing.data.UserProfile
import com.example.virtualwing.viewmodel.squadron.SquadronViewModel

class MemberListDialog(
    private val squadron: Squadron,
    private val currentUserProfile: UserProfile,
    private val viewModel: SquadronViewModel,
    private val onArrangeFlight: (UserProfile) -> Unit
) : DialogFragment() {

    private lateinit var adapter: MemberAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_member_list, null)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_members)

        adapter = MemberAdapter(currentUserProfile, viewModel, onArrangeFlight)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        viewModel.squadronMembers.observe(this) { members ->
            adapter.submitList(members)
        }

        // Load members when dialog opens
        viewModel.loadSquadronMembers()

        builder.setView(view)
            .setNegativeButton("Close") { dialog, _ -> dialog.dismiss() }

        return builder.create()
    }


}