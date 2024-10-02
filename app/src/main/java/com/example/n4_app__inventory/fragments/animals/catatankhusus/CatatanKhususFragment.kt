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

class CatatanKhususFragment : Fragment() {

    private lateinit var binding: FragmentCatatanKhususBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var inputBinding: InputCatatanKhususBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment's layout and bind the views
        binding = FragmentCatatanKhususBinding.inflate(inflater, container, false)

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
        inputBinding.linearColumnSelectDate.setupDatePicker(requireContext(), this, inputBinding.txtSelectDate)

        // Set up the image picker
        inputBinding.columnImgAnimal.setOnClickListener {
            openImagePicker(this) // Open the image picker
        }

        // Set the click listener for the submit button
        inputBinding.btnSubmit.setOnClickListener {
            saveDataToFirestore(inputBinding) // Pass the binding to saveDataToFirestore
        }

        // Add the input view to the FrameLayout
        frameLayout.addView(inputView)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("ImagePicker", "onActivityResult called")
        // Call the handleImagePickerResult function with the result data
        handleImagePickerResult(requestCode, resultCode, data, inputBinding.imageView)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open the image picker
                openImagePicker(this)
            } else {
                // Permission denied, handle accordingly (e.g., show a message to the user)
                // You may want to inform the user about the importance of the camera permission
                Log.e(TAG, "Camera permission denied")
            }
        }
    }

    private fun configureViewData(frameLayout: FrameLayout) {
        // Inflate the "view_catatan_khusus" layout (assuming you mean to display the same layout) and add it
        val viewData = layoutInflater.inflate(R.layout.view_catatan_khusus, frameLayout, false)
        frameLayout.addView(viewData)
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

    private fun saveDataToFirestore(inputBinding: InputCatatanKhususBinding) {
        val selectedDate = inputBinding.txtSelectDate.text.toString()
        val kejadianKhusus = inputBinding.txtIsiKejadianKhusus.text.toString()
        val catatanKhusus = inputBinding.txtIsiCatatanKhusus.text.toString()

        // Get the image URI from the ImageView tag or other means (if available)
        val imageUri = inputBinding.imageView.tag as? Uri

        if (imageUri != null) {
            // Upload the image to Firebase Storage
            val storageReference = FirebaseStorage.getInstance().getReference("kejadian/${System.currentTimeMillis()}_kejadian.jpg")

            storageReference.putFile(imageUri)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        Log.d("ImageUrl", "Image uploaded successfully. URL: $imageUrl")

                        // Create the kejadian object with the imageUrl
                        val kejadian = Kejadian(
                            tanggal = selectedDate,
                            kejadianKhusus = kejadianKhusus,
                            catatanKhusus = catatanKhusus,
                            imageUrl = imageUrl // Save the image URL
                        )

                        // Save to Firestore
                        firestore.collection("kejadian")
                            .add(kejadian)
                            .addOnSuccessListener { documentReference ->
                                Log.d("Firestore", "Document added with ID: ${documentReference.id}")
                                Toast.makeText(requireContext(), "Data saved: ${documentReference.id}", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Error saving data", e)
                                Toast.makeText(requireContext(), "Error saving data: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("ImageUpload", "Failed to upload image", e)
                    Toast.makeText(requireContext(), "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
                }

        } else {
            // Handle the case where no image was selected (optional)
            Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
        }
    }


    private fun clearInputFields(inputBinding: InputCatatanKhususBinding) {
        // Clear the input fields
        inputBinding.txtSelectDate.text = null
        inputBinding.txtIsiKejadianKhusus.text = null
        inputBinding.txtIsiCatatanKhusus.text = null
        inputBinding.imageView.setImageDrawable(null)
        inputBinding.imageView.visibility = View.GONE
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
