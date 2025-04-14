package com.example.registerationform

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class SwimmingPoolActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swimming_pool)  // Ensure you create this XML layout

        // You can update the content of the activity as needed
        val swimmingPoolInfoTextView: TextView = findViewById(R.id.swimmingPoolInfoTextView)

        // Set the content
        swimmingPoolInfoTextView.text = """
            Welcome to the Swimming Pool!

            Our swimming pool is an Olympic-size pool perfect for swimmers of all levels. 
            Whether you're looking to swim laps or simply relax, our pool is the ideal place for fitness and leisure.
            
            Timings: 6:00 AM - 9:00 PM
            Special Features:
            - Heated Pool
            - Poolside Lounge
            - Poolside Snacks & Drinks
        """.trimIndent()
    }
}
