package com.example.n4_app__inventory.fragments.qr

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
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
import com.google.firebase.firestore.FirebaseFirestore

class QrFragment : Fragment() {

    private lateinit var binding: FragmentQrBinding
    private lateinit var codeScanner: CodeScanner
    private lateinit var firestore: FirebaseFirestore
    private val CAMERA_REQUEST_CODE = 101
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQrBinding.inflate(inflater, container, false)
        val view = binding.root

        // Initialize ProgressBar
        progressBar = binding.root.findViewById(R.id.progressBar)

        checkCameraPermission() // Check camera permission on fragment view creation

        return view
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted; request it
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        } else {
            // Permission has already been granted; initialize the scanner
            startQrScanner()
        }
    }

    private fun startQrScanner() {
        val scannerView = binding.root.findViewById<CodeScannerView>(R.id.scanner_view)
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

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted; initialize the scanner
                startQrScanner()
            } else {
                // Permission denied
                Toast.makeText(requireContext(), "Camera permission is required to scan QR codes", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Camera permission is checked and scanner is initialized in checkCameraPermission()
    }

    private fun fetchAnimalData(animalId: String) {
        // Show progress bar when data is being fetched
        progressBar.visibility = View.VISIBLE

        val animalRef = firestore.collection("animals").document(animalId)
        animalRef.get()
            .addOnSuccessListener { document ->
                // Hide progress bar when data is successfully fetched
                progressBar.visibility = View.GONE

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
                // Hide progress bar if there's an error
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to fetch animal data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onResume() {
        super.onResume()
        if (::codeScanner.isInitialized) {
            codeScanner.startPreview()
        }
    }

    override fun onPause() {
        if (::codeScanner.isInitialized) {
            codeScanner.releaseResources()
        }
        super.onPause()
    }
}
