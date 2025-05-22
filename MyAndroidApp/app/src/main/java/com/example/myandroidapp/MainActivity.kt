package com.example.myandroidapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var editTextTimePerFrame: EditText
    private lateinit var spinnerTimeUnit: Spinner
    private lateinit var editTextTotalFrames: EditText
    private lateinit var buttonCalculate: Button
    private lateinit var textViewEstimatedTime: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextTimePerFrame = findViewById(R.id.editTextTimePerFrame)
        spinnerTimeUnit = findViewById(R.id.spinnerTimeUnit)
        editTextTotalFrames = findViewById(R.id.editTextTotalFrames)
        buttonCalculate = findViewById(R.id.buttonCalculate)
        textViewEstimatedTime = findViewById(R.id.textViewEstimatedTime)

        // Setup Spinner
        // The R.array.time_units_array is defined in strings.xml
        ArrayAdapter.createFromResource(
            this,
            R.array.time_units_array, 
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerTimeUnit.adapter = adapter
        }

        buttonCalculate.setOnClickListener {
            calculateEstimatedTime()
        }
    }

    private fun calculateEstimatedTime() {
        val timePerFrameStr = editTextTimePerFrame.text.toString()
        val totalFramesStr = editTextTotalFrames.text.toString()
        val selectedUnit = spinnerTimeUnit.selectedItem.toString()

        if (timePerFrameStr.isBlank() || totalFramesStr.isBlank()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            textViewEstimatedTime.text = "Estimated Total Render Time: " // Reset on error
            return
        }

        try {
            val timePerFrame = timePerFrameStr.toDouble()
            val totalFrames = totalFramesStr.toInt()

            if (timePerFrame <= 0 || totalFrames <= 0) {
                Toast.makeText(this, "Inputs must be positive numbers", Toast.LENGTH_SHORT).show()
                textViewEstimatedTime.text = "Estimated Total Render Time: " // Reset on error
                return
            }

            val timePerFrameInSeconds = when (selectedUnit) {
                "Seconds" -> timePerFrame
                "Minutes" -> timePerFrame * 60
                "Hours" -> timePerFrame * 3600
                else -> {
                    Toast.makeText(this, "Invalid time unit selected", Toast.LENGTH_SHORT).show()
                    textViewEstimatedTime.text = "Estimated Total Render Time: " // Reset on error
                    return
                }
            }

            val totalTimeInSeconds = timePerFrameInSeconds * totalFrames

            val hours = (totalTimeInSeconds / 3600).toInt()
            val minutes = ((totalTimeInSeconds % 3600) / 60).toInt()
            val seconds = (totalTimeInSeconds % 60).toInt()

            val formattedTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
            
            // Escaping the $ for shell interpretation in heredoc
            textViewEstimatedTime.text = "Estimated Total Render Time: $formattedTime"

        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Invalid number format. Please enter valid numbers.", Toast.LENGTH_LONG).show()
            textViewEstimatedTime.text = "Estimated Total Render Time: " // Reset on error
        }
    }
}
