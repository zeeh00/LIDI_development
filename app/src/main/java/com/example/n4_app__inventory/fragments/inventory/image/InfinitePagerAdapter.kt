package com.example.n4_app__inventory.fragments.inventory.image

import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.n4_app__inventory.R

enum class AnimalType { KAMBING, DOMBA, SAPI }
enum class LocationType { MAR, BUT, TOB, PAB }

sealed class ImageType {
    data class Animal(val animalType: AnimalType) : ImageType()
    data class Location(val locationType: LocationType) : ImageType()
}

data class ImageSliderInfo(
    val images: Array<Int>,
    val onImageClickListener: (imageType: ImageType) -> Unit
)

class InfinitePagerAdapter(
    private val imageSets: List<Array<Int>>,
    private val onAnimalClickListener: (animalType: AnimalType) -> Unit,
    private val onLocationClickListener: (locationType: LocationType) -> Unit
) : RecyclerView.Adapter<InfinitePagerAdapter.ViewHolder>() {

    // Maps for resource ID to AnimalType and LocationType
    private val animalTypeMap = mapOf(
        R.drawable.kambing to AnimalType.KAMBING,
        R.drawable.domba to AnimalType.DOMBA,
        R.drawable.sapi to AnimalType.SAPI
    )

    private val locationTypeMap = mapOf(
        R.drawable.kbn_mar to LocationType.MAR,
        R.drawable.kbn_but to LocationType.BUT,
        R.drawable.kbn_tob to LocationType.TOB,
        R.drawable.pks_pab to LocationType.PAB
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(createImageView(parent))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (imageSets.isEmpty()) return

        val setIndex = position % imageSets.size
        val actualPosition = position % imageSets[setIndex].size
        val resourceId = imageSets[setIndex][actualPosition]

        holder.bind(resourceId)
        holder.itemView.setOnClickListener { handleImageClick(resourceId) }
    }

    override fun getItemCount(): Int = if (imageSets.isNotEmpty()) Int.MAX_VALUE else 0

    private fun createImageView(parent: ViewGroup): ImageView {
        return ImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    private fun handleImageClick(resourceId: Int) {
        animalTypeMap[resourceId]?.let {
            onAnimalClickListener(it)
            return
        }

        locationTypeMap[resourceId]?.let {
            onLocationClickListener(it)
            return
        }

        throw IllegalArgumentException("Unknown image type for resource ID: $resourceId")
    }

    inner class ViewHolder(private val imageView: ImageView) : RecyclerView.ViewHolder(imageView) {
        fun bind(imageResId: Int) {
            imageView.setImageResource(imageResId)
        }
    }
}
