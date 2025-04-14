package com.example.registerationform

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.View

class GymSubscriptionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gym_subscription)

        // Set padding for the system bars (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Define the onClick method as public and static signature
    fun onBuyNowClicked(view: View) {
        val url = when (view.id) {
            R.id.silverBuyNowButton -> "https://www.example.com/silver-plan"
            R.id.goldBuyNowButton -> "https://www.example.com/gold-plan"
            R.id.platinumBuyNowButton -> "https://www.example.com/platinum-plan"
            else -> return
        }

        // Open the subscription link in a browser
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}
