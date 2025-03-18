package com.example.n4_app__inventory.fragments.animals

<<<<<<< HEAD
=======
import AnimalAdapter
>>>>>>> c22f9dc796c365bc15eea4d67f8d2e345d4e6626
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
<<<<<<< HEAD
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
=======
import androidx.appcompat.widget.SearchView
>>>>>>> c22f9dc796c365bc15eea4d67f8d2e345d4e6626
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.n4_app__inventory.R
import com.example.n4_app__inventory.databinding.FragmentKambingBinding
import com.example.n4_app__inventory.fragments.form.data.Animal
<<<<<<< HEAD
import com.example.n4_app__inventory.fragments.profile.ProfileFragment
=======
import com.example.n4_app__inventory.functions.setupLiveSearch
>>>>>>> c22f9dc796c365bc15eea4d67f8d2e345d4e6626
import com.google.firebase.firestore.FirebaseFirestore

class KambingFragment : Fragment(), AnimalAdapter.OnItemClickListener {

    private lateinit var animalAdapter: AnimalAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
<<<<<<< HEAD
=======
    private lateinit var searchView: SearchView
>>>>>>> c22f9dc796c365bc15eea4d67f8d2e345d4e6626
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var binding: FragmentKambingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
<<<<<<< HEAD
    ): View? {
=======
    ): View {
>>>>>>> c22f9dc796c365bc15eea4d67f8d2e345d4e6626
        binding = FragmentKambingBinding.inflate(inflater, container, false)
        val view = binding.root

        recyclerView = view.findViewById(R.id.recyclerListpathtwo)
<<<<<<< HEAD
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager

        animalAdapter = AnimalAdapter(this)
        recyclerView.adapter = animalAdapter

=======
        searchView = view.findViewById(R.id.searchViewGroupFiftyOne)
        progressBar = view.findViewById(R.id.progressBar)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)
        animalAdapter = AnimalAdapter(this)
        recyclerView.adapter = animalAdapter

        // Set up back button
>>>>>>> c22f9dc796c365bc15eea4d67f8d2e345d4e6626
        val arrowLeftButton: ImageButton = view.findViewById(R.id.btnArrowleft)
        arrowLeftButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

<<<<<<< HEAD
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
=======
        progressBar.visibility = View.VISIBLE
        fetchDataFromFirestore()

        searchView.setupLiveSearch(animalAdapter)
        return view
    }

    private fun fetchDataFromFirestore() {
        val collectionReference = firestore.collection("animals")

>>>>>>> c22f9dc796c365bc15eea4d67f8d2e345d4e6626
        collectionReference.whereEqualTo("anmlType", "Kambing")
            .get()
            .addOnSuccessListener { documents ->
                val animals = mutableListOf<Animal>()

                for (document in documents) {
                    val animal = document.toObject(Animal::class.java)
                    animals.add(animal)
                }

                animalAdapter.setData(animals)
<<<<<<< HEAD

                progressBar.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                // Handle failures
=======
                progressBar.visibility = View.GONE
            }
            .addOnFailureListener {
                progressBar.visibility = View.GONE
>>>>>>> c22f9dc796c365bc15eea4d67f8d2e345d4e6626
            }
    }

    override fun onLinearColumnClick(animal: Animal) {
<<<<<<< HEAD
        replacetoAnimalInfoFragment(animal)
    }

    override fun onImageAnimalClick(animal: Animal) {
        replacetoAnimalInfoFragment(animal)
    }

    private fun replacetoAnimalInfoFragment(animal: Animal) {
=======
        replaceToAnimalInfoFragment(animal)
    }

    override fun onImageAnimalClick(animal: Animal) {
        replaceToAnimalInfoFragment(animal)
    }

    private fun replaceToAnimalInfoFragment(animal: Animal) {
>>>>>>> c22f9dc796c365bc15eea4d67f8d2e345d4e6626
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
<<<<<<< HEAD
}
=======
}
>>>>>>> c22f9dc796c365bc15eea4d67f8d2e345d4e6626
