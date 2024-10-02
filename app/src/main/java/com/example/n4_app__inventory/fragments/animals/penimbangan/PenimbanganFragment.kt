package com.example.n4_app__inventory.fragments.animals.penimbangan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.n4_app__inventory.R
import com.example.n4_app__inventory.databinding.FragmentPenimbanganBinding
import com.example.n4_app__inventory.fragments.animals.pakan.PakanFragment
import com.example.n4_app__inventory.fragments.form.data.Animal
import com.example.n4_app__inventory.fragments.profile.ProfileFragment
import com.example.n4_app__inventory.spinner.setupDatePicker
import com.google.firebase.firestore.FirebaseFirestore

class PenimbanganFragment : Fragment() {

    private lateinit var binding : FragmentPenimbanganBinding
    private var animal: Animal? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPenimbanganBinding.inflate(inflater, container, false)
        val view = binding.root

        animal?.let { setAnimalData(it) }

        handleClickBack()
        handleImgProfile()
        handleSave()

        binding.linearColumnSelectPenmDate.setupDatePicker(requireContext(), this, binding.txtSelectPenimbanganDate)

        return view
    }

    private fun handleClickBack(){
        binding.btnArrowleft.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun handleImgProfile(){
        binding.imgProfile.setOnClickListener {
            val profileFragment = ProfileFragment()
            replaceFragment(profileFragment)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun handleSave() {
        binding.btnSave.setOnClickListener {
            // Get the values from the UI
            val inputPenmDate = binding.txtSelectPenimbanganDate.text.toString().trim()
            val bbtAwal = binding.txtBbtAwal.text.toString().trim()
            val bbtPenm = binding.txtBbtPenimbangan.text.toString().trim()

            // Validate inputs
            if (inputPenmDate.isEmpty() || bbtAwal.isEmpty() || bbtPenm.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Call a method to send data to Firebase
            updateAnimalData(inputPenmDate, bbtAwal, bbtPenm)
        }
    }

    private fun updateAnimalData(inputPenmDate: String, bbtAwal: String, bbtPenm: String) {
        // Ensure Firestore is initialized
        val firestore = FirebaseFirestore.getInstance()

        animal?.let { animal ->
            // Create a map to hold the data you want to update
            val updates = hashMapOf<String, Any>(
                "inputPenmDate" to inputPenmDate,
                "bbtAwal" to bbtAwal,
                "bbtPenm" to bbtPenm
            )

            // Update the document in Firestore
            firestore.collection("animals").document(animal.id)
                .update(updates)
                .addOnSuccessListener {
                    // Handle success, e.g., show a Toast message
                    Toast.makeText(requireContext(), "Data updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    // Handle failure, e.g., show a Toast message
                    Toast.makeText(requireContext(), "Failed to update data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            // Handle the case where animalId is null
            Toast.makeText(requireContext(), "Animal data is missing", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        animal = arguments?.getParcelable(ARG_ANIMAL) // Retrieve the full Animal object
    }


    private fun setAnimalData(animal: Animal) {
        binding.txtSelectPenimbanganDate.text = animal.inputPenmDate
        binding.txtBbtAwal.setText(animal.bbtAwal)
        binding.txtBbtPenimbangan.setText(animal.bbtPenm)
    }

    companion object {
        private const val ARG_ANIMAL = "arg_animal" // Use this consistently

        fun newInstance(animal: Animal): PenimbanganFragment {
            val fragment = PenimbanganFragment()
            val args = Bundle()
            args.putParcelable(ARG_ANIMAL, animal) // Pass the full Animal object
            fragment.arguments = args
            return fragment
        }

    }


}