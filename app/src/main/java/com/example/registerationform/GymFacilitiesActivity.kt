package com.example.registerationform

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GymFacilitiesActivity : AppCompatActivity() {

    private lateinit var facilitiesRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gym_facilities)  // Updated to use the correct layout

        facilitiesRecyclerView = findViewById(R.id.facilitiesRecyclerView)

        val facilities = listOf(
            FacilityItem("WiFi", "Free WiFi in the gym area", R.drawable.wifi, WifiActivity::class.java),
            FacilityItem("Parking", "Free parking available", R.drawable.parking, ParkingActivity::class.java),
            FacilityItem("Classes", "Wide variety of fitness classes", R.drawable.classes, ClassesActivity::class.java),
            FacilityItem("Locker", "Secure locker rooms", R.drawable.locker, LockerActivity::class.java),
            FacilityItem("Diet Plan", "Customized diet plans", R.drawable.dietplan, DietPlanActivity::class.java),
            FacilityItem("Swimming Pool", "Olympic-size swimming pool", R.drawable.swimming, SwimmingPoolActivity::class.java),
            FacilityItem("Sauna", "Relaxing sauna available", R.drawable.sauna, SaunaActivity::class.java),
            FacilityItem("Personal Trainer", "One-on-one personal trainer", R.drawable.trainer, GymTrainersActivity::class.java),
            FacilityItem("Yoga Studio", "Yoga sessions available", R.drawable.yoga, YogaActivity::class.java)
        )

        val adapter = FacilitiesAdapter(facilities, this)
        facilitiesRecyclerView.layoutManager = LinearLayoutManager(this)
        facilitiesRecyclerView.adapter = adapter
    }
}
