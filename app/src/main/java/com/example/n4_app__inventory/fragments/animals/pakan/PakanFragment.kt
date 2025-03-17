package com.example.n4_app__inventory.fragments.animals.pakan

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import com.example.n4_app__inventory.databinding.FragmentPakanBinding
import com.example.n4_app__inventory.fragments.form.data.Animal
import com.example.n4_app__inventory.spinner.setupDatePicker
import com.google.firebase.firestore.FirebaseFirestore

class PakanFragment : Fragment() {

    private lateinit var binding: FragmentPakanBinding
    private var animal: Animal? = null
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPakanBinding.inflate(inflater, container, false)
        progressBar = binding.progressBar // Add this line in onCreateView

        animal?.let { setAnimalData(it) }

        handleClickBack()
        handleSave()
        setupClearTextView()

        binding.linearColumnSelectInputDate.setupDatePicker(requireContext(), this, binding.txtSelectInputDate)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        animal = arguments?.getParcelable(ARG_ANIMAL)
    }

    private fun handleClickBack() {
        binding.btnArrowleft.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun handleSave() {
        binding.btnSave.setOnClickListener {
            // Get the values from the UI
            val inputDate = binding.txtSelectInputDate.text.toString().trim()
            val konsumsiPakan = binding.txtKonsumsiPkn.text.toString().trim()

            // Validate fields before proceeding
            if (!isValidInput(inputDate, konsumsiPakan)) {
                Toast.makeText(requireContext(), "Please fill in all fields correctly", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE // Show progress bar only after validation passes

            // Update animal data and pakan data separately
            updateAnimalData(inputDate, konsumsiPakan)
        }
    }

    private fun isValidInput(inputDate: String, konsumsiPakan: String): Boolean {
        // Check if inputDate is empty
        if (inputDate.isEmpty()) {
            binding.txtSelectInputDate.error = "Date is required"
            return false
        }

        // Check if konsumsiPakan is empty
        if (konsumsiPakan.isEmpty()) {
            binding.txtKonsumsiPkn.error = "Konsumsi Pakan is required"
            return false
        }

        // Check if konsumsiPakan is a valid number
        val pakanValue = konsumsiPakan.toFloatOrNull()
        if (pakanValue == null || pakanValue <= 0) {
            binding.txtKonsumsiPkn.error = "Please enter a valid amount for Konsumsi Pakan"
            return false
        }

        return true // Validation passed
    }

    private fun setupClearTextView() {
        binding.txtClearAll.setOnClickListener {
            clearAllInputs() // Clear all input fields
        }
    }

    private fun clearAllInputs() {
        binding.txtSelectInputDate.text = "" // Clear the date input
        binding.txtKonsumsiPkn.text.clear() // Clear the consumption input
    }

    private fun updateAnimalData(inputDate: String, konsumsiPakan: String) {
        val firestore = FirebaseFirestore.getInstance()

        animal?.let { animal ->
            val updates = hashMapOf<String, Any>(
                "inputDate" to inputDate,
                "konsumsiPakan" to konsumsiPakan
            )

            firestore.collection("animals").document(animal.id)
                .update(updates)
                .addOnSuccessListener {
                    // Animal data updated successfully, now handle pakan data
                    handlePakanData(animal.id, konsumsiPakan, inputDate)

                    // Create an updated animal object
                    val updatedAnimal = animal.copy(
                        inputDate = inputDate,
                        konsumsiPakan = konsumsiPakan
                    )

                    // Send updated animal data to other fragments
                    val animalResult = Bundle()
                    animalResult.putParcelable("updatedAnimal", updatedAnimal)
                    parentFragmentManager.setFragmentResult("animalUpdate", animalResult)

                    progressBar.visibility = View.INVISIBLE
                    Toast.makeText(requireContext(), "Data updated successfully", Toast.LENGTH_SHORT).show()

                    // Pop back to the previous fragment
                    requireActivity().supportFragmentManager.popBackStack()
                }
                .addOnFailureListener { e ->
                    progressBar.visibility = View.INVISIBLE
                    Toast.makeText(requireContext(), "Failed to update data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun handlePakanData(animalId: String, konsumsiPakan: String, inputDate: String) {
        val firestore = FirebaseFirestore.getInstance()
        val pakanDocRef = firestore.collection("pakan").document(animalId)

        pakanDocRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // Document exists, update weights and dates
                val weightsList = document.get("weights") as? MutableList<String> ?: mutableListOf()
                val datesList = document.get("dates") as? MutableList<String> ?: mutableListOf()

                // Add new weight and date
                weightsList.add(konsumsiPakan)
                datesList.add(inputDate)

                // Update the document with new lists
                val updates = hashMapOf<String, Any>(
                    "weights" to weightsList,
                    "dates" to datesList,
                    "id" to animalId
                )

                pakanDocRef.update(updates)
                    .addOnSuccessListener {
                        // Ensure fragment is attached before sending fragment result
                        if (isAdded) {
                            val pakanResult = Bundle().apply {
                                putString("konsumsiPakan", konsumsiPakan)
                                putString("inputDate", inputDate)
                            }
                            parentFragmentManager.setFragmentResult("pakanDataUpdated", pakanResult)
                        } else {
                            Log.e("PakanFragment", "Fragment is not attached to the activity.")
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Failed to update pakan data: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Document doesn't exist, create a new one
                val newPakanData = hashMapOf(
                    "weights" to listOf(konsumsiPakan),
                    "dates" to listOf(inputDate),
                    "id" to animalId
                )

                pakanDocRef.set(newPakanData)
                    .addOnSuccessListener {
                        // Ensure fragment is attached before sending fragment result
                        if (isAdded) {
                            val pakanResult = Bundle().apply {
                                putString("konsumsiPakan", konsumsiPakan)
                                putString("inputDate", inputDate)
                            }
                            parentFragmentManager.setFragmentResult("pakanDataUpdated", pakanResult)
                        } else {
                            Log.e("PakanFragment", "Fragment is not attached to the activity.")
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Failed to create pakan data: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Failed to get pakan data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setAnimalData(animal: Animal) {
        binding.txtSelectInputDate.text = animal.inputDate
        binding.txtKonsumsiPkn.setText(animal.konsumsiPakan)
    }

    companion object {
        private const val ARG_ANIMAL = "arg_animal"

        fun newInstance(animal: Animal): PakanFragment {
            val fragment = PakanFragment()
            val args = Bundle()
            args.putParcelable(ARG_ANIMAL, animal)
            fragment.arguments = args
            return fragment
        }
    }
}
