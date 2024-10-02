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
            binding.txtAnmlIndukan.text = "Nomor Indukkan: ${it.anmlNumIndukan}"
            binding.txtAnmlLoc.text = "Lokasi: ${it.location}"
            binding.txtPurchaseDate.text = "Tanggal Pembelian: ${it.purchaseDate}"
            binding.txtAnmlMarriageStat.text = "Status Pernikahan: ${it.marriageStatus}"
            binding.txtTanggalInputDate.text = "${it.inputDate}"
            binding.txtBobotKg.text = "${it.konsumsiPakan}kg"
            binding.txtTanggalInputDatePnmbgn.text = "${it.inputPenmDate}"
            binding.txtBobotAwalKg.text = "${it.bbtAwal}"
            binding.txtBobotPenimbanganKg.text = "${it.bbtPenm}"
            binding.txtAnmlPrice.text = "Harga Beli: Rp. ${it.anmlPrice}"

            // Load image using Glide
            Glide.with(requireContext())
                .load(it.imageUrl)
                .apply(RequestOptions.centerCropTransform())
                .into(binding.imgAnimal)

            fetchAndDisplayPakanData()  // Ensure chart data is set on initial load
        } ?: run {
            // Handle the case where the animal data is null
            Toast.makeText(requireContext(), "Animal data is not available", Toast.LENGTH_SHORT).show()
        }

        handleClickEditPakan()
        handleClickEditPenimbangan()
        handleClickEditCatKhusus()
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
                    bbtAwal = binding.txtBobotAwalKg.text.toString().trim(),
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
        binding.txtBobotAwalKg.text = animal.bbtAwal
        binding.txtBobotPenimbanganKg.text = animal.bbtPenm
        fetchAndDisplayPenimbanganData() // Refresh the chart with new data
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
                    val bbtAwalList = documentSnapshot.get("bbtAwal") as? List<String> ?: emptyList()
                    val bbtPenimbanganList = documentSnapshot.get("bbtPenm") as? List<String> ?: emptyList()
                    val dateList = documentSnapshot.get("dates") as? List<String> ?: emptyList()

                    // Create entries for bbtAwal
                    val bbtAwalEntries = bbtAwalList.mapIndexed { index, weight ->
                        Entry(index.toFloat(), weight.toFloat())
                    }

                    // Create entries for bbtPenimbangan
                    val bbtPenimbanganEntries = bbtPenimbanganList.mapIndexed { index, weight ->
                        Entry(index.toFloat(), weight.toFloat())
                    }

                    // Use the extension function to set up the Penimbangan chart with both datasets and date list
                    penimbanganChart.setupPenimbanganChart(
                        bbtAwalEntries,
                        bbtPenimbanganEntries,
                        dateList,
                        "Bobot Awal",
                        "Bobot Penimbangan",
                        "Penimbangan Over Time"
                    )
                }
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
