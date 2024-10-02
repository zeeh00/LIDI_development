package com.example.n4_app__inventory.fragments.inventory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.viewpager2.widget.ViewPager2
import com.example.n4_app__inventory.R
import com.example.n4_app__inventory.databinding.FragmentInventoryBinding
import com.example.n4_app__inventory.fragments.animals.DombaFragment
import com.example.n4_app__inventory.fragments.animals.KambingFragment
import com.example.n4_app__inventory.fragments.animals.SapiFragment
import com.example.n4_app__inventory.fragments.inventory.image.AnimalType
import com.example.n4_app__inventory.fragments.inventory.image.ImageSliderInfo
import com.example.n4_app__inventory.fragments.inventory.image.ImageType
import com.example.n4_app__inventory.fragments.inventory.image.InfinitePagerAdapter
import com.example.n4_app__inventory.fragments.inventory.image.LocationType
import com.example.n4_app__inventory.fragments.location.KbnButFragment
import com.example.n4_app__inventory.fragments.location.KbnMarFragment
import com.example.n4_app__inventory.fragments.location.KbnTobFragment
import com.example.n4_app__inventory.fragments.location.PksPabFragment
import com.example.n4_app__inventory.fragments.profile.ProfileFragment

class InventoryFragment : Fragment() {

    private lateinit var binding: FragmentInventoryBinding
    private lateinit var viewPager2: ViewPager2
    private lateinit var viewPagerFarmLoc: ViewPager2
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInventoryBinding.inflate(inflater, container, false)
        val view = binding.root

        viewPager2 = view.findViewById(R.id.imageSliderAnimals)
        viewPagerFarmLoc = view.findViewById(R.id.imageSliderFarmLoc)

        // Set up the ViewPager2 instances
        val animalSliderInfo = ImageSliderInfo(
            arrayOf(R.drawable.kambing, R.drawable.domba, R.drawable.sapi),
            onImageClickListener = { imageType ->
                // Handle click event and navigate to the appropriate fragment
                when (imageType) {
                    is ImageType.Animal -> {
                        val fragment = createFragmentForAnimalType(imageType.animalType)
                        replaceFragment(fragment)
                    }
                    is ImageType.Location -> {
                        // Handle location click if needed
                    }
                }
            }
        )

        val locationSliderInfo = ImageSliderInfo(
            arrayOf(R.drawable.kbn_mar, R.drawable.kbn_but, R.drawable.kbn_tob, R.drawable.pks_pab),
            onImageClickListener = { imageType ->
                // Handle click event and navigate to the appropriate fragment
                when (imageType) {
                    is ImageType.Animal -> {
                        // Handle animal click if needed
                    }
                    is ImageType.Location -> {
                        val fragment = createFragmentForLocationType(imageType.locationType)
                        replaceFragment(fragment)
                    }
                }
            }
        )

        val arrowLeftButton: ImageButton = view.findViewById(R.id.btnArrowleft)
        arrowLeftButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.imgProfile.setOnClickListener {
            val profileFragment = ProfileFragment()
            replaceFragment(profileFragment)
        }


        setupViewPager(viewPager2, listOf(animalSliderInfo))
        setupViewPager(viewPagerFarmLoc, listOf(locationSliderInfo))

        return view
    }

    private fun createFragmentForAnimalType(animalType: AnimalType): Fragment {
        return when (animalType) {
            AnimalType.KAMBING -> KambingFragment.newInstance()
            AnimalType.DOMBA -> DombaFragment.newInstance()
            AnimalType.SAPI -> SapiFragment.newInstance()
        }
    }

    private fun createFragmentForLocationType(locationType: LocationType): Fragment {
        return when (locationType) {
            LocationType.MAR -> KbnMarFragment.newInstance()
            LocationType.BUT -> KbnButFragment.newInstance()
            LocationType.TOB -> KbnTobFragment.newInstance()
            LocationType.PAB -> PksPabFragment.newInstance()
        }
    }

    private fun setupViewPager(
        viewPager: ViewPager2,
        imageSliderInfos: List<ImageSliderInfo>
    ) {
        val adapters = imageSliderInfos.map { info ->
            InfinitePagerAdapter(
                listOf(info.images),
                onAnimalClickListener = { animalType ->
                    // Handle click event for animals
                    val fragment = createFragmentForAnimalType(animalType)
                    replaceFragment(fragment)
                },
                onLocationClickListener = { locationType ->
                    // Handle click event for locations
                    val fragment = createFragmentForLocationType(locationType)
                    replaceFragment(fragment)
                }
            )
        }

        viewPager.adapter = adapters.first() // Set the first adapter initially

        startAutoScroll(viewPager)
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun startAutoScroll(viewPager: ViewPager2) {
        val handler = android.os.Handler()
        val runnable = object : Runnable {
            override fun run() {
                val currentItem = viewPager.currentItem
                viewPager.setCurrentItem(currentItem + 1, true)
                handler.postDelayed(this, AUTO_SCROLL_INTERVAL)
            }
        }

        handler.postDelayed(runnable, AUTO_SCROLL_INTERVAL)
    }

    companion object {
        private const val AUTO_SCROLL_INTERVAL = 3000L
    }
}