package com.example.n4_app__inventory.spinner

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.n4_app__inventory.R
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.components.Description
import android.graphics.Color
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

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

    // Create and show the date picker dialog
    val datePicker = builder.build()
    datePicker.setStyle(R.style.CustomDatePickerStyle, 0)

    // Handle the selected date
    datePicker.addOnPositiveButtonClickListener { selectedDate ->
        // Get today's date at the start of the day (00:00:00)
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Get the calendar instance for the selected date
        val selectedCalendar = Calendar.getInstance().apply {
            timeInMillis = selectedDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Check if the selected date is after today
        if (selectedCalendar.after(today)) {
            // If the selected date is in the future, show an error message
            Toast.makeText(context, "Tanggal tidak boleh lebih dari hari ini!", Toast.LENGTH_SHORT).show()
        } else {
            // If the selected date is valid, format and display it
            val formattedDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(selectedCalendar.time)

            // Update the TextView with the selected date
            selectDateTextView.text = formattedDate
            selectDateTextView.visibility = View.VISIBLE
        }
    }

    // Show the date picker
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

fun Spinner.setupAnimalPhysStat(inputFormLayout: View, listener: (String) -> Unit) {
    // Access UI elements in the input form layout
    val AnmlPhysStat = inputFormLayout.findViewById<Spinner>(R.id.spinnerAnmlPhysStat)

    // Access the array of animal sex types from strings.xml
    val anmlPhysStat = resources.getStringArray(R.array.anml_phys_stat)

    // Set up the spinner adapter
    val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, anmlPhysStat)
    adapter.setDropDownViewResource(R.layout.custom_spinner)
    AnmlPhysStat.adapter = adapter

    // Set "Select Type" as the default selection
    AnmlPhysStat.setSelection(0)

    AnmlPhysStat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val selectedAnmlPhysStat = anmlPhysStat[position]
            listener(selectedAnmlPhysStat)
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

fun LineChart.setupChart(
    entries: List<Entry>,
    label: String,
    descriptionText: String,
    lineColor: Int,
    dateList: List<String> // Add this parameter
) {
    this.setExtraOffsets(10f, 10f, 10f, 10f)

    val lineDataSet = LineDataSet(entries, label).apply {
        color = lineColor
        valueTextColor = Color.BLACK
        lineWidth = 2f
        circleRadius = 4f
        setCircleColor(lineColor)
        setDrawValues(true)
    }

    val lineData = LineData(lineDataSet)
    this.data = lineData

    // Set description text
    this.description = Description().apply {
        text = descriptionText
    }

    // Customize X-axis to display dates
    this.xAxis.labelRotationAngle = 30f
    this.xAxis.setDrawGridLines(false)
    this.xAxis.granularity = 1f // Ensure labels are shown for each point

    // Set up a ValueFormatter to display dates on the X-axis
    this.xAxis.valueFormatter = object : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val index = value.toInt()
            return if (index >= 0 && index < dateList.size) {
                dateList[index] // Return corresponding date
            } else {
                ""
            }
        }
    }

    // Disable right Y-axis
    this.axisRight.isEnabled = false

    // Enable pinch-to-zoom and allow zooming
    this.setPinchZoom(true)
    this.isDragEnabled = true

    // Set maximum visible range (number of X-values visible at once)
    this.setVisibleXRangeMaximum(8f)

    // Enable scaling in both X and Y axes
    this.isScaleXEnabled = true
    this.isScaleYEnabled = true

    // Refresh the chart with the new data
    this.invalidate()
}


fun LineChart.setupPenimbanganChart(
    bbtAwalEntries: List<Entry>,
    bbtPenimbanganEntries: List<Entry>,
    dateList: List<String>,  // List of dates corresponding to the entries
    labelAwal: String,
    labelPenimbangan: String,
    descriptionText: String
) {
    this.setExtraOffsets(10f, 10f, 10f, 10f)
    // Create dataset for bbtAwal (initial weights)
    val bbtAwalDataSet = LineDataSet(bbtAwalEntries, labelAwal).apply {
        color = Color.RED
        valueTextColor = Color.BLACK
        lineWidth = 2f
        circleRadius = 4f
        setCircleColor(Color.RED)
        setDrawValues(true)
    }

    // Create dataset for bbtPenimbangan (weigh-in results)
    val bbtPenimbanganDataSet = LineDataSet(bbtPenimbanganEntries, labelPenimbangan).apply {
        color = Color.BLUE
        valueTextColor = Color.BLACK
        lineWidth = 2f
        circleRadius = 4f
        setCircleColor(Color.BLUE)
        setDrawValues(true)
    }

    // Combine both datasets into LineData
    val lineData = LineData(bbtAwalDataSet, bbtPenimbanganDataSet)
    this.data = lineData

    // Customize the description text
    this.description = Description().apply {
        text = descriptionText
    }

    // Custom formatter for the X-axis to display dates
    this.xAxis.valueFormatter = object : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            val index = value.toInt()
            return if (index in dateList.indices) dateList[index] else ""
        }
    }

    // Customize X-axis label rotation and grid lines
    this.xAxis.labelRotationAngle = 45f
    this.xAxis.setDrawGridLines(false)

    // Disable right Y-axis
    this.axisRight.isEnabled = false

    // Enable pinch zoom and touch interactions
    this.setPinchZoom(true)  // Pinch-to-zoom for both axes
    this.isDragEnabled = true  // Allow dragging

    // Set maximum visible range (number of X-values visible at once)
    this.setVisibleXRangeMaximum(8f)  // Show 5 data points initially, adjust as needed
    this.moveViewToX(0f)  // Start at the beginning of the data

    // Allow zooming and scaling
    this.isScaleXEnabled = true
    this.isScaleYEnabled = true

    // Refresh the chart with the new data
    this.invalidate()
}


private const val TAG = "YourTag"
const val PICK_IMAGE_REQUEST = 123
const val CAMERA_PERMISSION_REQUEST = 456