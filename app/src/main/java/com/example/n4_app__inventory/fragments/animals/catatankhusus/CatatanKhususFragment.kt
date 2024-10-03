package com.example.n4_app__inventory.fragments.animals.catatankhusus

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.n4_app__inventory.R
import com.example.n4_app__inventory.databinding.FragmentCatatanKhususBinding
import com.example.n4_app__inventory.databinding.InputCatatanKhususBinding
import com.example.n4_app__inventory.fragments.form.data.Animal
import com.example.n4_app__inventory.spinner.CAMERA_PERMISSION_REQUEST
import com.example.n4_app__inventory.spinner.PICK_IMAGE_REQUEST
import com.example.n4_app__inventory.spinner.handleImagePickerResult
import com.example.n4_app__inventory.spinner.openImagePicker
import com.example.n4_app__inventory.spinner.setupActionSpinner
import com.example.n4_app__inventory.spinner.setupDatePicker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException
import android.Manifest
import android.icu.text.SimpleDateFormat
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import com.google.type.Date
import java.util.Locale

class CatatanKhususFragment : Fragment() {

    private lateinit var binding: FragmentCatatanKhususBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var inputBinding: InputCatatanKhususBinding
    private var selectedImageUri: Uri? = null
    private var animalId: String? = null
    private val CAMERA_PERMISSION_REQUEST = 100
    private val CAMERA_REQUEST_CODE = 100


    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST)
        } else {
            // Permission has already been granted
            openImagePicker(this)
        }
    }

    private fun openImagePicker() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment's layout and bind the views
        binding = FragmentCatatanKhususBinding.inflate(inflater, container, false)

        // Get the animalId from arguments
        val animal: Animal? = arguments?.getParcelable(ARG_ANIMAL)
        animalId = animal?.id // Assign the animalId from the passed Animal object

        // Get the spinner and frameLayout from the inflated layout
        val formTypeSpinner = binding.spinnerFormTypeKK
        val frameLayout = binding.containerFragmentKK

        // Setup the spinner using a helper function and define what happens when a selection is made
        formTypeSpinner.setupActionSpinner(requireContext(), resources.getStringArray(R.array.form_typesKK), frameLayout) { selectedFormType ->
            handleFormTypeSelection(selectedFormType, frameLayout)
        }

        firestore = FirebaseFirestore.getInstance()

        // Handle the back button click
        handleClickBack()

        return binding.root
    }

    private fun handleFormTypeSelection(formType: String, frameLayout: FrameLayout) {
        // Clear any existing views from the FrameLayout
        frameLayout.removeAllViews()
        when (formType) {
            "Input" -> configureInputData(frameLayout)
            "View" -> configureViewData(frameLayout)
            else -> configureDefaultForm(frameLayout)
        }
    }

    private fun configureInputData(frameLayout: FrameLayout) {
        val inputView = layoutInflater.inflate(R.layout.input_catatan_khusus, frameLayout, false)
        inputBinding = InputCatatanKhususBinding.bind(inputView) // Initialize the binding

        // Set up the date picker
        inputBinding.linearColumnSelectDate.setupDatePicker(requireContext(), this, inputBinding.txtSelectDateKejadian)

        // Set up the image picker
        inputBinding.columnImgAnimal.setOnClickListener {
            checkCameraPermission() // Open the image picker
        }

        // Add the ProgressBar
        val progressBar = inputBinding.progressBar // Assuming you added this in your binding class
        progressBar.visibility = View.GONE // Initially hide the ProgressBar

        // Set the click listener for the submit button
        inputBinding.btnSubmit.setOnClickListener {
            progressBar.visibility = View.VISIBLE // Show ProgressBar
            saveDataToFirestore(inputBinding, progressBar) // Pass the binding and ProgressBar
        }

        // Add the input view to the FrameLayout
        frameLayout.addView(inputView)
    }

    private fun saveDataToFirestore(inputBinding: InputCatatanKhususBinding, progressBar: ProgressBar) {
        val selectedDate = inputBinding.txtSelectDateKejadian.text.toString()
        val kejadianKhusus = inputBinding.txtIsiKejadianKhusus.text.toString()
        val catatanKhusus = inputBinding.txtIsiCatatanKhusus.text.toString()
        val animalId = this.animalId // Use the class property

        if (animalId != null) {
            val imageUri = selectedImageUri // Get the selected image URI directly

            if (imageUri != null) {
                val storageReference = FirebaseStorage.getInstance()
                    .getReference("kejadian/${animalId}_${System.currentTimeMillis()}.jpg")

                // Upload the image to Firebase Storage
                storageReference.putFile(imageUri)
                    .addOnSuccessListener { taskSnapshot ->
                        taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                            val imageUrl = uri.toString()
                            Log.d("ImageUrl", "Image uploaded successfully. URL: $imageUrl")

                            // Create the kejadian object with the imageUrl and animalId
                            val kejadian = mapOf(
                                "tanggal" to selectedDate,
                                "kejadianKhusus" to kejadianKhusus,
                                "catatanKhusus" to catatanKhusus,
                                "imageUrlKejadian" to imageUrl
                            )

                            // Save the new entry to Firestore, appending it to the array
                            firestore.collection("kejadian")
                                .document(animalId) // Use animalId as the document ID
                                .update("kejadianEntries", FieldValue.arrayUnion(kejadian))
                                .addOnSuccessListener {
                                    Log.d("Firestore", "Data appended successfully")
                                    Toast.makeText(requireContext(), "Data saved successfully!", Toast.LENGTH_SHORT).show()
                                    progressBar.visibility = View.GONE // Hide ProgressBar on success
                                    clearInputFields(inputBinding) // Optionally clear the input fields
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Firestore", "Error saving data", e)
                                    // Handle the case where the document doesn't exist
                                    if (e.message?.contains("No document") == true) {
                                        // Create a new document with an array
                                        firestore.collection("kejadian")
                                            .document(animalId)
                                            .set(mapOf("animalId" to animalId, "kejadianEntries" to listOf(kejadian)))
                                            .addOnSuccessListener {
                                                Toast.makeText(requireContext(), "Document created and data saved!", Toast.LENGTH_SHORT).show()
                                                progressBar.visibility = View.GONE // Hide ProgressBar on success
                                                clearInputFields(inputBinding)
                                            }
                                    } else {
                                        Toast.makeText(requireContext(), "Error saving data: ${e.message}", Toast.LENGTH_SHORT).show()
                                        progressBar.visibility = View.GONE // Hide ProgressBar on failure
                                    }
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("ImageUpload", "Failed to upload image", e)
                        Toast.makeText(requireContext(), "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
                        progressBar.visibility = View.GONE // Hide ProgressBar on failure
                    }
            } else {
                Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE // Hide ProgressBar if no image
            }
        } else {
            Toast.makeText(requireContext(), "Animal ID is null", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE // Hide ProgressBar if animalId is null
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("ImagePicker", "onActivityResult called")
        // Call the handleImagePickerResult function with the result data
        handleImagePickerResult(requestCode, resultCode, data, inputBinding.imageView)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data // Store the selected image URI
            inputBinding.imageView.setImageURI(selectedImageUri) // Display the selected image
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker()
            } else {
                Toast.makeText(requireContext(), "Camera permission is required to take pictures", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configureViewData(frameLayout: FrameLayout) {
        // Inflate the view_catatan_khusus layout and add it to the FrameLayout
        val inputView = layoutInflater.inflate(R.layout.view_catatan_khusus, frameLayout, false)

        // Find the RecyclerView in the inflated view
        val recyclerView = inputView.findViewById<RecyclerView>(R.id.recyclerListpathtwo)

        val animalId = this.animalId // Use the animalId from your fragment

        if (animalId != null) {
            // Fetch the document from Firestore
            firestore.collection("kejadian").document(animalId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Retrieve the array of kejadian entries
                        val kejadianList = document.get("kejadianEntries") as? List<Map<String, Any>>

                        if (kejadianList != null) {
                            // Convert the Firestore maps to a list of KejadianEntry objects
                            val entries = kejadianList.map {
                                Kejadian(
                                    tanggal = it["tanggal"] as? String ?: "",
                                    kejadianKhusus = it["kejadianKhusus"] as? String ?: "",
                                    catatanKhusus = it["catatanKhusus"] as? String ?: "",
                                    imageUrlKejadian = it["imageUrlKejadian"] as? String ?: "",
                                    animalId = animalId // Use the current animalId
                                )
                            }

                            // Initialize the RecyclerView adapter with the list of entries
                            val adapter = KejadianAdapter(entries)
                            recyclerView.adapter = adapter
                            recyclerView.layoutManager = LinearLayoutManager(context)
                        }
                    } else {
                        Log.d("Firestore", "Document does not exist.")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error fetching document", e)
                }
        } else {
            Log.e("Firestore", "Animal ID is null.")
        }

        // Add the view to the FrameLayout
        frameLayout.addView(inputView)
    }

    class KejadianAdapter(private val kejadianList: List<Kejadian>) :
        RecyclerView.Adapter<KejadianAdapter.KejadianViewHolder>() {

        inner class KejadianViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val txtDateKejadian: TextView = itemView.findViewById(R.id.txtDateKejadian)
            val txtKejadian: TextView = itemView.findViewById(R.id.txtKejadian)
            val txtCatatan: TextView = itemView.findViewById(R.id.txtCatatan)
            val imgKejadian: ImageView = itemView.findViewById(R.id.imgKejadian)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KejadianViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_catatan_khusus, parent, false)
            return KejadianViewHolder(view)
        }

        override fun onBindViewHolder(holder: KejadianViewHolder, position: Int) {
            val kejadianEntry = kejadianList[position]
            holder.txtDateKejadian.text = kejadianEntry.tanggal
            holder.txtKejadian.text = kejadianEntry.kejadianKhusus
            holder.txtCatatan.text = kejadianEntry.catatanKhusus

            // Load the image using Glide or any other image loading library
            Glide.with(holder.itemView.context)
                .load(kejadianEntry.imageUrlKejadian)
                .into(holder.imgKejadian)
        }

        override fun getItemCount(): Int {
            return kejadianList.size
        }
    }


    private fun configureDefaultForm(frameLayout: FrameLayout) {
        // Inflate the "view_catatan_khusus" layout and add it to the FrameLayout
        val defaultView = layoutInflater.inflate(R.layout.view_catatan_khusus, frameLayout, false)
        frameLayout.addView(defaultView)
    }

    private fun handleClickBack() {
        // Set the back button functionality
        binding.btnArrowleft.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun clearInputFields(inputBinding: InputCatatanKhususBinding) {
        // Clear the input fields
        inputBinding.txtSelectDateKejadian.text = null
        inputBinding.txtIsiKejadianKhusus.text = null
        inputBinding.txtIsiCatatanKhusus.text = null
        inputBinding.imageView.setImageDrawable(null)
        inputBinding.imageView.visibility = View.GONE
        selectedImageUri = null // Clear the selected image URI
    }


    companion object {
        private const val ARG_ANIMAL = "arg_animal"

        fun newInstance(animal: Animal): CatatanKhususFragment {
            val fragment = CatatanKhususFragment()
            val args = Bundle()
            args.putParcelable(ARG_ANIMAL, animal)
            fragment.arguments = args
            return fragment
        }
    }
}