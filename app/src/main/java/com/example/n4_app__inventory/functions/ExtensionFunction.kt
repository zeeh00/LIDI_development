package com.example.n4_app__inventory.functions

<<<<<<< HEAD
class ExtensionFunction {

=======
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
>>>>>>> c22f9dc796c365bc15eea4d67f8d2e345d4e6626
}