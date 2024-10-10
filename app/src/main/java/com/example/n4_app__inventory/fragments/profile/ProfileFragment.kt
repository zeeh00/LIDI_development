package com.example.n4_app__inventory.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.n4_app__inventory.R
import com.example.n4_app__inventory.databinding.FragmentProfileBinding
import com.example.n4_app__inventory.fragments.FirstScreenFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        // Retrieve and update user data
        retrieveAndUpdateUserData()

        // Handle logout button click
        binding.btnLogout.setOnClickListener {
            performBackgroundTask()
        }

        // Handle back arrow click
        binding.btnArrowleft.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        return view
    }

    private fun retrieveAndUpdateUserData() {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email

        if (userEmail != null) {
            val collectionRef = db.collection("users")

            collectionRef.whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val fullName = document.getString("fullname")
                        val phoneNumber = document.getString("phnum")
                        val role = document.getString("role")
                        val username = document.getString("username")

                        println("Retrieved Data: fullName=$fullName, phoneNumber=$phoneNumber, role=$role, username=$username")

                        binding.username.setText(username)
                        binding.userEmail.setText(userEmail)
                        binding.etFullname.setText(fullName)
                        binding.etPhoneNumber.setText(phoneNumber)
                        binding.etRole.setText(role)
                        binding.etUsername.setText(username)
                        binding.etEmail.setText(userEmail)
                    }
                }
                .addOnFailureListener { exception ->
                    println("Error getting documents: $exception")
                }
        } else {
            println("Current user email is null.")
        }
    }

    private fun performBackgroundTask() {
        FirebaseAuth.getInstance().signOut()

        // Show a message that the user has been logged out
        Toast.makeText(requireContext(), "You have been logged out", Toast.LENGTH_SHORT).show()

        // Redirect to FirstScreenFragment
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, FirstScreenFragment())  // Assuming 'fragmentContainer' is your main container
            .commit()

        // Optionally clear the back stack
        requireActivity().supportFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}
