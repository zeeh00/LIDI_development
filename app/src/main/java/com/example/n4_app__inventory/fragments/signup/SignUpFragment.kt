package com.example.n4_app__inventory.fragments.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.n4_app__inventory.R
import com.example.n4_app__inventory.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val view = binding.root

        // Example: Setting up AutoCompleteTextView
        val items = listOf("Admin")
        val autoComplete: AutoCompleteTextView = binding.root.findViewById(R.id.USR_ROLE)
        val adapter = ArrayAdapter(requireContext(), R.layout.list_role, items)

        autoComplete.setAdapter(adapter)

        autoComplete.setOnItemClickListener(AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val itemSelected = adapterView.getItemAtPosition(i)
            Toast.makeText(requireContext(), "Role: $itemSelected", Toast.LENGTH_SHORT).show()
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        registerEvent()
    }

    private fun init(view: View){
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()

        val toolbar: androidx.appcompat.widget.Toolbar = view.findViewById(R.id.toolbarToolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        val backButton: ImageButton = view.findViewById(R.id.btnArrowleft)
        backButton.setOnClickListener {
            // Handle the back button click
            findNavController().navigateUp()
        }
    }

    private fun registerEvent() {
        binding.txtAlrHaveAcc.setOnClickListener {
            navController.navigate(R.id.action_signUpFragment_to_signInFragment)
        }

        binding.btnSignUp.setOnClickListener {
            val username = binding.USRNAME.text.toString().trim()
            val fullname = binding.USRFULLNAME.text.toString().trim()
            val email = binding.USREMAIL.text.toString().trim()
            val role = binding.USRROLE.text.toString().trim()
            val phnum = binding.USRPHNNUM.text.toString().trim()
            val pass = binding.USRPSWRD.text.toString().trim()
            val verifyPass = binding.USRCONPSWRD.text.toString().trim()

            if (username.isNotEmpty() && email.isNotEmpty() && role.isNotEmpty()
                && pass.isNotEmpty() && verifyPass.isNotEmpty() && phnum.isNotEmpty()
                && fullname.isNotEmpty()) {
                if (pass == verifyPass) {
                    auth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Registered Successfully", Toast.LENGTH_SHORT).show()
                                navController.navigate(R.id.action_signUpFragment_to_homeActivity)
                            } else {
                                Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    // Passwords do not match, handle accordingly
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Handle empty fields
                Toast.makeText(context, "All fields must be filled", Toast.LENGTH_SHORT).show()
            }
        }
    }

}