package com.example.registerationform

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LockerActivity : AppCompatActivity() {

    private lateinit var lockerNumberInput: TextInputEditText
    private lateinit var durationInput: TextInputEditText
    private lateinit var bookLockerButton: Button

    private lateinit var availableTextView: TextView
    private lateinit var occupiedTextView: TextView
    private lateinit var totalTextView: TextView

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_locker)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Locker Room"

        // Initialize views
        lockerNumberInput = findViewById(R.id.lockerNumberInput)
        durationInput = findViewById(R.id.durationInput)
        bookLockerButton = findViewById(R.id.bookLockerButton)

        availableTextView = findViewById(R.id.lockerAvailableText)
        occupiedTextView = findViewById(R.id.lockerOccupiedText)
        totalTextView = findViewById(R.id.lockerTotalText)

        // Firebase references
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Listen to locker slot updates
        database.child("lockerSlots").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val available = snapshot.child("available").getValue(Int::class.java) ?: 0
                val occupied = snapshot.child("occupied").getValue(Int::class.java) ?: 0
                val total = snapshot.child("total").getValue(Int::class.java) ?: 0

                availableTextView.text = available.toString()
                occupiedTextView.text = occupied.toString()
                totalTextView.text = total.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LockerActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })

        bookLockerButton.setOnClickListener {
            if (lockerNumberInput.text.isNullOrEmpty() || durationInput.text.isNullOrEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val uid = auth.currentUser?.uid ?: "unknown"

            // Get username and proceed to save
            database.child("users").child(uid).child("name").get().addOnSuccessListener { snapshot ->
                val name = snapshot.getValue(String::class.java) ?: "Guest"

                val reservation = mapOf(
                    "lockerNumber" to lockerNumberInput.text.toString(),
                    "duration" to durationInput.text.toString(),
                    "userId" to uid,
                    "userName" to name
                )

                // Push reservation and update slot count
                database.child("lockerReservations").push().setValue(reservation)
                updateSlotCount()

            }.addOnFailureListener {
                Toast.makeText(this, "Failed to get username", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateSlotCount() {
        val slotRef = database.child("lockerSlots")
        slotRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val available = currentData.child("available").getValue(Int::class.java) ?: return Transaction.success(currentData)
                val occupied = currentData.child("occupied").getValue(Int::class.java) ?: 0

                if (available > 0) {
                    currentData.child("available").value = available - 1
                    currentData.child("occupied").value = occupied + 1
                }
                return Transaction.success(currentData)
            }

            override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
                if (committed) {
                    Toast.makeText(this@LockerActivity, "Locker booked!", Toast.LENGTH_SHORT).show()
                    clearForm()
                } else {
                    Toast.makeText(this@LockerActivity, "Failed to book locker", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun clearForm() {
        lockerNumberInput.text?.clear()
        durationInput.text?.clear()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
