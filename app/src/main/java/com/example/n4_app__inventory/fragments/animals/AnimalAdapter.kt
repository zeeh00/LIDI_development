import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.n4_app__inventory.R
import com.example.n4_app__inventory.fragments.form.data.Animal
import java.util.Locale

class AnimalAdapter(private val itemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<AnimalAdapter.AnimalViewHolder>(), Filterable {

    private var animalList: List<Animal> = mutableListOf()
    private var animalListFull: List<Animal> = mutableListOf() // Store full list for filtering

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_animal, parent, false)
        return AnimalViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        val animal = animalList[position]

        holder.txtAnimalLocation.text = animal.location
        holder.txtAnimalId.text = animal.id
        holder.txtAnimalRace.text = animal.race

        Glide.with(holder.itemView)
            .load(animal.imageUrl)
            .into(holder.imageAnimal)

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
                        // Add race to the search conditions and include other relevant fields
                        if (animal.id.lowercase(Locale.getDefault()).contains(filterPattern) ||
                            animal.location.lowercase(Locale.getDefault()).contains(filterPattern) ||
                            animal.race.lowercase(Locale.getDefault()).contains(filterPattern) ||
                            // Add more fields that might be useful for searching
                            animal.anmlType?.lowercase(Locale.getDefault())?.contains(filterPattern) == true ||
                            animal.breedType?.lowercase(Locale.getDefault())?.contains(filterPattern) == true ||
                            animal.anmlPhysStat?.lowercase(Locale.getDefault())?.contains(filterPattern) == true ||
                            animal.sex?.lowercase(Locale.getDefault())?.contains(filterPattern) == true) {
                            filteredList.add(animal)
                        }
                    }
                }
                val results = FilterResults()
                results.values = filteredList
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                animalList = results?.values as List<Animal>
                notifyDataSetChanged()
            }
        }
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