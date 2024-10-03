package com.example.n4_app__inventory.fragments.form

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.example.n4_app__inventory.R
import com.example.n4_app__inventory.databinding.FragmentFormBinding
import com.example.n4_app__inventory.fragments.profile.ProfileFragment
import com.example.n4_app__inventory.fragments.reactions.ReactionFailedFragment
import com.example.n4_app__inventory.fragments.reactions.ReactionSuccessFragment
import com.example.n4_app__inventory.fragments.reactions.ReactionSuccessUpdateFragment
import com.example.n4_app__inventory.spinner.CAMERA_PERMISSION_REQUEST
import com.example.n4_app__inventory.spinner.handleImagePickerResult
import com.example.n4_app__inventory.spinner.openImagePicker
import com.example.n4_app__inventory.spinner.setupActionSpinner
import com.example.n4_app__inventory.spinner.setupAnimalBreedSpinner
import com.example.n4_app__inventory.spinner.setupAnimalPhysStat
import com.example.n4_app__inventory.spinner.setupAnimalTypeSpinner
import com.example.n4_app__inventory.spinner.setupAnmlPhysStatSpinner
import com.example.n4_app__inventory.spinner.setupAnmlTypeSpinner
import com.example.n4_app__inventory.spinner.setupDatePicker

import com.example.n4_app__inventory.spinner.setupImagePickerWithPermissionCheck
import com.example.n4_app__inventory.spinner.setupLocTypeSpinner
import com.example.n4_app__inventory.spinner.setupLocationTypeSpinner
import com.example.n4_app__inventory.spinner.setupMarriageTypeSpinner
import com.example.n4_app__inventory.spinner.setupOriginTypeSpinner
import com.example.n4_app__inventory.spinner.setupSexTypeSpinner
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.ByteArrayOutputStream
import java.text.NumberFormat
import java.util.Locale

class FormFragment : Fragment() {

    private lateinit var imageView: ImageView
    private var imageUri: Uri? = null
    private lateinit var progressBar: ProgressBar
    private lateinit var binding:FragmentFormBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFormBinding.inflate(inflater, container, false)
        val view = binding.root

        val formTypeSpinner = view.findViewById<Spinner>(R.id.spinnerFormType)
        val frameLayout = view.findViewById<FrameLayout>(R.id.containerFragment)

        formTypeSpinner.setupActionSpinner(requireContext(), resources.getStringArray(R.array.form_types), frameLayout) { selectedFormType ->
            handleFormTypeSelection(selectedFormType, frameLayout)
        }

        binding.imgProfile.setOnClickListener {
            val profileFragment = ProfileFragment()
            replaceFragment(profileFragment)
        }

        return view
    }

    private fun handleFormTypeSelection(formType: String, frameLayout: FrameLayout){
        // Remove any existing views from the FrameLayout
        frameLayout.removeAllViews()
        when (formType) {
            "Input" -> configureInputForm(frameLayout)
            "Update" -> configureUpdateForm(frameLayout, isEditable = true)
            "Delete" -> configureDeleteForm(frameLayout)
            else -> configureDefaultForm(frameLayout)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun configureInputForm(view: View) {

        val frameLayout = view.findViewById<FrameLayout>(R.id.containerFragment)

        // Inflate your input form layout
        val inputFormLayout = layoutInflater.inflate(R.layout.input_form_layout, frameLayout, false)

        // Reset hasError for each validation cycle
        var hasError = false


        val anmlType = inputFormLayout.findViewById<Spinner>(R.id.spinnerAnmlType)
        anmlType.setupAnmlTypeSpinner(inputFormLayout) { selectedAnimalType ->

        }

        val originType = inputFormLayout.findViewById<Spinner>(R.id.spinnerOriginType)
        originType.setupOriginTypeSpinner(inputFormLayout) { selectedOriginType ->

        }

        val locationType = inputFormLayout.findViewById<Spinner>(R.id.spinnerLoc)
        locationType.setupLocTypeSpinner(inputFormLayout) { selectedLoc ->

        }

        val anmlSexType = inputFormLayout.findViewById<Spinner>(R.id.spinnerAnmlSexType)
        anmlSexType.setupSexTypeSpinner(inputFormLayout) { selectedAnmlSexType ->

        }

        val anmlMarriageType = inputFormLayout.findViewById<Spinner>(R.id.spinnerAnmlMarrStat)
        anmlMarriageType.setupMarriageTypeSpinner(inputFormLayout) { selectedAnmlMarriageType ->

        }

        val anmlBreedType = inputFormLayout.findViewById<Spinner>(R.id.spinnerAnmlBreed)
        anmlBreedType.setupAnimalBreedSpinner(inputFormLayout) { selectedAnmlBreedType ->

        }

        val anmlPhysStat = inputFormLayout.findViewById<Spinner>(R.id.spinnerAnmlPhysStat)
        anmlPhysStat.setupAnimalPhysStat(inputFormLayout) { selectedAnmlPhysStat ->

        }

        // Access UI elements in the input form layout
        imageView = inputFormLayout.findViewById(R.id.imageView)
        val imageAnimal = inputFormLayout.findViewById<LinearLayout>(R.id.columnImgAnimal)
        val txtRace = inputFormLayout.findViewById<EditText>(R.id.txtRaceColumn)
        val txtAnmlNum = inputFormLayout.findViewById<EditText>(R.id.txtAnmlNumColumn)
        val btnSubmit = inputFormLayout.findViewById<Button>(R.id.btnSubmit)
        val txtSelectDate = inputFormLayout.findViewById<TextView>(R.id.txtSelectDate)
        val txtSelectPurchaseDate = inputFormLayout.findViewById<TextView>(R.id.txtSelectPurchaseDate)
        val linearColumnSelectDate = inputFormLayout.findViewById<LinearLayout>(R.id.linearColumnSelectDate)
        val linearColumnSelectPurchaseDate = inputFormLayout.findViewById<LinearLayout>(R.id.linearColumnSelectPurchaseDate)
        val txtAnmlPrice = inputFormLayout.findViewById<EditText>(R.id.txtAnmlPriceColumn)
        val txtAnmlNumIndukan = inputFormLayout.findViewById<EditText>(R.id.txtAnmlNumIndukanColumn)
        val scrollView = inputFormLayout.findViewById<ScrollView>(R.id.scrollViewForm)

        val txtAnmlTypeError = inputFormLayout.findViewById<TextView>(R.id.txtAnmlTypeError)
        val txtAnmlOriginError = inputFormLayout.findViewById<TextView>(R.id.txtAnmlOriginError)
        val txtLocationError = inputFormLayout.findViewById<TextView>(R.id.txtAnmlLocError)
        val txtAnmlSexError = inputFormLayout.findViewById<TextView>(R.id.txtAnmlSexError)
        val txtAnmlMarriageError = inputFormLayout.findViewById<TextView>(R.id.txtAnmlMarriageError)
        val txtAnmlBreedError = inputFormLayout.findViewById<TextView>(R.id.txtAnmlBreedError)
        val txtAnmlRaceError = inputFormLayout.findViewById<TextView>(R.id.txtAnmlRaceError)
        val txtAnmlNumError = inputFormLayout.findViewById<TextView>(R.id.txtAnmlNumError)
        val txtAnmlPhysStatError = inputFormLayout.findViewById<TextView>(R.id.txtAnmlPhysStatError)

        progressBar = inputFormLayout.findViewById(R.id.progressBar)

        txtAnmlPrice.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                if (isUpdating) return

                val input = editable.toString()

                // Stop further updates temporarily to avoid recursive calls
                isUpdating = true

                // Remove "Rp." and any non-numeric characters except dot
                val numericInput = input.replace("[Rp.,]".toRegex(), "").trim()

                if (numericInput.isEmpty()) {
                    // Set default when field is empty
                    txtAnmlPrice.setText("")
                    isUpdating = false
                    return
                }

                try {
                    // Parse the numeric input as a long for formatting
                    val parsed = numericInput.toLong()

                    // Format the number with dot separator using Indonesian locale
                    val localeID = Locale("in", "ID")
                    val formatter = NumberFormat.getNumberInstance(localeID)
                    val formattedNumber = formatter.format(parsed)

                    // Combine with "Rp." prefix
                    val formattedInput = "Rp. $formattedNumber"

                    // Update the EditText with formatted text
                    txtAnmlPrice.setText(formattedInput)
                    txtAnmlPrice.setSelection(formattedInput.length) // Move cursor to the end

                } catch (e: NumberFormatException) {
                    // Handle invalid input
                    txtAnmlPrice.setText(input)  // Leave as-is if input is invalid
                }

                // Re-enable updates
                isUpdating = false
            }
        })

        btnSubmit.setOnClickListener {

            val selectedAnmlType = anmlType.selectedItem.toString().trim()
            val selectedOrigin = originType.selectedItem.toString().trim()
            val selectedLocation = locationType.selectedItem.toString().trim()
            val selectedDate = txtSelectDate.text.toString().trim()
            val selectedSex = anmlSexType.selectedItem.toString().trim()
            val selectedMarriage = anmlMarriageType.selectedItem.toString().trim()
            val selectedPurchaseDate = txtSelectPurchaseDate.text.toString().trim()
            val selectedAnmlBreedType = anmlBreedType.selectedItem.toString().trim()
            val inputtedRace = txtRace.text.toString().trim()
            val inputtedAnmlNum = txtAnmlNum.text.toString().trim()
            val inpuutedAnmlNumIndukan = txtAnmlNumIndukan.text.toString().trim()
            val selectedAnmlPhysStat = anmlPhysStat.selectedItem.toString().trim()
            val inputtedAnmlPrice = txtAnmlPrice.text.toString().replace("Rp.", "").replace(".", "").trim()


            // Reset errors before new validation cycle
            resetErrorMessages(txtAnmlTypeError, txtAnmlOriginError, txtLocationError, txtAnmlSexError, txtAnmlMarriageError, txtAnmlBreedError, txtAnmlRaceError, txtAnmlNumError)

            hasError = false

            // Validate the form fields one by one
            if (selectedAnmlType.isEmpty() || selectedAnmlType == "Select Type") {
                txtAnmlTypeError.visibility = View.VISIBLE
                hasError = true
            }

            if (selectedOrigin.isEmpty() || selectedOrigin == "Select Type") {
                txtAnmlOriginError.visibility = View.VISIBLE
                hasError = true
            }

            if (selectedLocation.isEmpty() || selectedLocation == "Select Type") {
                txtLocationError.visibility = View.VISIBLE
                hasError = true
            }

            if (selectedSex.isEmpty() || selectedSex == "Select Type") {
                txtAnmlSexError.visibility = View.VISIBLE
                hasError = true
            }

            if (inputtedRace.isEmpty()) {
                txtAnmlRaceError.visibility = View.VISIBLE
                hasError = true
            }

            if (selectedMarriage.isEmpty() || selectedMarriage == "Select Type") {
                txtAnmlMarriageError.visibility = View.VISIBLE
                hasError = true
            }

            if (inputtedAnmlNum.isEmpty()) {
                txtAnmlNumError.visibility = View.VISIBLE
                hasError = true
            }

            if (selectedAnmlBreedType.isEmpty() || selectedAnmlBreedType == "Select Type") {
                txtAnmlBreedError.visibility = View.VISIBLE
                hasError = true
            }

            if (selectedAnmlPhysStat.isEmpty() || selectedAnmlPhysStat == "Select Type") {
                txtAnmlPhysStatError.visibility = View.VISIBLE
                hasError = true
            }

            if (hasError) {
                Toast.makeText(requireContext(), "Mohon isi data terlebih dahulu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = FirebaseFirestore.getInstance()

            progressBar.visibility = View.VISIBLE

            // Check if the anmlNum already exists in Firestore
            db.collection("animals")
                .whereEqualTo("anmlNum", inputtedAnmlNum)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documents = task.result
                        if (documents != null && !documents.isEmpty) {
                            // The anmlNum already exists, show an error message
                            progressBar.visibility = View.GONE
                            Toast.makeText(requireContext(), "Nomor Hewan sudah digunakan.", Toast.LENGTH_SHORT).show()
                            return@addOnCompleteListener
                        }
                        // Function to upload image to Firebase Storage
                        fun uploadImageToStorage(imageBitmap: Bitmap?, customDocumentName: String) {
                            if (imageBitmap != null) {
                                val storage = FirebaseStorage.getInstance()
                                val storageRef = storage.reference
                                val imageRef = storageRef.child("animal/$customDocumentName.jpg")

                                val byteArrayOutputStream = ByteArrayOutputStream()
                                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                                val data = byteArrayOutputStream.toByteArray()

                                val uploadTask = imageRef.putBytes(data)

                                val qrCodeContents = "${customDocumentName}"
                                val barcodeEncoder = BarcodeEncoder()
                                val qrCodeBitmap = barcodeEncoder.encodeBitmap(qrCodeContents, BarcodeFormat.QR_CODE, 300, 300)

                                val qrCodeTask = uploadQrCodeToStorage(qrCodeBitmap, customDocumentName)

                                qrCodeTask.addOnCompleteListener { qrCodeTask ->
                                    if (qrCodeTask.isSuccessful) {
                                        val qrCodeDownloadUrl = qrCodeTask.result.toString()

                                        uploadTask.continueWithTask { task ->
                                            // Continue with the task to get the download URL
                                            if (!task.isSuccessful) {
                                                task.exception?.let {
                                                    throw it
                                                }
                                            }
                                            imageRef.downloadUrl
                                        }.addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                val downloadUrl = task.result.toString()

                                                val customDocumentName = generateUniqueId(selectedAnmlType, selectedLocation, inputtedAnmlNum)

                                                // Save form data to Cloud Firestore with the custom document name
                                                val formData = hashMapOf<String, Any>().apply {
                                                    put("id", customDocumentName)
                                                    put("anmlType", selectedAnmlType)
                                                    put("anmlNum", inputtedAnmlNum)
                                                    put("origin", selectedOrigin)
                                                    put("location", selectedLocation)
                                                    put("birthDate", selectedDate)
                                                    put("sex", selectedSex)
                                                    put("race", inputtedRace)
                                                    put("purchaseDate", selectedPurchaseDate)
                                                    put("marriageStatus", selectedMarriage)
                                                    put("breedType", selectedAnmlBreedType)
                                                    put("imageUrl", downloadUrl)
                                                    put("qrcodePath", qrCodeDownloadUrl)
                                                    put("anmlPrice", inputtedAnmlPrice)
                                                    put("anmlPhysStat", selectedAnmlPhysStat)
                                                    put("anmlNumIndukan", inpuutedAnmlNumIndukan)
                                                }

                                                db.collection("animals")
                                                    .document(customDocumentName)
                                                    .set(formData)
                                                    .addOnSuccessListener {
                                                        // Document added successfully

                                                        progressBar.visibility = View.GONE
                                                        Toast.makeText(requireContext(), "Data Submitted!", Toast.LENGTH_SHORT).show()

                                                        val generatedId = customDocumentName

                                                        val transaction = parentFragmentManager.beginTransaction()
                                                        val successFragment = ReactionSuccessFragment.newInstance(generatedId)
                                                        successFragment.show(transaction, "reactionSuccessFragment")

                                                        anmlType.setSelection(0)
                                                        anmlBreedType.setSelection(0)
                                                        originType.setSelection(0)
                                                        locationType.setSelection(0)
                                                        txtSelectDate.text = null
                                                        anmlSexType.setSelection(0)
                                                        txtRace.text = null
                                                        txtAnmlNum.text = null
                                                        txtSelectPurchaseDate.text = null
                                                        anmlMarriageType.setSelection(0)
                                                        txtAnmlPrice.text = null
                                                        anmlPhysStat.setSelection(0)
                                                        txtAnmlNumIndukan.text = null
                                                        imageView.setImageDrawable(null)
                                                        imageView.visibility = View.GONE
                                                        scrollView.fullScroll(ScrollView.FOCUS_UP)

                                                    }
                                                    .addOnFailureListener { exception ->
                                                        // Handle errors during Firestore document creation
                                                        progressBar.visibility = View.GONE
                                                        Toast.makeText(requireContext(), "Firestore document creation failed: ${exception.message}", Toast.LENGTH_SHORT).show()

                                                        val transaction = parentFragmentManager.beginTransaction()
                                                        val failedFragment = ReactionFailedFragment.newInstance()
                                                        failedFragment.show(transaction, "reactionFailedFragment")
                                                    }

                                            } else {
                                                // Handle the error when obtaining the download URL
                                                Toast.makeText(requireContext(), "Error getting download URL", Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                    } else {
                                        // Handle the error when obtaining the QR code download URL
                                        Toast.makeText(requireContext(), "Error getting QR code download URL", Toast.LENGTH_SHORT).show()
                                    }
                                }

                            } else {
                                // Handle the case when the camera image bitmap is null
                                Toast.makeText(requireContext(), "Error processing camera-captured image", Toast.LENGTH_SHORT).show()
                            }
                        }

                        // Check if an image is selected
                        if (imageUri != null) {
                            // Image selected from the gallery
                            val customDocumentName = generateUniqueId(selectedAnmlType, selectedLocation, inputtedAnmlNum)
                            // Upload image to Firebase Storage
                            uploadImageToStorage(imageBitmap = null, customDocumentName = customDocumentName)
                        } else {
                            // Camera-captured image
                            val cameraImageBitmap: Bitmap? = imageView.drawable?.toBitmap()
                            if (cameraImageBitmap != null) {
                                val customDocumentName = generateUniqueId(selectedAnmlType, selectedLocation, inputtedAnmlNum)
                                uploadImageToStorage(imageBitmap = cameraImageBitmap, customDocumentName = customDocumentName)
                            } else {
                                // Handle the case when the camera image bitmap is null
                                Toast.makeText(requireContext(), "Error processing camera-captured image", Toast.LENGTH_SHORT).show()
                            }
                        }

                    } else {
                        // Handle the error when checking for anmlNum uniqueness
                        Toast.makeText(requireContext(), "Error checking anmlNum uniqueness", Toast.LENGTH_SHORT).show()
                    }
                }

        }
        // Add the input form layout to the frame layout
        frameLayout.addView(inputFormLayout)
        // Set up image picker on the LinearLayout
        imageAnimal.setupImagePickerWithPermissionCheck(this@FormFragment)
        // Set up the date picker for the linearColumnSelectDate using the shared function
        linearColumnSelectDate.setupDatePicker(requireContext(),
            this, txtSelectDate)
        linearColumnSelectPurchaseDate.setupDatePicker(requireContext(),
            this, txtSelectPurchaseDate)
    }

    private fun generateUniqueId(selectedAnmlType: String, selectedLocation: String, anmlNum: String): String {
        return "N4-$selectedAnmlType-$selectedLocation-$anmlNum"
    }

    private fun resetErrorMessages(vararg textViews: TextView) {
        textViews.forEach { it.visibility = View.GONE }
    }

    private fun uploadQrCodeToStorage(qrCodeBitmap: Bitmap, documentName: String): Task<Uri> {
        val storage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.reference
        val qrCodeRef: StorageReference = storageRef.child("qrcode/$documentName.jpg")

        val byteArrayOutputStream = ByteArrayOutputStream()
        qrCodeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()

        val uploadTask = qrCodeRef.putBytes(data)

        return uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            qrCodeRef.downloadUrl
        }
    }

    private fun configureDeleteForm(view: View) {

        val frameLayout = view.findViewById<FrameLayout>(R.id.containerFragment)

        val deleteFormLayout = layoutInflater.inflate(R.layout.delete_form_layout, frameLayout, false)

        val autoCompleteTextView = deleteFormLayout.findViewById<AutoCompleteTextView>(R.id.actvFieldOne)

        val db = FirebaseFirestore.getInstance()
        val animalsCollectionRef = db.collection("animals")

        animalsCollectionRef.get().addOnSuccessListener { querySnapshot ->
            val animalIds = mutableListOf<String>()

            for (document in querySnapshot) {
                val animalId = document.getString("id")
                animalId?.let {
                    animalIds.add(it)
                }
            }

            val adapter = ArrayAdapter(autoCompleteTextView.context, R.layout.custom_spinner, animalIds)
            autoCompleteTextView.setAdapter(adapter)

            autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
                val selectedItem = adapter.getItem(position)?.toString()

                if (!selectedItem.isNullOrEmpty()) {
                    // Pass isEditable=false to indicate delete form
                    inflateNewLayout(view, selectedItem, isEditable = false)
                    hideKeyboard(autoCompleteTextView)
                }
            }

        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error getting documents: ", exception)
        }
        frameLayout.addView(deleteFormLayout)
    }

    private fun configureUpdateForm(view: View, isEditable: Boolean) {
        val frameLayout = view.findViewById<FrameLayout>(R.id.containerFragment)
        val updateFormLayout = layoutInflater.inflate(R.layout.update_form_layout, frameLayout, false)

        val autoCompleteTextView = updateFormLayout.findViewById<AutoCompleteTextView>(R.id.actvFieldOne)

        val db = FirebaseFirestore.getInstance()
        val animalsCollectionRef = db.collection("animals")

        animalsCollectionRef.get().addOnSuccessListener { querySnapshot ->
            val animalIds = mutableListOf<String>()

            for (document in querySnapshot) {
                val animalId = document.getString("id")
                animalId?.let {
                    animalIds.add(it)
                }
            }

            val adapter = ArrayAdapter(autoCompleteTextView.context, R.layout.custom_spinner, animalIds)
            autoCompleteTextView.setAdapter(adapter)

            autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
                val selectedItem = adapter.getItem(position)?.toString()

                if (!selectedItem.isNullOrEmpty()) {
                    inflateNewLayout(view, selectedItem, isEditable)
                    hideKeyboard(autoCompleteTextView)
                }
            }

        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error getting documents: ", exception)
        }
        frameLayout.addView(updateFormLayout)
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun inflateNewLayout(containerView: View, selectedItem: String, isEditable: Boolean) {
        val frameLayout = containerView.findViewById<FrameLayout>(R.id.containerFragmentUpdate)
        val updateFormLayout = layoutInflater.inflate(R.layout.update_patched_form_layout, frameLayout, false)
        val linearColumnSelectDate = updateFormLayout.findViewById<LinearLayout>(R.id.linearColumnSelectDate)
        val linearColumnSelectPurchaseDate = updateFormLayout.findViewById<LinearLayout>(R.id.linearColumnSelectPurchaseDate)

        // Fetch data from Firestore using selectedItem
        val db = FirebaseFirestore.getInstance()
        val animalDocRef = db.collection("animals").document(selectedItem)

        val textColorDisabled = ContextCompat.getColor(requireContext(), R.color.bluegray_800_7f)
        val buttonDeleteColor = ContextCompat.getColor(requireContext(), R.color.red_500)

        progressBar = updateFormLayout.findViewById(R.id.progressBar)

        animalDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {

                val data = documentSnapshot.data

                // Example: Fetch animalType from Firestore
                val anmlType = data?.get("anmlType") as? String
                val anmlSpinner = updateFormLayout.findViewById<Spinner>(R.id.spinnerAnmlType)
                anmlSpinner.setupAnimalTypeSpinner(anmlType, listOf("Kambing", "Sapi", "Domba")) { selectedItem ->
                    // Handle the selected animal type, e.g., update Firestore document
                }
                anmlSpinner.isEnabled = false

                // Example: Fetch brrednType from Firestore
                val breedType = data?.get("breedType") as? String
                val breedSpinner = updateFormLayout.findViewById<Spinner>(R.id.spinnerAnmlBreed)
                breedSpinner.setupOriginTypeSpinner(breedType, listOf("Pedaging", "Perah", "Dwiguna")) { selectedItem ->
                    // Handle the selected origin type, e.g., update Firestore document
                }
                breedSpinner.isEnabled = false

                // Example: Fetch originType from Firestore
                val originType = data?.get("origin") as? String
                val originSpinner = updateFormLayout.findViewById<Spinner>(R.id.spinnerOriginType)
                originSpinner.setupOriginTypeSpinner(originType, listOf("Dalam", "Luar", "Titipan")) { selectedItem ->
                    // Handle the selected origin type, e.g., update Firestore document
                }
                originSpinner.isEnabled = true

                // Example: Fetch locationType from Firestore
                val locType = data?.get("location") as? String
                val locSpinner = updateFormLayout.findViewById<Spinner>(R.id.spinnerLoc)
                locSpinner.setupLocationTypeSpinner(locType, listOf("PAB", "TOB", "BUT", "MAR")) { selectedItem ->
                    // Handle the selected location type, e.g., update Firestore document
                }
                locSpinner.isEnabled = true

                // Example: Fetch sexType from Firestore
                val sexType = data?.get("sex") as? String
                val anmlSexSpinner = updateFormLayout.findViewById<Spinner>(R.id.spinnerAnmlSexType)
                anmlSexSpinner.setupSexTypeSpinner(sexType, listOf("Jantan", "Betina")) { selectedItem ->
                    // Handle the selected sex type, e.g., update Firestore document
                }
                anmlSexSpinner.isEnabled = false

                // Example: Fetch marriageType from Firestore
                val marriageStatus = data?.get("marriageStatus") as? String
                val anmlMarriageSpinner = updateFormLayout.findViewById<Spinner>(R.id.spinnerAnmlMarriageType)
                anmlMarriageSpinner.setupMarriageTypeSpinner(marriageStatus, listOf("Menikah", "Belum Menikah")) { selectedItem ->
                    // Handle the selected sex type, e.g., update Firestore document
                }
                anmlMarriageSpinner.isEnabled = true

                val physStatus = data?.get("anmlPhysStat") as? String // Get the animal's physiological status from the data
                val anmlPhysStatSpinner = updateFormLayout.findViewById<Spinner>(R.id.spinnerAnmlPhysStat)

                anmlPhysStatSpinner.setupAnmlPhysStatSpinner(physStatus, listOf("Pejantan", "Calon Pejantan", "Induk", "Dara", "Lepas Sapih", "Pra Sapih")) { selectedItem ->
                    // Handle the selected physiological status, e.g., update Firestore document
                }
                anmlPhysStatSpinner.isEnabled = true


                val birthDate = data?.get("birthDate") as? String
                val txtSelectDate = updateFormLayout.findViewById<TextView>(R.id.txtSelectDate)
                txtSelectDate.text = birthDate

                val purchaseDate = data?.get("purchaseDate") as? String
                val txtSelectPurchaseDate = updateFormLayout.findViewById<TextView>(R.id.txtSelectPurchaseDate)
                txtSelectPurchaseDate.text = purchaseDate

                val race = data?.get("race") as? String
                val txtRaceColumn = updateFormLayout.findViewById<TextView>(R.id.txtRaceColumn)
                txtRaceColumn.text = race

                val anmlNum = data?.get("anmlNum") as? String
                val txtAnmlNumColumn = updateFormLayout.findViewById<TextView>(R.id.txtAnmlNumColumn)
                txtAnmlNumColumn.text = anmlNum
                txtAnmlNumColumn.isEnabled = false

                val anmlPrice = data?.get("anmlPrice") as? String
                val txtAnmlPriceColumn = updateFormLayout.findViewById<EditText>(R.id.txtAnmlPriceColumn)

                anmlPrice?.let {
                    val price = it.toLongOrNull() ?: 0L
                    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                    format.maximumFractionDigits = 0 // No decimal places
                    txtAnmlPriceColumn.setText(format.format(price).replace("Rp", "Rp.")) // Add period after Rp
                }

                // Add TextWatcher for formatting the price dynamically
                txtAnmlPriceColumn.addTextChangedListener(object : TextWatcher {
                    private var current = ""

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                    override fun afterTextChanged(s: Editable?) {
                        if (s.toString() != current) {
                            txtAnmlPriceColumn.removeTextChangedListener(this)

                            // Remove "Rp." and periods, so we can work with raw numbers
                            val cleanString = s.toString().replace("Rp.", "").replace(".", "").trim()

                            if (cleanString.isNotEmpty()) {
                                // Convert the clean string to a number
                                val parsed = cleanString.toLongOrNull() ?: 0L

                                // Format it with "Rp." and thousand separators
                                val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                                format.maximumFractionDigits = 0
                                val formatted = format.format(parsed).replace("Rp", "Rp.")

                                // Set the formatted text and update the current value
                                current = formatted
                                txtAnmlPriceColumn.setText(formatted)
                                txtAnmlPriceColumn.setSelection(formatted.length) // Move cursor to the end
                            } else {
                                current = ""
                            }

                            txtAnmlPriceColumn.addTextChangedListener(this)
                        }
                    }
                })

                val anmlIndukan = data?.get("anmlNumIndukan") as? String
                val txtAnmlNumIndukan = updateFormLayout.findViewById<TextView>(R.id.txtAnmlNumIndukanColumn)
                txtAnmlNumIndukan.text = anmlIndukan

                // Load image into ImageView using Glide
                val imageView = updateFormLayout.findViewById<ImageView>(R.id.imageView)
                val imagePath = data?.get("imageUrl") as? String
                Glide.with(requireContext())
                    .load(imagePath)
                    .into(imageView)

                linearColumnSelectDate.setupDatePicker(requireContext(),
                    this, txtSelectDate)

                linearColumnSelectPurchaseDate.setupDatePicker(requireContext(),
                    this, txtSelectPurchaseDate)

                // Disable UI elements in delete mode
                if (!isEditable) {
                    disableNonEditableUI(anmlSpinner, originSpinner,
                        locSpinner, anmlSexSpinner, linearColumnSelectDate,
                        linearColumnSelectPurchaseDate, txtRaceColumn, anmlMarriageSpinner, anmlPhysStatSpinner, txtAnmlPriceColumn)

                    txtRaceColumn.setTextColor(textColorDisabled)
                    txtSelectPurchaseDate.setTextColor(textColorDisabled)
                    txtSelectDate.setTextColor(textColorDisabled)
                    txtAnmlPriceColumn.setTextColor(textColorDisabled)

                    val btnUpdate = updateFormLayout.findViewById<Button>(R.id.btnUpdate)
                    btnUpdate.isEnabled = true
                    btnUpdate.text = "DELETE"
                    btnUpdate.backgroundTintList = ColorStateList.valueOf(buttonDeleteColor)

                    // Set up the listener for deleting
                    btnUpdate.setOnClickListener {
                        progressBar.visibility = View.VISIBLE
                        handleDeleteClick(selectedItem)

                    }

                } else {
                    val btnUpdate = updateFormLayout.findViewById<Button>(R.id.btnUpdate)
                    btnUpdate.setOnClickListener {
                        // Handle the update logic here
                        progressBar.visibility = View.VISIBLE
                        handleUpdateClick(selectedItem, updateFormLayout)

                    }
                }

            } else {
                // Handle the case where the document doesn't exist
            }
        }.addOnFailureListener { exception ->
            // Handle failures
            Log.e("Firestore", "Error getting document: ", exception)
        }
        // Add the new layout to the container
        frameLayout.removeAllViews()
        frameLayout.addView(updateFormLayout)
    }

    private fun disableNonEditableUI(vararg views: View) {
        for (view in views) {
            view.isEnabled = false
        }
    }

    private fun handleDeleteClick(selectedItem: String, ) {
        progressBar.visibility = View.VISIBLE // Show progress bar when delete starts
        val db = FirebaseFirestore.getInstance()
        val animalDocRef = db.collection("animals").document(selectedItem)
        val pakanDocRef = db.collection("pakan").document(selectedItem) // Reference to pakan collection document
        val autoCompleteTextView = requireView().findViewById<AutoCompleteTextView>(R.id.actvFieldOne)

        animalDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val storage = FirebaseStorage.getInstance()
                val deleteTasks = mutableListOf<Task<Void>>()

                // Delete the image from Firebase Storage
                val imagePath = documentSnapshot.getString("imageUrl")
                if (!imagePath.isNullOrEmpty()) {
                    val storageRef = storage.getReferenceFromUrl(imagePath)
                    val imageDeleteTask = storageRef.delete()
                    deleteTasks.add(imageDeleteTask)
                }

                // Delete the QR code from Firebase Storage
                val qrCodePath = documentSnapshot.getString("qrcodePath")
                if (!qrCodePath.isNullOrEmpty()) {
                    val qrCodeRef = storage.getReferenceFromUrl(qrCodePath)
                    val qrCodeDeleteTask = qrCodeRef.delete()
                    deleteTasks.add(qrCodeDeleteTask)
                }

                // Delete the document from the pakan collection
                val pakanDeleteTask = pakanDocRef.delete()
                deleteTasks.add(pakanDeleteTask)

                // Delete the document from the animals collection
                val docDeleteTask = animalDocRef.delete()
                deleteTasks.add(docDeleteTask)

                // Wait for all deletions to complete
                Tasks.whenAllComplete(deleteTasks).addOnCompleteListener { task ->
                    progressBar.visibility = View.GONE // Hide progress bar after deletion
                    if (task.isSuccessful) {
                        val frameLayout = requireView().findViewById<FrameLayout>(R.id.containerFragmentUpdate)
                        frameLayout.removeAllViews() // Clear previous views
                        autoCompleteTextView.text = null
                        Toast.makeText(requireContext(), "Document and related data successfully deleted!", Toast.LENGTH_SHORT).show()
                        Log.d("Firestore", "All deletion tasks completed successfully!")
                    } else {
                        Toast.makeText(requireContext(), "Failed to delete some parts of the data!", Toast.LENGTH_SHORT).show()
                        Log.e("Firestore", "Some deletion tasks failed", task.exception)
                    }
                }
            } else {
                progressBar.visibility = View.GONE // Hide progress bar if no document found
                Toast.makeText(requireContext(), "Document does not exist!", Toast.LENGTH_SHORT).show()
                Log.e("Firestore", "Document does not exist")
            }
        }.addOnFailureListener { exception ->
            progressBar.visibility = View.GONE
            Toast.makeText(requireContext(), "Error retrieving document!", Toast.LENGTH_SHORT).show()
            Log.e("Firestore", "Error retrieving document from Firestore", exception)
        }
    }

    private fun handleUpdateClick(selectedItem: String, updateFormLayout: View) {
        val selectedAnmlMarriageType = extractSpinnerValue(updateFormLayout.findViewById(R.id.spinnerAnmlMarriageType))
        val selectedAnmlPhysStat = extractSpinnerValue(updateFormLayout.findViewById(R.id.spinnerAnmlPhysStat))
        val enteredRace = extractEditTextValue(updateFormLayout.findViewById(R.id.txtRaceColumn))
        val enteredAnmlIndukan = extractEditTextValue(updateFormLayout.findViewById(R.id.txtAnmlNumIndukanColumn))
        val selectedDate = extractEditTextValue(updateFormLayout.findViewById(R.id.txtSelectDate))
        val selectedPurchaseDate = extractEditTextValue(updateFormLayout.findViewById(R.id.txtSelectPurchaseDate))
        val selectedOrigin = extractSpinnerValue(updateFormLayout.findViewById(R.id.spinnerOriginType))
        val selectedLoc = extractSpinnerValue(updateFormLayout.findViewById(R.id.spinnerLoc))

        // Get the formatted price
        val formattedAnmlPrice = extractEditTextValue(updateFormLayout.findViewById(R.id.txtAnmlPriceColumn))

        // Remove "Rp." and periods from the formatted price to get the raw number
        val rawAnmlPrice = formattedAnmlPrice.replace("Rp.", "").replace(".", "").trim()

        // Create a map of new values
        val newValues = mapOf(
            "marriageStatus" to selectedAnmlMarriageType,
            "anmlPhysStat" to selectedAnmlPhysStat,
            "race" to enteredRace,
            "birthDate" to selectedDate,
            "purchaseDate" to selectedPurchaseDate,
            "origin" to selectedOrigin,
            "location" to selectedLoc,
            "anmlPrice" to rawAnmlPrice, // Save the raw price here
            "anmlNumIndukan" to enteredAnmlIndukan
        )

        // Update the Firestore document
        updateFirestoreDocument(selectedItem, newValues)
    }

    private fun updateFirestoreDocument(documentId: String, newValues: Map<String, Any>) {
        val db = FirebaseFirestore.getInstance()
        val animalDocRef = db.collection("animals").document(documentId)

        animalDocRef.update(newValues)
            .addOnSuccessListener {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Document updated successfully", Toast.LENGTH_SHORT).show()
                showReactionSuccessFragment(documentId)
            }
            .addOnFailureListener { exception ->
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Firestore document update failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun extractSpinnerValue(spinner: Spinner): String {
        return spinner.selectedItem.toString()
    }

    private fun extractEditTextValue(textView: View): String {
        return if (textView is TextView) {
            textView.text.toString().trim()
        } else {

            Toast.makeText(requireContext(), "Invalid input element", Toast.LENGTH_SHORT).show()
            ""
        }
    }

    private fun configureDefaultForm(view: View) {

    }

    private fun showReactionSuccessFragment(generatedId: String) {
        val reactionFragment = ReactionSuccessUpdateFragment.newInstance(generatedId)
        reactionFragment.show(childFragmentManager, "ReactionSuccessFragment")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("ImagePicker", "onActivityResult called")
        // Call the handleImagePickerResult function with the result data
        handleImagePickerResult(requestCode, resultCode, data, imageView)
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
}