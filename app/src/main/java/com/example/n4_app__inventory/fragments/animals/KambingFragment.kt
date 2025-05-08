package com.example.n4_app__inventory.fragments.animals

import AnimalAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.n4_app__inventory.R
import com.example.n4_app__inventory.databinding.FragmentKambingBinding
import com.example.n4_app__inventory.fragments.form.data.Animal
import com.example.n4_app__inventory.functions.setupLiveSearch
import com.google.firebase.firestore.FirebaseFirestore

class KambingFragment : Fragment(), AnimalAdapter.OnItemClickListener {

    private lateinit var animalAdapter: AnimalAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var searchView: SearchView
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var binding: FragmentKambingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentKambingBinding.inflate(inflater, container, false)
        val view = binding.root

        recyclerView = view.findViewById(R.id.recyclerListpathtwo)
        searchView = view.findViewById(R.id.searchViewGroupFiftyOne)
        progressBar = view.findViewById(R.id.progressBar)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)
        animalAdapter = AnimalAdapter(this)
        recyclerView.adapter = animalAdapter

        // Set up back button
        val arrowLeftButton: ImageButton = view.findViewById(R.id.btnArrowleft)
        arrowLeftButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        progressBar.visibility = View.VISIBLE
        fetchDataFromFirestore()

        searchView.setupLiveSearch(animalAdapter, 300)
        return view
    }

    private fun fetchDataFromFirestore() {
        val collectionReference = firestore.collection("animals")

        collectionReference.whereEqualTo("anmlType", "Kambing")
            .get()
            .addOnSuccessListener { documents ->
                val animals = mutableListOf<Animal>()

                for (document in documents) {
                    val animal = document.toObject(Animal::class.java)
                    animals.add(animal)
                }

                animalAdapter.setData(animals)
                progressBar.visibility = View.GONE
            }
            .addOnFailureListener {
                progressBar.visibility = View.GONE
            }
    }

    override fun onLinearColumnClick(animal: Animal) {
        replaceToAnimalInfoFragment(animal)
    }

    override fun onImageAnimalClick(animal: Animal) {
        replaceToAnimalInfoFragment(animal)
    }

    private fun replaceToAnimalInfoFragment(animal: Animal) {
        val animalInfo = AnimalInfoFragment.newInstance(animal)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, animalInfo)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    companion object {
        fun newInstance(): KambingFragment {
            return KambingFragment()
        }
    }
}
