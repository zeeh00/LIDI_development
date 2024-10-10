package com.example.n4_app__inventory.fragments.animals.harga

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.n4_app__inventory.R
import com.example.n4_app__inventory.fragments.form.data.Animal
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.text.NumberFormat
import java.util.*

class HargaFragment : Fragment() {

    private lateinit var etFieldOne: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var animal: Animal
    private var current = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_harga, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UI components
        etFieldOne = view.findViewById(R.id.txtHargaJual)
        progressBar = view.findViewById(R.id.progressBar)

        // Retrieve the passed Animal object
        animal = arguments?.getParcelable(ARG_ANIMAL) ?: return

        // Fetch current price from Firestore and display it
        fetchCurrentPrice()

        // Set up automatic "Rp." formatting
        etFieldOne.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != current) {
                    etFieldOne.removeTextChangedListener(this)

                    // Remove "Rp." and periods to work with raw numbers
                    val cleanString = s.toString().replace("Rp.", "").replace(".", "").trim()

                    if (cleanString.isNotEmpty()) {
                        val parsed = cleanString.toLongOrNull() ?: 0L
                        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                        format.maximumFractionDigits = 0
                        val formatted = format.format(parsed).replace("Rp", "Rp.")

                        current = formatted
                        etFieldOne.setText(formatted)
                        etFieldOne.setSelection(formatted.length) // Move cursor to the end
                    } else {
                        current = ""
                    }

                    etFieldOne.addTextChangedListener(this)
                }
            }
        })

        // Set up button click listener for saving harga
        view.findViewById<View>(R.id.btnSave).setOnClickListener {
            saveHarga()
        }

        // Set up button click listener for back button
        view.findViewById<View>(R.id.btnArrowleft).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack() // Navigate back
        }
    }

    private fun fetchCurrentPrice() {
        // Show progress bar while fetching data
        progressBar.visibility = View.VISIBLE

        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("harga").document(animal.id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val hargaJual = document.getString("hargaJual")
                    if (!hargaJual.isNullOrEmpty()) {
                        // Set the existing price in the EditText
                        val formattedPrice = "Rp." + NumberFormat.getInstance(Locale("id", "ID"))
                            .format(hargaJual.toLong()).replace("Rp", "Rp.")
                        etFieldOne.setText(formattedPrice)
                    }
                } else {
                    Toast.makeText(requireContext(), "Harga not found!", Toast.LENGTH_SHORT).show()
                }
                progressBar.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error fetching harga: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveHarga() {
        val inputPrice = etFieldOne.text.toString().replace("Rp.", "").replace(".", "").trim()

        // Validate input
        if (inputPrice.isNotBlank()) {
            progressBar.visibility = View.VISIBLE

            // Prepare the data to be saved
            val hargaData = hashMapOf(
                "animalId" to animal.id,  // Assuming the Animal class has an id property
                "hargaJual" to inputPrice
            )

            // Save data to Firestore
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("harga")
                .document(animal.id)  // Use the animal ID to store data uniquely
                .set(hargaData, SetOptions.merge()) // Merging to update existing document or create new
                .addOnSuccessListener {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Harga saved successfully!", Toast.LENGTH_SHORT).show()
                    etFieldOne.text.clear() // Clear the input field after saving
                }
                .addOnFailureListener { e ->
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error saving harga: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Show an error message to the user
            Toast.makeText(requireContext(), "Please enter a valid price", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val ARG_ANIMAL = "arg_animal"

        fun newInstance(animal: Animal): HargaFragment {
            val fragment = HargaFragment()
            val args = Bundle()
            args.putParcelable(ARG_ANIMAL, animal)
            fragment.arguments = args
            return fragment
        }
    }
}
