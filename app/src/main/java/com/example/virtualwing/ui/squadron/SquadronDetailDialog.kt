package com.example.virtualwing.utils

import android.app.Dialog
import android.content.Context
import android.widget.Button
import com.example.virtualwing.data.Squadron

class SquadronDetailDialog(
    context: Context,
    private val squadron: Squadron,
    private val onJoinClicked: (Squadron) -> Unit
) : Dialog(context) {

    private lateinit var joinButton: Button

    
}