package com.example.registerationform

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ClassesActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classes)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Fitness Classes"

        // Firebase database reference
        database = FirebaseDatabase.getInstance().getReference("Bookings")

        val bookButtons = listOf(
            findViewById<Button>(R.id.bookButton1),
            findViewById<Button>(R.id.bookButton2),
            findViewById<Button>(R.id.bookButton3),
            findViewById<Button>(R.id.bookButton4),
            findViewById<Button>(R.id.bookButton5),
            findViewById<Button>(R.id.bookButton6),
            findViewById<Button>(R.id.bookButton7),
            findViewById<Button>(R.id.bookButton8)
        )

        val classNames = listOf(
            "Morning Yoga", "HIIT Blast", "Zumba Party", "Spin Class",
            "Pilates Core", "CrossFit", "Kickboxing", "Aqua Fit"
        )

        bookButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                val className = classNames[index]
                saveBookingToFirebase(className)
            }
        }
    }

    private fun saveBookingToFirebase(className: String) {
        val bookingId = database.push().key!!
        val booking = mapOf(
            "id" to bookingId,
            "className" to className,
            "timestamp" to System.currentTimeMillis()
        )

        database.child(bookingId).setValue(booking)
            .addOnSuccessListener {
                Toast.makeText(this, "Booked $className", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to book class", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
