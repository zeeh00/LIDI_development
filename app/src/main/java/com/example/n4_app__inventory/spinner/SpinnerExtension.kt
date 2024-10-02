package com.example.n4_app__inventory.spinner

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.n4_app__inventory.R
import com.example.n4_app__inventory.fragments.form.data.Animal
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// Set up the spinner adapter and item selection listener
// Extension function for setting up a Spinner with a listener
fun Spinner.setupActionSpinner(context: Context, formTypes: Array<String>, frameLayout: FrameLayout, listener: (String) -> Unit) {
    val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, formTypes)
    adapter.setDropDownViewResource(R.layout.custom_spinner)
    this.adapter = adapter

    // Set "Select Type" as the default selection
    this.setSelection(0)

    this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val selectedFormType = formTypes[position]
            listener(selectedFormType)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            // Handle case where nothing is selected
        }
    }
}

fun Spinner.setupAnmlTypeSpinner(inputFormLayout: View, listener: (String) -> Unit) {
    // Access UI elements in the input form layout
    val AnmlType = inputFormLayout.findViewById<Spinner>(R.id.spinnerAnmlType)

    // Access the array of animal types from strings.xml
    val animalTypes = resources.getStringArray(R.array.anml_types)

    // Set up the spinner adapter
    val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, animalTypes)
    adapter.setDropDownViewResource(R.layout.custom_spinner)
    AnmlType.adapter = adapter

    // Set "Select Type" as the default selection
    AnmlType.setSelection(0)

    AnmlType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val selectedAnimalType = animalTypes[position]
            listener(selectedAnimalType)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            // Handle case where nothing is selected
        }
    }
}

fun Spinner.setupOriginTypeSpinner(inputFormLayout: View, listener: (String) -> Unit) {
    // Access UI elements in the input form layout
    val OriginType = inputFormLayout.findViewById<Spinner>(R.id.spinnerOriginType)

    // Access the array of animal origin types from strings.xml
    val originTypes = resources.getStringArray(R.array.origin_types)

    // Set up the spinner adapter
    val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, originTypes)
    adapter.setDropDownViewResource(R.layout.custom_spinner)
    OriginType.adapter = adapter

    // Set "Select Type" as the default selection
    OriginType.setSelection(0)

    OriginType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val selectedOriginType = originTypes[position]
            listener(selectedOriginType)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            // Handle case where nothing is selected
        }
    }
}

fun Spinner.setupLocTypeSpinner(inputFormLayout: View, listener: (String) -> Unit) {
    // Access UI elements in the input form layout
    val LocType = inputFormLayout.findViewById<Spinner>(R.id.spinnerLoc)

    // Access the array of location types from strings.xml
    val locType = resources.getStringArray(R.array.location)

    // Set up the spinner adapter
    val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, locType)
    adapter.setDropDownViewResource(R.layout.custom_spinner)
    LocType.adapter = adapter

    // Set "Select Type" as the default selection
    LocType.setSelection(0)

    LocType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val selectedLoc = locType[position]
            listener(selectedLoc)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            // Handle case where nothing is selected
        }
    }
}

fun LinearLayout.setupDatePicker(context: Context, fragment: Fragment, selectDateTextView: TextView) {
    this.setOnClickListener {
        showMaterialDatePicker(context, fragment, selectDateTextView)
    }
}

fun showMaterialDatePicker(context: Context, fragment: Fragment, selectDateTextView: TextView) {
    // Create a MaterialDatePicker
    val builder = MaterialDatePicker.Builder.datePicker()
    builder.setTitleText("Select a date")

    // Get today's date at the end of the day (23:59:59)
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }
    val maxDate = calendar.timeInMillis // Maximum date is today

    builder.setCalendarConstraints(
        CalendarConstraints.Builder()
            .setEnd(maxDate) // Maximum date is today
            .build()
    )

    // Create and show the date picker dialog
    val datePicker = builder.build()
    datePicker.setStyle(R.style.CustomDatePickerStyle, 0)

    datePicker.addOnPositiveButtonClickListener { selectedDate ->
        // Set the selected date to midnight (00:00:00) to avoid time affecting calculations
        val calendar = Calendar.getInstance().apply {
            timeInMillis = selectedDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Format the selected date
        val formattedDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.time)

        // Update the TextView with the selected date
        selectDateTextView.text = formattedDate
        selectDateTextView.visibility = View.VISIBLE
    }

    datePicker.show(fragment.parentFragmentManager, "DatePicker")
}

fun Spinner.setupSexTypeSpinner(inputFormLayout: View, listener: (String) -> Unit) {
    // Access UI elements in the input form layout
    val AnmlSexType = inputFormLayout.findViewById<Spinner>(R.id.spinnerAnmlSexType)

    // Access the array of animal sex types from strings.xml
    val anmlSexType = resources.getStringArray(R.array.anml_sex_type)

    // Set up the spinner adapter
    val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, anmlSexType)
    adapter.setDropDownViewResource(R.layout.custom_spinner)
    AnmlSexType.adapter = adapter

    // Set "Select Type" as the default selection
    AnmlSexType.setSelection(0)

    AnmlSexType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val selectedAnmlSexType = anmlSexType[position]
            listener(selectedAnmlSexType)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            // Handle case where nothing is selected
        }
    }
}

fun Spinner.setupMarriageTypeSpinner(inputFormLayout: View, listener: (String) -> Unit) {
    // Access UI elements in the input form layout
    val AnmlMarriageType = inputFormLayout.findViewById<Spinner>(R.id.spinnerAnmlMarrStat)

    // Access the array of animal sex types from strings.xml
    val anmlMarriageType = resources.getStringArray(R.array.anml_marriage_type)

    // Set up the spinner adapter
    val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, anmlMarriageType)
    adapter.setDropDownViewResource(R.layout.custom_spinner)
    AnmlMarriageType.adapter = adapter

    // Set "Select Type" as the default selection
    AnmlMarriageType.setSelection(0)

    AnmlMarriageType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val selectedAnmlMarriageType = anmlMarriageType[position]
            listener(selectedAnmlMarriageType)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            // Handle case where nothing is selected
        }
    }
}

fun Spinner.setupAnimalBreedSpinner(inputFormLayout: View, listener: (String) -> Unit) {
    // Access UI elements in the input form layout
    val AnmlBreedType = inputFormLayout.findViewById<Spinner>(R.id.spinnerAnmlBreed)

    // Access the array of animal sex types from strings.xml
    val anmlbreedType = resources.getStringArray(R.array.anml_breed_type)

    // Set up the spinner adapter
    val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, anmlbreedType)
    adapter.setDropDownViewResource(R.layout.custom_spinner)
    AnmlBreedType.adapter = adapter

    // Set "Select Type" as the default selection
    AnmlBreedType.setSelection(0)

    AnmlBreedType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val selectedAnmlBreedType = anmlbreedType[position]
            listener(selectedAnmlBreedType)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            // Handle case where nothing is selected
        }
    }
}

// Extension function for a LinearLayout to open the image picker
fun LinearLayout.setupImagePickerWithPermissionCheck(fragment: Fragment) {
    setOnClickListener {
        openImagePickerWithPermissionCheck(fragment)
    }
}

private fun openImagePickerWithPermissionCheck(fragment: Fragment) {
    if (ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        openImagePicker(fragment)
    } else {
        // Request camera permission
        ActivityCompat.requestPermissions(
            fragment.requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST
        )
    }
}

fun openImagePicker(fragment: Fragment) {
    val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    galleryIntent.type = "image/*"

    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

    val chooserIntent = Intent.createChooser(galleryIntent, "Select Image")
    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))

    fragment.startActivityForResult(chooserIntent, PICK_IMAGE_REQUEST)
}

// Extension function for a Fragment to handle the result of the image picker
fun Fragment.handleImagePickerResult(requestCode: Int, resultCode: Int, data: Intent?, imageView: ImageView): Uri? {
    return if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
        if (data?.data == null && data?.extras != null) {
            // Camera-captured image
            val bitmap = data.extras!!.get("data") as Bitmap
            val resizedBitmap = resizeBitmap(bitmap, 1024, 1024)
            val rotatedBitmap = rotateBitmapIfRequired(resizedBitmap, null, requireContext()) // Pass context
            imageView.setImageBitmap(rotatedBitmap)
            imageView.visibility = View.VISIBLE
            null // Return null for camera-captured images
        } else {
            // Gallery-selected image
            val selectedImageUri: Uri? = data?.data
            selectedImageUri?.let {
                val inputStream = requireContext().contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val resizedBitmap = resizeBitmap(bitmap, 1024, 1024)
                val rotatedBitmap = rotateBitmapIfRequired(resizedBitmap, it, requireContext()) // Pass context
                imageView.setImageBitmap(rotatedBitmap)
                imageView.visibility = View.VISIBLE
            }
            selectedImageUri // Return the selected image URI
        }
    } else {
        imageView.visibility = View.GONE
        null
    }
}

// Helper function to check and apply rotation
fun rotateBitmapIfRequired(bitmap: Bitmap, uri: Uri?, context: Context): Bitmap {
    try {
        val exif: ExifInterface? = if (uri != null) {
            val inputStream = context.contentResolver.openInputStream(uri)
            ExifInterface(inputStream!!)
        } else {
            null // For camera images, Exif data might not be available
        }

        exif?.let {
            val orientation = it.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            val matrix = Matrix()

            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            }

            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return bitmap // Return the original bitmap if no rotation is needed or if there's an error
}


// Helper function to resize the bitmap
fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val aspectRatio = width.toFloat() / height.toFloat()

    return if (width > height) {
        Bitmap.createScaledBitmap(bitmap, maxWidth, (maxWidth / aspectRatio).toInt(), false)
    } else {
        Bitmap.createScaledBitmap(bitmap, (maxHeight * aspectRatio).toInt(), maxHeight, false)
    }
}

private const val TAG = "YourTag"
const val PICK_IMAGE_REQUEST = 123
const val CAMERA_PERMISSION_REQUEST = 456