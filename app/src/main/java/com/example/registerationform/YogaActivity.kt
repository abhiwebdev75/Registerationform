package com.example.registerationform
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class YogaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_yoga)  // Link to the layout file

        // Find the TextViews in the layout
        val yogaInfoTextView: TextView = findViewById(R.id.yogaInfoTextView)
        val yogaTimingsTextView: TextView = findViewById(R.id.yogaTimingsTextView)

        // Set the content for the activity
        yogaInfoTextView.text = """
            Welcome to Yoga Auditorium!
            
            Our Yoga Auditorium is a peaceful and serene space designed to offer the perfect environment for Yoga enthusiasts of all levels. 
            Whether you are a beginner or an expert, we have sessions for everyone.
            
            The auditorium is equipped with high-quality mats, relaxing music, and expert instructors to guide you through your yoga journey.
        """.trimIndent()

        yogaTimingsTextView.text = """
            Yoga Timings:
            
            Morning Session: 6:00 AM - 7:30 AM
            Evening Session: 6:00 PM - 7:30 PM
        """.trimIndent()
    }
}
