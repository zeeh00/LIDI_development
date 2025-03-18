package com.example.n4_app__inventory.functions

import AnimalAdapter
import androidx.appcompat.widget.SearchView

fun SearchView.setupLiveSearch(animalAdapter: AnimalAdapter) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            animalAdapter.filter.filter(newText) // Apply live filtering
            return true
        }
    })
}