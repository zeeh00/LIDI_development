<<<<<<< HEAD
package com.example.n4_app__inventory.fragments.animals

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
=======
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
>>>>>>> c22f9dc796c365bc15eea4d67f8d2e345d4e6626
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.n4_app__inventory.R
import com.example.n4_app__inventory.fragments.form.data.Animal
<<<<<<< HEAD

class AnimalAdapter(private val itemClickListener: OnItemClickListener) : RecyclerView.Adapter<AnimalAdapter.AnimalViewHolder>() {

    private var animalList: List<Animal> = mutableListOf()
=======
import java.util.Locale

class AnimalAdapter(private val itemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<AnimalAdapter.AnimalViewHolder>(), Filterable {

    private var animalList: List<Animal> = mutableListOf()
    private var animalListFull: List<Animal> = mutableListOf() // Store full list for filtering
>>>>>>> c22f9dc796c365bc15eea4d67f8d2e345d4e6626

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_animal, parent, false)
        return AnimalViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        val animal = animalList[position]

<<<<<<< HEAD
        // Bind data to views
=======
>>>>>>> c22f9dc796c365bc15eea4d67f8d2e345d4e6626
        holder.txtAnimalLocation.text = animal.location
        holder.txtAnimalId.text = animal.id
        holder.txtAnimalRace.text = animal.race

<<<<<<< HEAD
        // Load image using Glide library (replace with your image loading logic)
=======
>>>>>>> c22f9dc796c365bc15eea4d67f8d2e345d4e6626
        Glide.with(holder.itemView)
            .load(animal.imageUrl)
            .into(holder.imageAnimal)

<<<<<<< HEAD
        // Set click listeners
=======
>>>>>>> c22f9dc796c365bc15eea4d67f8d2e345d4e6626
        holder.linearColumnInformation.setOnClickListener {
            itemClickListener.onLinearColumnClick(animal)
        }

        holder.imageAnimal.setOnClickListener {
            itemClickListener.onImageAnimalClick(animal)
        }
    }

    override fun getItemCount(): Int {
        return animalList.size
    }

    fun setData(newList: List<Animal>) {
        animalList = newList
<<<<<<< HEAD
        notifyDataSetChanged()
    }

=======
        animalListFull = ArrayList(newList) // Keep a full copy for filtering
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = mutableListOf<Animal>()
                if (constraint.isNullOrEmpty()) {
                    filteredList.addAll(animalListFull)
                } else {
                    val filterPattern = constraint.toString().lowercase(Locale.getDefault()).trim()
                    for (animal in animalListFull) {
                        if (animal.id.lowercase(Locale.getDefault()).contains(filterPattern) ||
                            animal.location.lowercase(Locale.getDefault()).contains(filterPattern)) {
                            filteredList.add(animal)
                        }
                    }
                }
                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                animalList = results?.values as List<Animal>
                notifyDataSetChanged()
            }
        }
    }

>>>>>>> c22f9dc796c365bc15eea4d67f8d2e345d4e6626
    inner class AnimalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtAnimalLocation: TextView = itemView.findViewById(R.id.txtAnimalLocation)
        val txtAnimalId: TextView = itemView.findViewById(R.id.txtAnimalId)
        val txtAnimalRace: TextView = itemView.findViewById(R.id.txtAnimalRace)
        val imageAnimal: ImageView = itemView.findViewById(R.id.imageAnimal)
        val linearColumnInformation: LinearLayout = itemView.findViewById(R.id.linearColumnInformation)
    }

    interface OnItemClickListener {
        fun onLinearColumnClick(animal: Animal)
        fun onImageAnimalClick(animal: Animal)
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> c22f9dc796c365bc15eea4d67f8d2e345d4e6626
