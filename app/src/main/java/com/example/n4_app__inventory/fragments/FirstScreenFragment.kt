package com.example.n4_app__inventory.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.n4_app__inventory.HomeActivity
import com.example.n4_app__inventory.R
import com.example.n4_app__inventory.databinding.FragmentFirstScreenBinding
import com.google.firebase.auth.FirebaseAuth

class FirstScreenFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentFirstScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFirstScreenBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        setUpClicks()
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
    }

    private fun setUpClicks() {
        binding.btnSignUp.setOnClickListener {
            navController.navigate(R.id.action_firstScreenFragment_to_signUpFragment)
        }

        binding.txtAlrHaveAcc.setOnClickListener {
            // Check if the user is already authenticated (logged in)
            if (FirebaseAuth.getInstance().currentUser != null) {
                // If the user is logged in, navigate to HomeActivity
                val intent = Intent(requireContext(), HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                activity?.finish() // Optional, closes the current activity so the user can't go back to it
            } else {
                // If the user is not logged in, navigate to SignInFragment
                navController.navigate(R.id.action_firstScreenFragment_to_signInFragment)
            }
        }
    }
}
