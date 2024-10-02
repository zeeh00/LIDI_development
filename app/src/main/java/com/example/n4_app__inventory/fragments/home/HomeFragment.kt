package com.example.n4_app__inventory.fragments.home

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.n4_app__inventory.R
import com.example.n4_app__inventory.databinding.FragmentHomeBinding
import com.example.n4_app__inventory.fragments.inventory.InventoryFragment
import com.example.n4_app__inventory.fragments.profile.AboutFragment
import com.example.n4_app__inventory.fragments.profile.ProfileFragment
import com.example.n4_app__inventory.SettingFragment
import com.google.android.material.navigation.NavigationView

class HomeFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using View Binding
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        // Access DrawerLayout from the fragment's layout
        drawerLayout = binding.drawerSettings

        // Handle profile icon click to open/close the drawer
        binding.imgProfile.setOnClickListener {
            toggleDrawer()
        }

        // Initialize NavigationView and set the listener
        val navigationView = view.findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        // Handle button click to replace fragment
        binding.btnGettingStartedOne.setOnClickListener {
            val inventoryFragment = InventoryFragment()
            replaceFragment(inventoryFragment)
        }

        return view
    }

    // Toggle drawer open/close
    fun toggleDrawer() {
        if (drawerLayout.isDrawerOpen(Gravity.END)) {
            drawerLayout.closeDrawer(Gravity.END)
        } else {
            drawerLayout.openDrawer(Gravity.END)
        }
    }

    // Handle navigation item selections
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var selectedFragment: Fragment? = null

        // Check which menu item was selected
        when (item.itemId) {
            R.id.nav_profile -> selectedFragment = ProfileFragment()
            R.id.nav_about -> selectedFragment = AboutFragment()
            R.id.nav_setting -> selectedFragment = SettingFragment()
        }

        // Replace the fragment if one was selected
        selectedFragment?.let {
            replaceFragment(it)
            // Close the drawer after selection
            drawerLayout.closeDrawer(Gravity.END)
        }

        return true
    }

    // Method to replace fragment
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = requireActivity().supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment) // Ensure this matches your nav host ID
        transaction.addToBackStack(null)
        transaction.commit()
    }

    // Method to check if the drawer is open
    fun isDrawerOpen(): Boolean {
        return drawerLayout.isDrawerOpen(Gravity.END)
    }
}
