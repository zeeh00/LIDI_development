package com.example.n4_app__inventory.fragments.signin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.n4_app__inventory.R
import com.example.n4_app__inventory.databinding.FragmentSignInBinding

import com.google.firebase.auth.FirebaseAuth

class SignInFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var binding: FragmentSignInBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        loginEvent()
    }

    private fun init(view: View){
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()

        val backButton: ImageButton = view.findViewById(R.id.btnArrowleft)
        backButton.setOnClickListener {
            // Handle the back button click
            findNavController().navigateUp()
        }
    }

    private fun loginEvent() {
        binding.dontHaveAcc.setOnClickListener {
            navController.navigate(R.id.action_signInFragment_to_signUpFragment)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.USREMAIL.text.toString().trim()
            val pass = binding.USRPSWRD.text.toString().trim()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Login Successfully", Toast.LENGTH_SHORT).show()
                            navController.navigate(R.id.action_signInFragment_to_homeActivity)
                        } else {
                            Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
            }else{
                // Handle empty fields
                Toast.makeText(context, "All fields must be filled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}