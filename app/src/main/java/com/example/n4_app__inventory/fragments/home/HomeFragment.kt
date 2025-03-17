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
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var drawerLayout: DrawerLayout
    private val db = FirebaseFirestore.getInstance() // Inisialisasi Firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        drawerLayout = binding.drawerSettings

        binding.imgProfile.setOnClickListener {
            toggleDrawer()
        }

        val navigationView = view.findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        binding.btnGettingStartedOne.setOnClickListener {
            val inventoryFragment = InventoryFragment()
            replaceFragment(inventoryFragment)
        }

        // Memanggil fungsi untuk menghitung jumlah hewan
        countAnimals()

        return view
    }

    // Fungsi untuk mengambil data dari Firestore dan menghitung jumlah hewan berdasarkan anmlType
    private fun countAnimals() {
        db.collection("animals")
            .get()
            .addOnSuccessListener { documents ->
                var countDomba = 0
                var countKambing = 0
                var countSapi = 0

                for (document in documents) {
                    val anmlType = document.getString("anmlType")
                    when (anmlType) {
                        "Domba" -> countDomba++
                        "Kambing" -> countKambing++
                        "Sapi" -> countSapi++
                    }
                }

                val total = countDomba + countKambing + countSapi

                // Menampilkan hasil di UI
                binding.txtInputInfoDomba.text = countDomba.toString()
                binding.txtInputInfoKambing.text = countKambing.toString()
                binding.txtInputInfoSapi.text = countSapi.toString()
                binding.txtInputInfoTotal.text = total.toString()
            }
            .addOnFailureListener { exception ->
                // Handle error jika gagal mengambil data
                binding.txtInputInfoTotal.text = "Error"
            }
    }

    fun toggleDrawer() {
        if (drawerLayout.isDrawerOpen(Gravity.END)) {
            drawerLayout.closeDrawer(Gravity.END)
        } else {
            drawerLayout.openDrawer(Gravity.END)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var selectedFragment: Fragment? = null

        when (item.itemId) {
            R.id.nav_profile -> selectedFragment = ProfileFragment()
            R.id.nav_about -> selectedFragment = AboutFragment()
            R.id.nav_setting -> selectedFragment = SettingFragment()
        }

        selectedFragment?.let {
            replaceFragment(it)
            drawerLayout.closeDrawer(Gravity.END)
        }

        return true
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = requireActivity().supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun isDrawerOpen(): Boolean {
        return drawerLayout.isDrawerOpen(Gravity.END)
    }
}
