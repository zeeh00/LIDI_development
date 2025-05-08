package com.example.n4_app__inventory.functions

import android.view.View
import android.widget.Filterable
import androidx.appcompat.widget.SearchView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Extension function to add live search capability to a SearchView.
 * Includes debounce functionality to prevent excessive filtering operations.
 *
 * @param adapter Any adapter that implements the Filterable interface
 * @param delayMillis Delay in milliseconds before triggering the filter (default: 300ms)
 */
fun <T : Filterable> SearchView.setupLiveSearch(adapter: T, delayMillis: Long = 300) {
    var searchJob: Job? = null

    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            // We handle filtering in onQueryTextChange, so no need to filter here
            return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            // Cancel previous job if it's still active
            searchJob?.cancel()

            // Create a new coroutine for debouncing
            searchJob = CoroutineScope(Dispatchers.Main).launch {
                delay(delayMillis)
                adapter.filter.filter(newText ?: "")
            }
            return true
        }
    })

    // Clean up coroutine when SearchView is detached
    this.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) { /* No action needed */ }

        override fun onViewDetachedFromWindow(v: View) {
            searchJob?.cancel()
        }
    })
}