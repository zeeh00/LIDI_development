package com.example.n4_app__inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.n4_app__inventory.fragments.form.data.Animal
import com.google.firebase.firestore.FirebaseFirestore

class StatusHewanFragment : Fragment() {

    private lateinit var btnArrowleft: ImageButton
    private lateinit var spinnerInputStatusHewan: Spinner
    private lateinit var txtStatusNow: TextView
    private lateinit var btnSave: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var firestore: FirebaseFirestore
    private var animalId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_status_hewan, container, false)

        // Get animalId from arguments
        val animal = arguments?.getParcelable<Animal>(ARG_ANIMAL)
        if (animal == null) {
            Toast.makeText(requireContext(), "Animal data not provided!", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressed()
            return view
        }
        animalId = animal.id  // Make sure Animal class has an `id` field

        btnArrowleft = view.findViewById(R.id.btnArrowleft)
        spinnerInputStatusHewan = view.findViewById(R.id.spinnerInputStatusHewan)
        txtStatusNow = view.findViewById(R.id.txtStatusNow)
        btnSave = view.findViewById(R.id.btnSave)
        progressBar = view.findViewById(R.id.progressBar)

        firestore = FirebaseFirestore.getInstance()

        val statusOptions = listOf("Hidup", "Mati", "Terjual", "Dihibahkan", "Dihadiahkan")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statusOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerInputStatusHewan.adapter = adapter

        loadCurrentStatus()

        btnArrowleft.setOnClickListener {
            requireActivity().onBackPressed()
        }

        btnSave.setOnClickListener {
            val selectedStatus = spinnerInputStatusHewan.selectedItem.toString()
            saveStatus(selectedStatus)
        }

        return view
    }

    private fun loadCurrentStatus() {
        progressBar.visibility = View.VISIBLE
        firestore.collection("animals").document(animalId)
            .get()
            .addOnSuccessListener { document ->
                progressBar.visibility = View.GONE
                if (document != null && document.exists()) {
                    val currentStatus = document.getString("kondisiTernak") ?: "Unknown"
                    txtStatusNow.text = currentStatus

                    val index = (spinnerInputStatusHewan.adapter as ArrayAdapter<String>).getPosition(currentStatus)
                    if (index >= 0) {
                        spinnerInputStatusHewan.setSelection(index)
                    }
                } else {
                    txtStatusNow.text = "No data"
                }
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Error loading status: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveStatus(newStatus: String) {
        progressBar.visibility = View.VISIBLE
        firestore.collection("animals").document(animalId)
            .update("kondisiTernak", newStatus)
            .addOnSuccessListener {
                progressBar.visibility = View.GONE
                txtStatusNow.text = newStatus
                Toast.makeText(
                    requireContext(),
                    "Status updated to $newStatus. Pastikan untuk mengisi detail informasi pada Kejadian Khusus",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to update status: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val ARG_ANIMAL = "arg_animal"

        fun newInstance(animal: Animal): StatusHewanFragment {
            val fragment = StatusHewanFragment()
            val args = Bundle()
            args.putParcelable(ARG_ANIMAL, animal)
            fragment.arguments = args
            return fragment
        }
    }
}
