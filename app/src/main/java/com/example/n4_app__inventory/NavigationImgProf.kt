package com.example.n4_app__inventory

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import android.view.Gravity
import android.widget.FrameLayout
import android.view.ViewGroup
import com.example.n4_app__inventory.fragments.home.HomeFragment

class NavigationImgProf : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create DrawerLayout
        val drawerLayout = DrawerLayout(this).apply {
            id = R.id.drawer_settings
            layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            )
            fitsSystemWindows = true
        }

        // Include HomeFragment (inflated programmatically)
        val homeFragmentContainer = FrameLayout(this).apply {
            id = R.id.homeFragment
            layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        drawerLayout.addView(homeFragmentContainer)

        // Create NavigationView
        val navigationView = NavigationView(this).apply {
            id = R.id.nav_view
            layoutParams = DrawerLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ).apply {
                gravity = Gravity.END
            }
            fitsSystemWindows = true
            setHeaderView(R.layout.nav_header)  // Header layout
            inflateMenu(R.menu.nav_menu)  // Menu layout
        }
        drawerLayout.addView(navigationView)

        // Set the DrawerLayout as the content view
        setContentView(drawerLayout)

        // Load the HomeFragment into the container
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.homeFragment, HomeFragment())
                    .commit()
        }
    }

    private fun setHeaderView(navHeader: Int) {
        // Find the NavigationView
        val navigationView = findViewById<NavigationView>(R.id.nav_view)

                // Inflate the header layout and set it to the NavigationView
                navigationView.inflateHeaderView(navHeader)
    }

}
