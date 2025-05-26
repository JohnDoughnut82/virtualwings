package com.example.virtualwing.ui.squadron

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.virtualwing.R
import com.example.virtualwing.data.Squadron

class SquadronDetailDialog(
    context: Context,
    private val squadron: Squadron,
    private val onJoinClicked: (Squadron) -> Unit
) : Dialog(context) {

    private lateinit var joinButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_squadron_detail)
        setCancelable(true)

        val nameTextView = findViewById<TextView>(R.id.tv_squadron_name_detailed)
        val descTextView = findViewById<TextView>(R.id.tv_squadron_description)
        val regionTextView = findViewById<TextView>(R.id.tv_squadron_region_detailed)
        val timezoneTextView = findViewById<TextView>(R.id.tv_squadron_timezone)
        val createdByTextView = findViewById<TextView>(R.id.tv_squadron_created_by)
        val memberCountTextView = findViewById<TextView>(R.id.tv_squadron_member_count)
        val emblemImageView = findViewById<ImageView>(R.id.iv_squadron_emblem_detailed)

        joinButton = findViewById(R.id.btn_request_join)

        nameTextView.text = squadron.name
        descTextView.text = squadron.description
        regionTextView.text = squadron.region
        timezoneTextView.text = squadron.timezone
        createdByTextView.text = squadron.createdBy
        memberCountTextView.text = context.getString(
            R.string.squadron_members, squadron.members.size)

        Glide.with(context)
            .load(squadron.emblemUrl)
            .placeholder(R.drawable.ic_placeholder_emblem)
            .into(emblemImageView)

        joinButton.setOnClickListener {
            joinButton.isEnabled = false
            onJoinClicked(squadron)
            dismiss()
        }
    }
}