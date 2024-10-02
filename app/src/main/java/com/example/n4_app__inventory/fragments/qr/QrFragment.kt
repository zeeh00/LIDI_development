package com.example.n4_app__inventory.fragments.qr

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.example.n4_app__inventory.R
import com.example.n4_app__inventory.databinding.FragmentQrBinding
import com.example.n4_app__inventory.fragments.animals.AnimalInfoFragment
import com.example.n4_app__inventory.fragments.form.data.Animal
import com.example.n4_app__inventory.fragments.profile.ProfileFragment
import com.google.firebase.firestore.FirebaseFirestore

class QrFragment : Fragment() {

    private lateinit var binding: FragmentQrBinding
    private lateinit var codeScanner: CodeScanner
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQrBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.imgProfile.setOnClickListener {
            val profileFragment = ProfileFragment()
            replaceFragment(profileFragment)
        }

        return view
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
        val activity = requireActivity()
        firestore = FirebaseFirestore.getInstance()
        codeScanner = CodeScanner(activity, scannerView)
        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread {
                val animalId = it.text // Extracted ID from QR code
                fetchAnimalData(animalId) // Call fetchAnimalData after initializing firestore
            }
        }
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun fetchAnimalData(animalId: String) {
        val animalRef = firestore.collection("animals").document(animalId)
        animalRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val animal = document.toObject(Animal::class.java)
                    if (animal != null) {
                        val animalInfoFragment = AnimalInfoFragment.newInstance(animal)
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.qr_scanner, animalInfoFragment)
                            ?.addToBackStack(null)
                            ?.commit()
                    } else {
                        Toast.makeText(requireContext(), "Animal data not found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Animal not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Failed to fetch animal data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}