package com.example.n4_app__inventory.fragments.animals

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.n4_app__inventory.R
import com.example.n4_app__inventory.fragments.form.data.Animal

class AnimalAdapter(private val itemClickListener: OnItemClickListener) : RecyclerView.Adapter<AnimalAdapter.AnimalViewHolder>() {

    private var animalList: List<Animal> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_animal, parent, false)
        return AnimalViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        val animal = animalList[position]

        // Bind data to views
        holder.txtAnimalLocation.text = animal.location
        holder.txtAnimalId.text = animal.id
        holder.txtAnimalRace.text = animal.race

        // Load image using Glide library (replace with your image loading logic)
        Glide.with(holder.itemView)
            .load(animal.imageUrl)
            .into(holder.imageAnimal)

        // Set click listeners
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
        notifyDataSetChanged()
    }

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
}