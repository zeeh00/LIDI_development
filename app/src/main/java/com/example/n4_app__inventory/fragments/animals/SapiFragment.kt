package com.example.n4_app__inventory.fragments.animals

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.n4_app__inventory.R
import com.example.n4_app__inventory.databinding.FragmentSapiBinding
import com.example.n4_app__inventory.fragments.form.data.Animal
import com.example.n4_app__inventory.fragments.profile.ProfileFragment
import com.google.firebase.firestore.FirebaseFirestore

class SapiFragment : Fragment(), AnimalAdapter.OnItemClickListener {

    private lateinit var animalAdapter: AnimalAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var binding: FragmentSapiBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSapiBinding.inflate(inflater, container, false)
        val view = binding.root

        recyclerView = view.findViewById(R.id.recyclerListpathtwo)
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager

        animalAdapter = AnimalAdapter(this)
        recyclerView.adapter = animalAdapter

        val arrowLeftButton: ImageButton = view.findViewById(R.id.btnArrowleft)
        arrowLeftButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.imgProfile.setOnClickListener {
            val profileFragment = ProfileFragment()
            replaceFragment(profileFragment)
        }


        progressBar = view.findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        fetchDataFromFirestore()

        return view
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun fetchDataFromFirestore() {
        val collectionReference = firestore.collection("animals")

        // Example: Query all documents where anmlType is "Domba"
        collectionReference.whereEqualTo("anmlType", "Sapi")
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
            .addOnFailureListener { exception ->
                // Handle failures
            }
    }

    override fun onLinearColumnClick(animal: Animal) {
        replacetoAnimalInfoFragment(animal)
    }

    override fun onImageAnimalClick(animal: Animal) {
        replacetoAnimalInfoFragment(animal)
    }

    private fun replacetoAnimalInfoFragment(animal: Animal) {
        val animalInfo = AnimalInfoFragment.newInstance(animal)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, animalInfo)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    companion object {
        fun newInstance(): SapiFragment {
            return SapiFragment()
        }
    }
}