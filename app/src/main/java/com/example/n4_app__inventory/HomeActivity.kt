package com.example.n4_app__inventory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.n4_app__inventory.fragments.form.FormFragment
import com.example.n4_app__inventory.fragments.home.HomeFragment
import com.example.n4_app__inventory.fragments.qr.QrFragment
import com.example.n4_app__inventory.fragments.FirstScreenFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private var backPressedTime: Long = 0 // To track the time of the last back press
    private val backPressedInterval: Long = 2000 // 2 seconds interval for back press notification

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationBar)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)

        // Check if the user is authenticated
        if (FirebaseAuth.getInstance().currentUser != null) {
            // User is signed in, load the default fragment (HomeFragment)
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, HomeFragment())
                    .commit()
            }
        } else {
            // User is not signed in, navigate to the FirstScreenFragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, FirstScreenFragment())
                .commit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var selectedFragment: Fragment? = null

        when (item.itemId) {
            R.id.navigation_home -> selectedFragment = HomeFragment()
            R.id.navigation_form -> selectedFragment = FormFragment()
            R.id.navigation_qr -> selectedFragment = QrFragment()
        }

        selectedFragment?.let {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, it)
                .addToBackStack(null) // Add to back stack for proper back navigation
                .commit()
        }

        return true
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)

        if (fragment is FormFragment || fragment is QrFragment) {
            // If the current fragment is either FormFragment or QrFragment, go back to HomeFragment
            supportFragmentManager.popBackStack()
        } else if (fragment is HomeFragment) {
            // If currently in HomeFragment, check if the drawer is open
            val homeFragment = fragment as HomeFragment
            if (homeFragment.isDrawerOpen()) {
                // Close the drawer if it is open
                homeFragment.toggleDrawer()
            } else {
                // If drawer is closed, check back press count
                if (backPressedTime + backPressedInterval > System.currentTimeMillis()) {
                    // Clear the back stack to prevent returning to the splash screen
                    finishAffinity() // Close the app completely
                    return
                } else {
                    // Notify the user to press back again to exit
                    Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
                }
                backPressedTime = System.currentTimeMillis() // Update the back press time
            }
        } else if (fragment is FirstScreenFragment) {
            // If currently on FirstScreenFragment, navigate back to exit
            super.onBackPressed()
        } else {
            // Otherwise, call super to handle default back press behavior
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Perform any necessary cleanup here
        // e.g., clearing resources or references
    }
}
