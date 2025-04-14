package com.example.registerationform

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager

class Index : AppCompatActivity() {

    private lateinit var imageSlider: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index) // Set the content view directly

        // Initialize the ViewPager by finding it by ID
        imageSlider = findViewById(R.id.imageSlider)

        // Set up the image slider (ViewPager)
        val images = listOf(
            R.drawable.gym1, // Replace with your images
            R.drawable.gym2,
            R.drawable.gym3,
            R.drawable.gym4
        )

        // Set up the ViewPager with a custom adapter
        val adapter = ImageSliderAdapter(images)
        imageSlider.adapter = adapter

        // Set click listener for profile button
        findViewById<ImageButton>(R.id.profileButton).setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }

        // Set click listeners for each CardView to navigate to another activity
        findViewById<androidx.cardview.widget.CardView>(R.id.gymFacilitiesCard).setOnClickListener {
            val intent = Intent(this, GymFacilitiesActivity::class.java)
            startActivity(intent)
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.gymTimingsCard).setOnClickListener {
            val intent = Intent(this, GymTimingsActivity::class.java)
            startActivity(intent)
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.gymSubscriptionCard).setOnClickListener {
            val intent = Intent(this, GymSubscriptionActivity::class.java)
            startActivity(intent)
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.gymTrainersCard).setOnClickListener {
            val intent = Intent(this, GymTrainersActivity::class.java)
            startActivity(intent)
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.contactUsCard).setOnClickListener {
            val intent = Intent(this, ContactUsActivity::class.java)
            startActivity(intent)
        }
    }

    // Adapter for the ViewPager to display images
    private inner class ImageSliderAdapter(private val images: List<Int>) : PagerAdapter() {

        override fun getCount(): Int {
            return images.size
        }

        override fun isViewFromObject(view: android.view.View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun instantiateItem(container: android.view.ViewGroup, position: Int): Any {
            val imageView = ImageView(container.context)
            imageView.setImageResource(images[position])
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            container.addView(imageView)
            return imageView
        }

        override fun destroyItem(container: android.view.ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as android.view.View)
        }
    }
}