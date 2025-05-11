package com.example.virtualwing
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.virtualwing.utils.NavigationManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView

open class BaseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    protected lateinit var navigationManager: NavigationManager
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    private var isViewModified = false

    private fun hasUnsavedChanges(): Boolean {
        return isViewModified
    }

    open fun setViewModified(isModified: Boolean) {
        isViewModified = isModified
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigationManager = NavigationManager(this)
    }

    protected fun setUpDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)

        toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.openDrawer, R.string.closeDrawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: android.view.MenuItem): Boolean {
        if (hasUnsavedChanges()) {
            MaterialAlertDialogBuilder(this)
                .setTitle("Discard changes?")
                .setMessage("You have unsaved changes. Are you sure you want to leave without saving?")
                .setPositiveButton("OK") { _, _ ->
                    handleNavigation(item)
                }
                .setNegativeButton("Cancel", null)
                .show()
        } else {
            handleNavigation(item)
        }
        drawerLayout.closeDrawers()
        return true
    }

    private fun handleNavigation(item: android.view.MenuItem) {
        when (item.itemId) {
            R.id.nav_home -> navigationManager.navigateToHome()
            R.id.nav_profile -> navigationManager.navigateToProfile()
            R.id.nav_log -> navigationManager.navigateToFlightLog()
            R.id.nav_squad -> navigationManager.navigateToSquadron()
            R.id.nav_logout -> {
                navigationManager.logoutAndNavigateToLogin()
                finish()
            }
        }
    }

    override fun onBackPressed() {
        if (hasUnsavedChanges()) {
            MaterialAlertDialogBuilder(this)
                .setTitle("Discard changes?")
                .setMessage("You have unsaved changes. Are you sure you want to leave without saving?")
                .setPositiveButton("OK") { _, _ ->
                    super.onBackPressed()
                }
                .setNegativeButton("Cancel", null)
                .show()
        } else {
            super.onBackPressed()
        }
    }
}
