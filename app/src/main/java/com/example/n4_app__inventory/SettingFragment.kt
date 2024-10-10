package com.example.n4_app__inventory

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import com.example.n4_app__inventory.fragments.profile.ProfileFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.firestore.FirebaseFirestore

class SettingFragment : Fragment() {

    private lateinit var usernameTextView: MaterialTextView
    private lateinit var userEmailTextView: MaterialTextView
    private lateinit var backButton: View
    private lateinit var btn_Account: View
    private lateinit var btn_Notif: View
    private lateinit var btn_Appearance: View
    private lateinit var btn_Privacy: View
    private lateinit var btn_Language: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Initialize views
        usernameTextView = view.findViewById(R.id.username)
        userEmailTextView = view.findViewById(R.id.user_email)
        backButton = view.findViewById(R.id.back_button)
        btn_Account = view.findViewById(R.id.account_group)
        btn_Notif = view.findViewById(R.id.notification_group)
        btn_Appearance = view.findViewById(R.id.appearance_group)
        btn_Privacy = view.findViewById(R.id.privacy_security_group)
        btn_Language = view.findViewById(R.id.language_group)

        // to Profile Fragment
        btn_Account.setOnClickListener {
            val profileFragment = ProfileFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, profileFragment) // Use your actual container ID
                .addToBackStack(null) // Allows going back to SettingFragment
                .commit()
        }

        // to Setting Info
        btn_Notif.setOnClickListener {
            val settingInfoFragment = SettingInfoFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer,settingInfoFragment)
                .addToBackStack(null)
                .commit()
        }
        btn_Appearance.setOnClickListener {
            val settingInfoFragment = SettingInfoFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer,settingInfoFragment)
                .addToBackStack(null)
                .commit()
        }
        btn_Privacy.setOnClickListener {
            val settingInfoFragment = SettingInfoFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer,settingInfoFragment)
                .addToBackStack(null)
                .commit()
        }
        btn_Language.setOnClickListener {
            val settingInfoFragment = SettingInfoFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer,settingInfoFragment)
                .addToBackStack(null)
                .commit()
        }

        // Set up back button listener
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // Display current user's info
        displayUserInfo()

        return view
    }

    private fun displayUserInfo() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            userEmailTextView.text = user.email ?: "Email not available"

            // Fetch username from Firestore (assuming it's stored there)
            val db = FirebaseFirestore.getInstance()
            val userDocRef = db.collection("users").document(user.uid)

            userDocRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val username = document.getString("username") ?: "Default Username"
                    usernameTextView.text = username
                } else {
                    usernameTextView.text = "User not found in Firestore"
                }
            }.addOnFailureListener { exception ->
                Log.w("SettingFragment", "Error getting user document", exception)
            }
        } else {
            usernameTextView.text = "User not signed in"
            userEmailTextView.text = "Email not available"
        }
    }
}
