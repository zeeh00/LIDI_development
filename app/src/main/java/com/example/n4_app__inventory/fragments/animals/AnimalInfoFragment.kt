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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnimalInfoBinding.inflate(inflater, container, false)
        lineChart = binding.lineChart
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
            }
        }

        parentFragmentManager.setFragmentResultListener("pakanDataUpdated", viewLifecycleOwner) { _, _ ->
            fetchAndDisplayChartData() // Auto-refresh chart when data is updated
        }
    }


    override fun onResume() {
        super.onResume()
        fetchAndDisplayChartData() // Refresh data when fragment resumes
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
            binding.txtRace.text = "Ras: ${it.race}"
            binding.txtAge.text = "Umur: ${calculateAge(it.birthDate)}"
            binding.txtAnmlSexType.text = "Jenis Kelamin: ${it.sex}"
            binding.txtAnmlLoc.text = "Lokasi: ${it.location}"
            binding.txtPurchaseDate.text = "Tanggal Pembelian: ${it.purchaseDate}"
            binding.txtAnmlMarriageStat.text = "Status Pernikahan: ${it.marriageStatus}"
            binding.txtTanggalInputDate.text = "${it.inputDate}"
            binding.txtBobotKg.text = "${it.konsumsiPakan}"
            binding.txtTanggalInputDatePnmbgn.text = "${it.inputPenmDate}"
            binding.txtBobotAwalKg.text = "${it.bbtAwal}"
            binding.txtBobotPenimbanganKg.text = "${it.bbtPenm}"
            binding.txtAnmlPrice.text = "Harga Beli: Rp. ${it.anmlPrice}"

            // Load image using Glide
            Glide.with(requireContext())
                .load(it.imageUrl)
                .apply(RequestOptions.centerCropTransform())
                .into(binding.imgAnimal)

            fetchAndDisplayChartData()  // Ensure chart data is set on initial load
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
                replaceFragment(PenimbanganFragment.newInstance(it))
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
        fetchAndDisplayChartData() // Refresh the chart with new data
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



//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        parentFragmentManager.setFragmentResultListener("updateAnimal", viewLifecycleOwner) { _, bundle ->
//            val updatedAnimal = bundle.getParcelable<Animal>("animal")
//            updatedAnimal?.let {
//                updateUIWithAnimalData(it) // Call method to update UI
//            }
//        }
//    }
//    private fun updateUIWithAnimalData(animal: Animal) {
//        binding.txtTanggalInputDate.text = animal.inputDate
//        binding.txtBobotKg.text = animal.konsumsiPakan
//    }

    private fun fetchAndDisplayChartData() {
        val firestore = FirebaseFirestore.getInstance()
        val animalId = animal?.id ?: run {
            Toast.makeText(requireContext(), "Animal ID is not valid", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("pakan").document(animalId)
            .addSnapshotListener { documentSnapshot, e ->
                if (e != null) {
                    Toast.makeText(requireContext(), "Failed to fetch data: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val weightsList = documentSnapshot.get("weights") as? List<String> ?: emptyList()
                    val datesList = documentSnapshot.get("dates") as? List<String> ?: emptyList()

                    // Prepare data for the chart
                    val entries = mutableListOf<Entry>()
                    for (i in weightsList.indices) {
                        val weight = weightsList[i].toFloatOrNull() ?: 0f
                        entries.add(Entry(i.toFloat(), weight))
                    }

                    // Create LineDataSet and LineData
                    val lineDataSet = LineDataSet(entries, "Konsumsi Pakan")
                    lineDataSet.color = Color.BLUE
                    lineDataSet.valueTextColor = Color.BLACK

                    val lineData = LineData(lineDataSet)
                    lineChart.data = lineData

                    val description = Description()
                    description.text = "Konsumsi Pakan Over Time"
                    lineChart.description = description
                    lineChart.xAxis.labelRotationAngle = 30f
                    lineChart.xAxis.setDrawGridLines(false)
                    lineChart.axisRight.isEnabled = false

                    lineChart.xAxis.apply {
                        granularity = 1f
                        isGranularityEnabled = true
                        setLabelCount(weightsList.size, true)
                        valueFormatter = object : ValueFormatter() {
                            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                                val index = value.toInt()
                                return if (index in datesList.indices) {
                                    datesList[index]
                                } else {
                                    ""
                                }
                            }
                        }
                    }

                    lineChart.invalidate()
                } else {
//                    Toast.makeText(requireContext(), "No Pakan data found", Toast.LENGTH_SHORT).show()
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
