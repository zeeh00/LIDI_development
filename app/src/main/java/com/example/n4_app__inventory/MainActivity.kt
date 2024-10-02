package com.example.n4_app__inventory

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.example.n4_app__inventory.fragments.FirstScreenFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find the NavHostFragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        // Set an OnClickListener on the NavHostFragment's view
        navHostFragment.view?.setOnClickListener {
            // Check if the user is signed in
            if (FirebaseAuth.getInstance().currentUser != null) {
                // User is signed in, proceed to HomeActivity
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish() // Optionally finish this activity
            } else {
                // User is not signed in, redirect to sign-in or sign-up screen
                val intent = Intent(this, FirstScreenFragment::class.java) // Adjust to your sign-in activity
                startActivity(intent)
                finish() // Optionally finish this activity
            }
        }
    }
}
