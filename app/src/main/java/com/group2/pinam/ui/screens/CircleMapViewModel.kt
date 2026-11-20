
package com.group2.pinam.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group2.pinam.data.ServiceLocator
import com.group2.pinam.model.Pin
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// This ViewModel manages the pins shown on the map.
class CircleMapViewModel : ViewModel() {

    // Stores the list of pins that the screen will display.
    private val _pins = MutableStateFlow<List<Pin>>(emptyList())

    // Allows the screen to read the pins.
    val pins: StateFlow<List<Pin>> = _pins

    // Keeps track of the current job collecting pins.
    private var pinsJob: Job? = null

    // Gets the pins belonging to the selected circle.
    fun loadPins(circleId: String) {

        // Stops the previous collection if the circle changes.
        pinsJob?.cancel()

        // Starts getting the pins in the background.
        pinsJob = viewModelScope.launch {

            // Gets the pins from the repository and keeps the list updated.
            ServiceLocator.pins.observePins(circleId).collect { pinList ->

                // Saves the latest pins so the screen can display them.
                _pins.value = pinList
            }
        }
    }
}