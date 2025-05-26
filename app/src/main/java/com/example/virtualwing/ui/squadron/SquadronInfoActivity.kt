package com.example.virtualwing.ui.squadron

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.example.virtualwing.BaseActivity
import com.example.virtualwing.R
import com.example.virtualwing.data.Squadron
import com.example.virtualwing.data.UserProfile
import com.example.virtualwing.utils.JoinRequestsDialog
import com.example.virtualwing.utils.MemberListDialog
import com.example.virtualwing.viewmodel.factoryProvider.ViewModelFactoryProvider
import com.example.virtualwing.viewmodel.squadron.SquadronViewModel
import com.google.firebase.auth.FirebaseAuth

class SquadronInfoActivity : BaseActivity() {

    private lateinit var emblemImageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var descTextView: TextView
    private lateinit var regionTextView: TextView
    private lateinit var timezoneTextView: TextView
    private lateinit var memberCountTextView: TextView

    private lateinit var leaveButton: Button
    private lateinit var disbandButton: Button
    private lateinit var viewJoinRequestsButton: Button

    private lateinit var badgeTextView: TextView

    private var isLoadingSquadron = true

    private val squadronViewModel: SquadronViewModel by viewModels {
        ViewModelFactoryProvider.provideSquadronViewModelFactory()
    }

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onResume() {
        super.onResume()
        currentUserId?.let { squadronViewModel.checkUserSquadron(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_squadron_info)
        setUpDrawer()

        emblemImageView = findViewById(R.id.iv_squadron_emblem)
        nameTextView = findViewById(R.id.tv_squadron_name)
        descTextView = findViewById(R.id.tv_squadron_description)
        regionTextView = findViewById(R.id.tv_squadron_region)
        timezoneTextView = findViewById(R.id.tv_squadron_timezone)
        memberCountTextView = findViewById(R.id.tv_squadron_member_count)

        leaveButton = findViewById(R.id.btn_leave_squadron)
        disbandButton = findViewById(R.id.btn_disband_squadron)
        viewJoinRequestsButton = findViewById(R.id.btn_view_join_requests)

        badgeTextView = findViewById(R.id.tv_request_badge)

        // Observe current squadron data
        squadronViewModel.userSquadron.observe(this) { squadron ->
            if (isLoadingSquadron) {
                isLoadingSquadron = false
                if (squadron != null) {
                    renderSquadronInfo(squadron)
                } else {
                    Toast.makeText(this, "No squadron found.", Toast.LENGTH_SHORT).show()
                    finish() // Close activity if no squadron
                }
            }
        }

        leaveButton.setOnClickListener {
            currentUserId?.let { userId ->
                squadronViewModel.userSquadron.value?.let { squad ->
                    squadronViewModel.leaveSquadron(userId, squad.id)
                }
            }
        }

        disbandButton.setOnClickListener {
            currentUserId?.let { userId ->
                squadronViewModel.userSquadron.value?.let { squad ->
                    squadronViewModel.disbandSquadron(userId, squad.id)
                }
            }
        }

        viewJoinRequestsButton.setOnClickListener {
            val squadron = squadronViewModel.userSquadron.value
            if (squadron != null && userIsAdminOrCreator(currentUserId, squadron)) {
                squadronViewModel.getPendingJoinRequests(squadron.id)

                squadronViewModel.joinRequests.observe(this) { requests ->
                    if (requests.isNotEmpty()) {
                        JoinRequestsDialog(this, requests,
                            onApprove = { user ->
                                squadronViewModel.approveJoinRequest(user.id, squadron.id)
                            },
                            onDeny = { user ->
                                squadronViewModel.denyJoinRequest(user.id, squadron.id)
                            }
                        ).show()
                    } else {
                        Toast.makeText(this, "No join requests found.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Show results of leave/disband operations
        squadronViewModel.leaveResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "You left the squadron.", Toast.LENGTH_SHORT).show()
                finish() // Close after leaving
            }.onFailure {
                Toast.makeText(this, "Failed to leave: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        squadronViewModel.disbandResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Squadron disbanded.", Toast.LENGTH_SHORT).show()
                finish() // Close after disband
            }.onFailure {
                Toast.makeText(this, "Failed to disband: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        currentUserId?.let { squadronViewModel.checkUserSquadron(it) }

        currentUserId?.let { squadronViewModel.fetchUserProfile(it) }

        squadronViewModel.joinRequests.observe(this) { requests ->
            updateJoinRequestCount(requests.size)
        }

        memberCountTextView.setOnClickListener {
            val squadron = squadronViewModel.userSquadron.value ?: return@setOnClickListener
            val currentUser = squadronViewModel.userProfile.value

            if (currentUser != null) {
                val dialog =
                    MemberListDialog(squadron, currentUser, squadronViewModel) { memberToFlyWith ->
                        // Handle "fly with member" action
                    }
                dialog.show(supportFragmentManager, "MemberListDialog")
            } else {
                Toast.makeText(this, "User profile not loaded.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateJoinRequestCount(count: Int) {
        badgeTextView.text = count.toString()
        badgeTextView.visibility = if (count > 0) View.VISIBLE else View.GONE
    }

    @SuppressLint("StringFormatMatches")
    private fun renderSquadronInfo(squadron: Squadron) {
        nameTextView.text = squadron.name
        descTextView.text = squadron.description
        regionTextView.text = squadron.region
        timezoneTextView.text = squadron.timezone
        memberCountTextView.text = getString(R.string.squadron_members, squadron.members.size)

        Glide.with(this)
            .load(squadron.emblemUrl)
            .placeholder(R.drawable.ic_placeholder_emblem)
            .into(emblemImageView)

        val isCreator = currentUserId == squadron.creatorId
        leaveButton.visibility = if (isCreator) View.GONE else View.VISIBLE
        disbandButton.visibility = if (isCreator) View.VISIBLE else View.GONE
        viewJoinRequestsButton.visibility = if (isCreator) View.VISIBLE else View.GONE
        squadronViewModel.getPendingJoinRequests(squadron.id)
    }

    private fun userIsAdminOrCreator(userId: String?, squadron: Squadron): Boolean {
        val currentUser = squadronViewModel.userProfile.value
        return if (currentUser != null) {
            userId == squadron.creatorId || currentUser.isSquadronAdmin
        } else {
            false
        }
    }
}
