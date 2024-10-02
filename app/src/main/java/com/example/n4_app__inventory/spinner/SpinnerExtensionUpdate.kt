package com.example.n4_app__inventory.spinner


import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import com.bumptech.glide.Glide
import com.example.n4_app__inventory.R
import com.google.firebase.storage.FirebaseStorage

fun Spinner.setupAnimalTypeSpinner(anmlType: String?, animalTypes: List<String>, onItemSelected: (String?) -> Unit) {
    val dropdownItems = mutableListOf<String>()

    for (animal in animalTypes) {
        if (animal != anmlType) {
            dropdownItems.add(animal)
        }
    }

    if (anmlType != null && !dropdownItems.contains(anmlType)) {
        // Add the fetched animal type at the correct position (e.g., as the first item)
        dropdownItems.add(0, anmlType)
    }

    val adapterAnml = ArrayAdapter(context, android.R.layout.simple_spinner_item, dropdownItems)
    adapterAnml.setDropDownViewResource(R.layout.custom_spinner)
    adapter = adapterAnml

    if (anmlType != null) {
        setSelection(adapterAnml.getPosition(anmlType))
    }

    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
            val selectedItem = dropdownItems[position]
            onItemSelected(selectedItem)
            // Do something with the selected item if needed
        }

        override fun onNothingSelected(parentView: AdapterView<*>) {
            // Do nothing
        }
    }
}

fun Spinner.setupOriginTypeSpinner(originType: String?, originTypes: List<String>, onItemSelected: (String?) -> Unit) {
    val dropdownItems = mutableListOf<String>()

    for (origin in originTypes) {
        if (origin != originType) {
            dropdownItems.add(origin)
        }
    }

    if (originType != null && !dropdownItems.contains(originType)) {
        // Add the fetched origin type at the correct position (e.g., as the first item)
        dropdownItems.add(0, originType)
    }

    val adapterOrigin = ArrayAdapter(context, android.R.layout.simple_spinner_item, dropdownItems)
    adapterOrigin.setDropDownViewResource(R.layout.custom_spinner)
    adapter = adapterOrigin

    if (originType != null) {
        setSelection(adapterOrigin.getPosition(originType))
    }

    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
            val selectedItem = dropdownItems[position]
            onItemSelected(selectedItem)
            // Do something with the selected item if needed
        }

        override fun onNothingSelected(parentView: AdapterView<*>) {
            // Do nothing
        }
    }
}

fun Spinner.setupLocationTypeSpinner(locType: String?, locTypes: List<String>, onItemSelected: (String?) -> Unit) {
    val dropdownItems = mutableListOf<String>()

    for (location in locTypes) {
        if (location != locType) {
            dropdownItems.add(location)
        }
    }

    if (locType != null && !dropdownItems.contains(locType)) {
        // Add the fetched location type at the correct position (e.g., as the first item)
        dropdownItems.add(0, locType)
    }

    val adapterLoc = ArrayAdapter(context, android.R.layout.simple_spinner_item, dropdownItems)
    adapterLoc.setDropDownViewResource(R.layout.custom_spinner)
    adapter = adapterLoc

    if (locType != null) {
        setSelection(adapterLoc.getPosition(locType))
    }

    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
            val selectedItem = dropdownItems[position]
            onItemSelected(selectedItem)
            // Do something with the selected item if needed
        }

        override fun onNothingSelected(parentView: AdapterView<*>) {
            // Do nothing
        }
    }
}

fun Spinner.setupSexTypeSpinner(sexType: String?, sexTypes: List<String>, onItemSelected: (String?) -> Unit) {
    val dropdownItems = mutableListOf<String>()

    for (sex in sexTypes) {
        if (sex != sexType) {
            dropdownItems.add(sex)
        }
    }

    if (sexType != null && !dropdownItems.contains(sexType)) {
        // Add the fetched sex type at the correct position (e.g., as the first item)
        dropdownItems.add(0, sexType)
    }

    val adapterSex = ArrayAdapter(context, android.R.layout.simple_spinner_item, dropdownItems)
    adapterSex.setDropDownViewResource(R.layout.custom_spinner)
    adapter = adapterSex

    if (sexType != null) {
        setSelection(adapterSex.getPosition(sexType))
    }

    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
            val selectedItem = dropdownItems[position]
            onItemSelected(selectedItem)
            // Do something with the selected item if needed
        }

        override fun onNothingSelected(parentView: AdapterView<*>) {
            // Do nothing
        }
    }
}

fun Spinner.setupMarriageTypeSpinner(marriageType: String?, marriageTypes: List<String>, onItemSelected: (String?) -> Unit) {
    val dropdownItems = mutableListOf<String>()

    for (marriage in marriageTypes) {
        if (marriage != marriageType) {
            dropdownItems.add(marriage)
        }
    }

    if (marriageType != null && !dropdownItems.contains(marriageType)) {
        // Add the fetched sex type at the correct position (e.g., as the first item)
        dropdownItems.add(0, marriageType)
    }

    val adapterMarriage = ArrayAdapter(context, android.R.layout.simple_spinner_item, dropdownItems)
    adapterMarriage.setDropDownViewResource(R.layout.custom_spinner)
    adapter = adapterMarriage

    if (marriageType != null) {
        setSelection(adapterMarriage.getPosition(marriageType))
    }

    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
            val selectedItem = dropdownItems[position]
            onItemSelected(selectedItem)
            // Do something with the selected item if needed
        }

        override fun onNothingSelected(parentView: AdapterView<*>) {
            // Do nothing
        }
    }
}