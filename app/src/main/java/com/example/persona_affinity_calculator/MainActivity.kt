package com.example.persona_affinity_calculator

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : AppCompatActivity() {

    private lateinit var displayText: TextView
    private lateinit var resultText: TextView
    private val selectedTraits = mutableListOf<String>()

    // Declare personaAffinities, but initialize it lazily or in onCreate
    // Using 'lateinit' is not suitable for 'val' or for a Map that needs context.
    // The best approach here is to make it a 'lateinit var' if you plan to reassign it,
    // or initialize it in onCreate. Given it's a constant, initialize it in onCreate.
    private lateinit var personaAffinities: Map<String, List<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Optional: Keep splash screen on-screen for longer if needed
        splashScreen.setKeepOnScreenCondition { false }

        setContentView(R.layout.activity_main)

        // Initialize personaAffinities HERE, inside onCreate,
        // after the Activity has a valid Context.
        personaAffinities = mapOf(
            getString(R.string.char_joker) to listOf(getString(R.string.trait_leader), getString(R.string.trait_smart)),
            getString(R.string.char_ryuji) to listOf(getString(R.string.trait_loyal), getString(R.string.trait_strong)),
            getString(R.string.char_ann) to listOf(getString(R.string.trait_strong), getString(R.string.trait_artistic)),
            getString(R.string.char_yusuke) to listOf(getString(R.string.trait_artistic), getString(R.string.trait_curious)),
            getString(R.string.char_makoto) to listOf(getString(R.string.trait_smart), getString(R.string.trait_leader)),
            getString(R.string.char_futaba) to listOf(getString(R.string.trait_curious), getString(R.string.trait_smart))
        )

        initializeViews()
        setupButtons()
    }

    private fun initializeViews() {
        displayText = findViewById(R.id.displayText)
        resultText = findViewById(R.id.resultText)
        displayText.text = getString(R.string.selected_traits_none_default) // This is correctly placed
    }

    private fun setupButtons() {
        // Map button IDs to their corresponding trait string resources
        val traitButtonMappings = mapOf(
            R.id.btnTrait1 to R.string.trait_leader,
            R.id.btnTrait2 to R.string.trait_loyal,
            R.id.btnTrait3 to R.string.trait_artistic,
            R.id.btnTrait4 to R.string.trait_smart,
            R.id.btnTrait5 to R.string.trait_curious,
            R.id.btnTrait6 to R.string.trait_strong
        )

        // Dynamically set up trait buttons using the map
        for ((buttonId, traitStringResId) in traitButtonMappings) {
            val traitName = getString(traitStringResId) // This is correctly placed
            findViewById<Button>(buttonId).apply {
                text = traitName
                setOnClickListener { addTrait(traitName) }
            }
        }

        // Action buttons
        findViewById<Button>(R.id.btnCalculate).setOnClickListener {
            calculateAffinity()
        }

        findViewById<Button>(R.id.btnClear).setOnClickListener {
            clearTraits()
        }

        findViewById<Button>(R.id.btnInfo).setOnClickListener {
            showInfoDialog()
        }
    }

    private fun addTrait(trait: String) {
        if (!selectedTraits.contains(trait)) {
            selectedTraits.add(trait)
            updateDisplay()
        }
    }

    private fun updateDisplay() {
        if (selectedTraits.isEmpty()) {
            displayText.text = getString(R.string.selected_traits_none_default)
        } else {
            displayText.text = getString(R.string.selected_traits_format, selectedTraits.joinToString(", "))
        }
    }

    private fun calculateAffinity() {
        if (selectedTraits.isEmpty()) {
            resultText.text = getString(R.string.no_traits_selected_message)
            return
        }

        val scores = mutableMapOf<String, Int>()

        // Calculate affinity scores
        for ((character, traits) in personaAffinities) { // personaAffinities is now initialized
            var score = 0
            for (selectedTrait in selectedTraits) {
                if (traits.contains(selectedTrait)) {
                    score++
                }
            }
            scores[character] = score
        }

        // Find the character with highest affinity
        val topCharacterEntry = scores.maxByOrNull { it.value }
        val topCharacterName = topCharacterEntry?.key ?: getString(R.string.no_match_found)
        val matchingTraitsCount = topCharacterEntry?.value ?: 0

        val percentage = if (selectedTraits.isNotEmpty()) {
            (matchingTraitsCount * 100) / selectedTraits.size
        } else {
            0
        }

        resultText.text = getString(R.string.result_text_format, topCharacterName, percentage, matchingTraitsCount, selectedTraits.size)
    }

    private fun clearTraits() {
        selectedTraits.clear()
        updateDisplay()
        resultText.text = ""
    }

    private fun showInfoDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_title_info)
            .setMessage(R.string.dialog_message_info)
            .setPositiveButton(R.string.dialog_ok_button) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}