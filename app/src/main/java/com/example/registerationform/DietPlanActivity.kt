package com.example.registerationform

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DietPlanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diet_plan)  // Link to the layout file

        // Find the TextViews and ImageViews in the layout
        val dietPlanTextView: TextView = findViewById(R.id.dietPlanTextView)
        val mondayImageView: ImageView = findViewById(R.id.mondayImageView)
        val tuesdayImageView: ImageView = findViewById(R.id.tuesdayImageView)
        val wednesdayImageView: ImageView = findViewById(R.id.wednesdayImageView)
        val thursdayImageView: ImageView = findViewById(R.id.thursdayImageView)
        val fridayImageView: ImageView = findViewById(R.id.fridayImageView)
        val saturdayImageView: ImageView = findViewById(R.id.saturdayImageView)
        val sundayImageView: ImageView = findViewById(R.id.sundayImageView)

        // Set the description for the diet plan
        dietPlanTextView.text = """
            Welcome to the Weekly Diet Plan!
            
            This plan is designed to help you maintain a healthy and balanced lifestyle.
            Each day, you'll have a unique meal plan that provides all the nutrients your body needs.
            Follow it diligently for optimal results!
            
            Healthy snacks
            
            Mixed nuts
            Apple and peanut butter
            Hummus and pita bread
            Khakhras
            Makhana
            Smoothie
            Roasted Sweet potato chips
            Boiled eggs
            Boiled pulses
            Protein Muesli
            Protein Cookies
            Protein Chips
            Protein Bars
        """.trimIndent()

        // Set images for each day of the week (Make sure the images are in the 'drawable' folder)
        mondayImageView.setImageResource(R.drawable.monday_diet)  // Replace with your actual images
        tuesdayImageView.setImageResource(R.drawable.tuesday_diet)
        wednesdayImageView.setImageResource(R.drawable.wednesday_diet)
        thursdayImageView.setImageResource(R.drawable.thursday_diet)
        fridayImageView.setImageResource(R.drawable.friday_diet)
        saturdayImageView.setImageResource(R.drawable.saturday_diet)
        sundayImageView.setImageResource(R.drawable.sunday_diet)
    }
}
