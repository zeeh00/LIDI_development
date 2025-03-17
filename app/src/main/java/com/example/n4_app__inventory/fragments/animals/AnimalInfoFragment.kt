package com.example.n4_app__inventory.fragments.animals

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.n4_app__inventory.R
import com.example.n4_app__inventory.databinding.FragmentAnimalInfoBinding
import com.example.n4_app__inventory.fragments.animals.catatankhusus.CatatanKhususFragment
import com.example.n4_app__inventory.fragments.animals.harga.HargaFragment
import com.example.n4_app__inventory.fragments.animals.pakan.PakanFragment
import com.example.n4_app__inventory.fragments.animals.penimbangan.PenimbanganFragment
import com.example.n4_app__inventory.fragments.form.data.Animal
import com.example.n4_app__inventory.spinner.setupChart
import com.example.n4_app__inventory.spinner.setupPenimbanganChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AnimalInfoFragment : Fragment() {

    private lateinit var binding: FragmentAnimalInfoBinding
    private var animal: Animal? = null
    private lateinit var lineChart: LineChart
    private lateinit var penimbanganChart: LineChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnimalInfoBinding.inflate(inflater, container, false)
        lineChart = binding.lineChart
        penimbanganChart = binding.penimbanganChart
        val view = binding.root


        setupUI()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragmentManager.setFragmentResultListener("animalUpdate", viewLifecycleOwner) { _, bundle ->
            val updatedAnimal = bundle.getParcelable<Animal>("updatedAnimal")
            updatedAnimal?.let {
                onPakanDataUpdated(it) // Update UI with new Pakan data
                onPenimbaganDataUpdated(it)
            }
        }

    }


    override fun onResume() {
        super.onResume()
        fetchAndDisplayPakanData() // Refresh data when fragment resumes
        fetchAndDisplayPenimbanganData()
    }

    private fun setupUI() {
        // Back button listener
        binding.btnArrowleft.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // Retrieve and store the animal object
        animal = arguments?.getParcelable<Animal>(ARG_ANIMAL)

        animal?.let {
            // Set UI elements
            binding.txtAnmlId.text = "ID: ${it.id}"
            binding.txtOrigin.text = "Origin: ${it.origin}"
            binding.txtAnmlType.text = "Jenis Hewan: ${it.anmlType}"
            binding.txtAnmlBreed.text = "Tipe Hewan: ${it.breedType}"
            binding.txtAnmlPhysStat.text = "Fisiologi Hewan: ${it.anmlPhysStat}"
            binding.txtRace.text = "Ras: ${it.race}"
            binding.txtAge.text = "Umur: ${calculateAge(it.birthDate)}"
            binding.txtAnmlSexType.text = "Jenis Kelamin: ${it.sex}"
            binding.txtAnmlPejantan.text = "Nomor Pejantan: ${it.anmlNumPejantan}"
            binding.txtAnmlIndukan.text = "Nomor Indukkan: ${it.anmlNumIndukan}"
            binding.txtAnmlLoc.text = "Lokasi: ${it.location}"
            binding.txtAnmlBirthDate.text = "Tanggal Lahir: ${it.birthDate}"
            binding.txtPurchaseDate.text = "Tanggal Pembelian: ${it.purchaseDate}"
            binding.txtAnmlMarriageStat.text = "Status Kawin: ${it.marriageStatus}"
            binding.txtTanggalInputDate.text = "${it.inputDate}"
            binding.txtBobotKg.text = "${it.konsumsiPakan}"
            binding.txtTanggalInputDatePnmbgn.text = "${it.inputPenmDate}"
            binding.txtBobotPenimbanganKg.text = "${it.bbtPenm}"

            // Format the price with periods (thousands separator)
            val formattedPrice = formatPrice(it.anmlPrice)
            binding.txtAnmlPrice.text = "Harga Beli: Rp. $formattedPrice"

            // Load image using Glide
            Glide.with(requireContext())
                .load(it.imageUrl)
                .apply(RequestOptions.centerCropTransform())
                .into(binding.imgAnimal)

            fetchAndDisplayPakanData()  // Ensure chart data is set on initial load
            fetchAndDisplayPenimbanganData()
        } ?: run {
            // Handle the case where the animal data is null
            Toast.makeText(requireContext(), "Animal data is not available", Toast.LENGTH_SHORT).show()
        }

        handleClickEditPakan()
        handleClickEditPenimbangan()
        handleClickEditCatKhusus()
        handleClickEditHargaJual()
        fetchLatestCatatanKhusus()
        fetchAndDisplayHargaJualData()
    }

    private fun formatPrice(price: String): String {
        return try {
            // Parse the price as a Long and format with thousands separator
            val priceLong = price.toLong()
            val numberFormat = NumberFormat.getNumberInstance(Locale.US)
            numberFormat.format(priceLong)
        } catch (e: NumberFormatException) {
            price // Return original price if parsing fails
        }
    }

    private fun handleClickEditPakan() {
        binding.imageEdit.setOnClickListener {
            animal?.let {
                val updatedAnimal = it.copy(
                    inputDate = binding.txtTanggalInputDate.text.toString().trim(),
                    konsumsiPakan = binding.txtBobotKg.text.toString().trim()
                )
                replaceFragment(PakanFragment.newInstance(updatedAnimal))
            }
        }
    }

    private fun handleClickEditPenimbangan() {
        binding.imageEditPenimbangan.setOnClickListener {
            animal?.let {
                val updatedAnimal = it.copy(
                    inputPenmDate = binding.txtTanggalInputDatePnmbgn.text.toString().trim(),
                    bbtPenm = binding.txtBobotPenimbanganKg.text.toString().trim()
                )
                replaceFragment(PenimbanganFragment.newInstance(updatedAnimal))
            }
        }
    }
    private fun handleClickEditCatKhusus() {
        binding.imageEditCatKhusus.setOnClickListener {
            animal?.let {
                replaceFragment(CatatanKhususFragment.newInstance(it))
            }
        }
    }

    private fun handleClickEditHargaJual() {
        binding.imageEditHargaJual.setOnClickListener {
            animal?.let {
                replaceFragment(HargaFragment.newInstance(it))
            }
        }
    }

    private fun onPakanDataUpdated(animal: Animal) {
        // Update the animal instance and refresh the UI
        this.animal = animal
        binding.txtTanggalInputDate.text = animal.inputDate
        binding.txtBobotKg.text = animal.konsumsiPakan
        fetchAndDisplayPakanData() // Refresh the chart with new data
    }

    private fun onPenimbaganDataUpdated(animal: Animal) {
        // Update the animal instance and refresh the UI
        this.animal = animal
        binding.txtTanggalInputDatePnmbgn.text = animal.inputPenmDate

        // Fetch and update bbtPenimbangan from the updated animal object
        binding.txtBobotPenimbanganKg.text = animal.bbtPenm

        // Now rely on fetchAndDisplayPenimbanganData() to update bbtAwal and the chart
        fetchAndDisplayPenimbanganData() // This will dynamically set bbtAwal and refresh the chart
    }


    private fun replaceFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun calculateAge(birthDate: String): String {
        return try {
            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val date = sdf.parse(birthDate) ?: return "N/A"
            val dob = Calendar.getInstance().apply { time = date }

            val today = Calendar.getInstance()

            var ageInYears = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
            var months = today.get(Calendar.MONTH) - dob.get(Calendar.MONTH)
            var days = today.get(Calendar.DAY_OF_MONTH) - dob.get(Calendar.DAY_OF_MONTH)

            // If today's day of the month is less than birth day, adjust months and days
            if (days < 0) {
                months-- // Go back one month
                val maxDayOfPreviousMonth = today.getActualMaximum(Calendar.DAY_OF_MONTH)
                days += maxDayOfPreviousMonth // Adjust days by the number of days in the previous month
            }

            // Adjust age if birthday hasn't occurred yet this year
            if (months < 0) {
                ageInYears--
                months += 12 // Adjust for the negative month count
            }

            // Construct age string
            val ageParts = mutableListOf<String>()
            if (ageInYears > 0) ageParts.add("$ageInYears Tahun")
            if (months > 0) ageParts.add("$months Bulan")
            if (days > 0) ageParts.add("$days Hari")

            ageParts.joinToString(", ").ifEmpty { "N/A" }
        } catch (e: Exception) {
            e.printStackTrace()
            "N/A"
        }
    }

    private fun fetchAndDisplayPakanData() {
        val firestore = FirebaseFirestore.getInstance()
        val animalId = animal?.id ?: return

        firestore.collection("pakan").document(animalId)
            .addSnapshotListener { documentSnapshot, e ->
                if (e != null) return@addSnapshotListener

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val weightsList = documentSnapshot.get("weights") as? List<String> ?: emptyList()
                    val dateList = documentSnapshot.get("dates") as? List<String> ?: emptyList() // Fetch corresponding dates

                    // Create entries with proper index mapping
                    val entries = weightsList.mapIndexed { index, weight ->
                        Entry(index.toFloat(), weight.toFloat()) // Ensure x is the index
                    }

                    // Log the entries for debugging
                    Log.d("ChartEntries", "Entries: $entries")

                    // Use the extension function to set up the Pakan chart
                    lineChart.setupChart(entries, "Konsumsi Pakan", "Konsumsi Pakan Over Time", Color.BLUE, dateList)
                }
            }
    }

    private fun fetchAndDisplayPenimbanganData() {
        val firestore = FirebaseFirestore.getInstance()
        val animalId = animal?.id ?: return

        firestore.collection("penimbangan").document(animalId)
            .addSnapshotListener { documentSnapshot, e ->
                if (e != null) return@addSnapshotListener

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    // Fetch penimbangan data
                    val bbtPenimbanganList = documentSnapshot.get("bbtPenm") as? List<String> ?: emptyList()
                    val dateList = documentSnapshot.get("inputPenmDate") as? List<String> ?: emptyList()

                    if (bbtPenimbanganList.size < 2 || dateList.size < 2) {
                        Log.e("PenimbanganData", "Not enough data to calculate ADG")
                        return@addSnapshotListener
                    }

                    val bbtAwal = bbtPenimbanganList.first().toFloatOrNull() ?: 0f
                    val bbtAkhir = bbtPenimbanganList.last().toFloatOrNull() ?: 0f

                    // Convert date strings to Date objects safely
                    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    val dates = dateList.mapNotNull { sdf.parse(it) }

                    if (dates.size < 2) {
                        Log.e("PenimbanganData", "Invalid dates")
                        return@addSnapshotListener
                    }

                    val startDate = dates.first()
                    val endDate = dates.last()

                    // Calculate days difference
                    val daysDiff = maxOf(1, ((endDate.time - startDate.time) / (1000 * 60 * 60 * 24)).toInt())

                    // Calculate Pertambahan Bobot & ADG
                    val pertambahanBobot = bbtAkhir - bbtAwal
                    val adg = pertambahanBobot / daysDiff

                    // Update UI
                    requireActivity().runOnUiThread {
                        binding.txtBobotAwalKg.text = "$bbtAwal"
                        binding.txtBobotPertambahanKg.text = String.format("%.2f", adg) // Display ADG
                        binding.txtFcrKg.text = if (pertambahanBobot > 0) {
                            String.format("%.2f", (animal?.konsumsiPakan?.toFloatOrNull() ?: 0f) / pertambahanBobot)
                        } else {
                            "-"
                        }
                    }

                    Log.d("PenimbanganData", "BBT Awal: $bbtAwal, BBT Akhir: $bbtAkhir, Pertambahan: $pertambahanBobot, ADG: $adg")

                    // Create entries for penimbangan chart
                    val bbtPenimbanganEntries = bbtPenimbanganList.mapIndexed { index, weight ->
                        weight.toFloatOrNull()?.let { Entry(index.toFloat(), it) }
                    }.filterNotNull() // Remove invalid entries

                    // Update Penimbangan Chart
                    penimbanganChart.setupPenimbanganChart(
                        emptyList(),
                        bbtPenimbanganEntries,
                        dateList,
                        "Bobot Awal",
                        "Bobot Penimbangan",
                        "Penimbangan Over Time"
                    )
                }
            }
    }




    private fun fetchLatestCatatanKhusus() {
        val firestore = FirebaseFirestore.getInstance()
        val animalId = animal?.id ?: return // Get the current animal ID

        firestore.collection("kejadian") // Your Firestore collection
            .whereEqualTo("animalId", animalId) // Filter by the current animal ID
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents.first() // Get the first document

                    // Check if the kejadianEntries array is not empty
                    val kejadianEntries = document.get("kejadianEntries") as? List<Map<String, Any>>
                    if (!kejadianEntries.isNullOrEmpty()) {
                        // Get the most recent entry (you may want to sort if you have timestamps in the entries)
                        val latestEntry = kejadianEntries.last() // Assuming the last entry is the most recent

                        // Extract fields from the latest entry
                        val kejadianKhusus = latestEntry["kejadianKhusus"] as? String ?: "No data"
                        val tanggalInput = latestEntry["tanggal"] as? String ?: "No date"
                        val catatanKhusus = latestEntry["catatanKhusus"] as? String ?: "No notes"
                        val imageUrl = latestEntry["imageUrlKejadian"] as? String // Ensure this is the correct field name

                        // Update the UI
                        binding.txtKejadianKhususJudul.text = kejadianKhusus
                        binding.txtTanggalInputDateKejadian.text = tanggalInput
                        binding.txtCatatanKejadian.text = catatanKhusus

                        // Load the image using Glide or Picasso
                        if (!imageUrl.isNullOrEmpty()) {
                            Glide.with(requireContext())
                                .load(imageUrl)
                                .into(binding.imgKejadianAnimal)
                        } else {
                            binding.imgKejadianAnimal.setImageResource(R.drawable.ic_image_placeholder) // Placeholder image
                        }
                    } else {
                        // Handle case where no entries are found
                        binding.txtKejadianKhususJudul.text = "No special event available."
                        binding.txtTanggalInputDateKejadian.text = ""
                        binding.txtCatatanKejadian.text = ""
                        binding.imgKejadianAnimal.setImageResource(R.drawable.ic_image_placeholder) // Placeholder image
                    }
                } else {
                    // Handle case where no documents are found
                    binding.txtKejadianKhususJudul.text = "No special event available."
                    binding.txtTanggalInputDateKejadian.text = ""
                    binding.txtCatatanKejadian.text = ""
                    binding.imgKejadianAnimal.setImageResource(R.drawable.ic_image_placeholder) // Placeholder image
                }
            }
            .addOnFailureListener { e ->
                Log.e("AnimalInfoFragment", "Error fetching catatan khusus", e)
                Toast.makeText(requireContext(), "Failed to fetch notes.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchAndDisplayHargaJualData() {
        val firestore = FirebaseFirestore.getInstance()
        val animalId = animal?.id ?: return // Get the current animal ID

        firestore.collection("harga") // Your Firestore collection for harga
            .document(animalId) // Document based on animalId
            .addSnapshotListener { documentSnapshot, e ->
                if (e != null) return@addSnapshotListener

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    // Fetch hargaJual
                    val hargaJual = documentSnapshot.getString("hargaJual") ?: "0"

                    // Format and update the UI
                    binding.txtTanggalInputHargaJual.text = formatToCurrency(hargaJual)
                } else {
                    // Handle case where no document is found
                    binding.txtTanggalInputHargaJual.text = "No harga available"
                }
            }
    }

    private fun formatToCurrency(value: String?): String {
        return if (!value.isNullOrEmpty()) {
            val amount = value.toDoubleOrNull()
            amount?.let {
                NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(it)
            } ?: "Rp. 0"
        } else {
            "Rp. 0"
        }
    }

    companion object {
        private const val ARG_ANIMAL = "arg_animal"

        fun newInstance(animal: Animal): AnimalInfoFragment {
            val fragment = AnimalInfoFragment()
            val args = Bundle()
            args.putParcelable(ARG_ANIMAL, animal)
            fragment.arguments = args
            return fragment
        }
    }
}
