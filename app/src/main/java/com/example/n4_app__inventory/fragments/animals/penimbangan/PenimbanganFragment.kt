package com.example.n4_app__inventory.fragments.animals.penimbangan

import android.os.Bundle
import android.util.Log
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

    private fun updateAnimalData(inputPenmDate: String, bbtAwal: String, bbtPenm: String) {
        val firestore = FirebaseFirestore.getInstance()

        animal?.let { animal ->
            // Create a map to update general animal info
            val animalUpdates = hashMapOf<String, Any>(
                "inputPenmDate" to inputPenmDate,
                "bbtAwal" to bbtAwal,
                "bbtPenm" to bbtPenm
            )

            // Update the 'animals' collection with the general data
            firestore.collection("animals").document(animal.id)
                .update(animalUpdates)
                .addOnSuccessListener {
                    // Now handle saving to the 'penimbangan' collection
                    handlePenimbanganData(animal.id, inputPenmDate, bbtAwal, bbtPenm)

                    // Create an updated animal object
                    val updatedAnimal = animal.copy(
                        inputPenmDate = inputPenmDate,
                        bbtAwal = bbtAwal,
                        bbtPenm = bbtPenm
                    )

                    // Send updated animal data to other fragments (e.g., AnimalInfoFragment)
                    val animalResult = Bundle()
                    animalResult.putParcelable("updatedAnimal", updatedAnimal)
                    parentFragmentManager.setFragmentResult("animalUpdate", animalResult)

                    Toast.makeText(requireContext(), "Data updated successfully", Toast.LENGTH_SHORT).show()

                    // Pop back to the previous fragment
                    requireActivity().supportFragmentManager.popBackStack()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to update animal data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            Toast.makeText(requireContext(), "Animal data is missing", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleSave() {
        binding.btnSave.setOnClickListener {
            // Get the values from the UI
            val inputPenmDate = binding.txtSelectPenimbanganDate.text.toString()
            val bbtAwal = binding.txtBbtAwal.text.toString()
            val bbtPenm = binding.txtBbtPenimbangan.text.toString()


            // Validate inputs
            if (inputPenmDate.isEmpty() || bbtAwal.isEmpty() || bbtPenm.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Log the input values
            Log.d("PenimbanganFragment", "Input Values - bbtAwal: $bbtAwal, bbtPenm: $bbtPenm, inputPenmDate: $inputPenmDate")

            // Call a method to send data to Firebase
            updateAnimalData(inputPenmDate, bbtAwal, bbtPenm)
        }
    }

    private fun handlePenimbanganData(animalId: String, inputPenmDate: String, bbtAwal: String, bbtPenm: String) {
        val firestore = FirebaseFirestore.getInstance()
        val penimbanganDocRef = firestore.collection("penimbangan").document(animalId)

        penimbanganDocRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // Document exists, retrieve current values
                val currentInputPenmDate = document.get("inputPenmDate") as? ArrayList<String> ?: arrayListOf()
                val currentBbtAwal = document.get("bbtAwal") as? ArrayList<String> ?: arrayListOf()
                val currentBbtPenm = document.get("bbtPenm") as? ArrayList<String> ?: arrayListOf()

                // Log current values
                Log.d("PenimbanganData", "Current Values - inputPenmDate: $currentInputPenmDate, bbtAwal: $currentBbtAwal, bbtPenm: $currentBbtPenm")

                // Add new entries (ensure correct order)
                currentInputPenmDate.add(inputPenmDate) // Add date
                currentBbtAwal.add(bbtAwal) // Add initial weight
                currentBbtPenm.add(bbtPenm) // Add current weight

                // Log updated values
                Log.d("PenimbanganData", "Updated Values - inputPenmDate: $currentInputPenmDate, bbtAwal: $currentBbtAwal, bbtPenm: $currentBbtPenm")

                val updates = hashMapOf<String, Any>(
                    "inputPenmDate" to currentInputPenmDate,
                    "bbtAwal" to currentBbtAwal,
                    "bbtPenm" to currentBbtPenm,
                    "id" to animalId
                )

                penimbanganDocRef.update(updates)
                    .addOnSuccessListener {
                        // Update the animal fragment result
                        if (isAdded) {
                            val penimbanganResult = Bundle().apply {
                                putStringArrayList("inputPenmDate", currentInputPenmDate)
                                putStringArrayList("bbtAwal", currentBbtAwal)
                                putStringArrayList("bbtPenm", currentBbtPenm)
                            }
                            parentFragmentManager.setFragmentResult("penimbanganDataUpdated", penimbanganResult)
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Failed to update penimbangan data: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Document doesn't exist, create a new one with arrays
                val newPenimbanganData = hashMapOf(
                    "inputPenmDate" to arrayListOf(inputPenmDate),
                    "bbtAwal" to arrayListOf(bbtAwal),
                    "bbtPenm" to arrayListOf(bbtPenm),
                    "id" to animalId
                )

                penimbanganDocRef.set(newPenimbanganData)
                    .addOnSuccessListener {
                        if (isAdded) {
                            val penimbanganResult = Bundle().apply {
                                putStringArrayList("inputPenmDate", arrayListOf(inputPenmDate))
                                putStringArrayList("bbtAwal", arrayListOf(bbtAwal))
                                putStringArrayList("bbtPenm", arrayListOf(bbtPenm))
                            }
                            parentFragmentManager.setFragmentResult("penimbanganDataUpdated", penimbanganResult)
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Failed to create penimbangan data: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Failed to get penimbangan data: ${e.message}", Toast.LENGTH_SHORT).show()
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