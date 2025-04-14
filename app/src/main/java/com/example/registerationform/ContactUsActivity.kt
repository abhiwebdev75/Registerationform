package com.example.registerationform

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ContactUsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_contact_us)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    // Function to open Instagram link
    fun openInstagram(view: android.view.View) {
        val instagramUrl = "https://www.instagram.com/fithitgym_chd" // Replace with your Instagram link
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(instagramUrl))
        startActivity(intent)
    }

    // Function to open Facebook link
    fun openFacebook(view: android.view.View) {
        val facebookUrl = "https://www.facebook.com/fithitgym_chd" // Replace with your Facebook link
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl))
        startActivity(intent)
    }

    // Function to open YouTube link
    fun openYouTube(view: android.view.View) {
        val youtubeUrl = "https://www.youtube.com/fithitgym_chd" // Replace with your YouTube link
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl))
        startActivity(intent)
    }
}}