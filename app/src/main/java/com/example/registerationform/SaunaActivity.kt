package com.example.registerationform

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class SaunaActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var dateInput: TextInputEditText
    private lateinit var timeInput: TextInputEditText
    private lateinit var durationInput: AutoCompleteTextView
    private lateinit var bookButton: Button

    private lateinit var availableText: TextView
    private lateinit var inUseText: TextView
    private lateinit var totalText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sauna)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Sauna Facilities"

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        dateInput = findViewById(R.id.saunaDateInput)
        timeInput = findViewById(R.id.saunaTimeInput)
        durationInput = findViewById(R.id.saunaDurationInput)
        bookButton = findViewById(R.id.bookSaunaButton)

        availableText = findViewById(R.id.saunaAvailableTextView)
        inUseText = findViewById(R.id.saunaInUseTextView)
        totalText = findViewById(R.id.saunaTotalTextView)

        setupDropdown()
        setupPickers()
        loadSlotData()

        bookButton.setOnClickListener {
            if (validateInputs()) {
                bookSaunaSession()
            }
        }
    }

    private fun setupDropdown() {
        val durations = arrayOf("15 minutes", "30 minutes", "45 minutes", "1 hour")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, durations)
        durationInput.setAdapter(adapter)
    }

    private fun setupPickers() {
        dateInput.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(this, { _, y, m, d ->
                val selected = Calendar.getInstance()
                selected.set(y, m, d)
                val format = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
                dateInput.setText(format.format(selected.time))
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).apply {
                datePicker.minDate = cal.timeInMillis
                show()
            }
        }

        timeInput.setOnClickListener {
            val cal = Calendar.getInstance()
            TimePickerDialog(this, { _, hour, minute ->
                val selectedTime = Calendar.getInstance()
                selectedTime.set(Calendar.HOUR_OF_DAY, hour)
                selectedTime.set(Calendar.MINUTE, minute)
                val format = SimpleDateFormat("h:mm a", Locale.getDefault())
                timeInput.setText(format.format(selectedTime.time))
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
        }
    }

    private fun loadSlotData() {
        database.child("saunaSlots").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val available = snapshot.child("available").getValue(Int::class.java) ?: 0
                val inUse = snapshot.child("inUse").getValue(Int::class.java) ?: 0
                val total = snapshot.child("total").getValue(Int::class.java) ?: 0

                availableText.text = "Available: $available"
                inUseText.text = "In Use: $inUse"
                totalText.text = "Total: $total"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SaunaActivity, "Failed to load slots", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun validateInputs(): Boolean {
        return when {
            dateInput.text.isNullOrEmpty() -> {
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show(); false
            }
            timeInput.text.isNullOrEmpty() -> {
                Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show(); false
            }
            durationInput.text.isNullOrEmpty() -> {
                Toast.makeText(this, "Please select duration", Toast.LENGTH_SHORT).show(); false
            }
            else -> true
        }
    }

    private fun bookSaunaSession() {
        val date = dateInput.text.toString()
        val time = timeInput.text.toString()
        val duration = durationInput.text.toString()

        val currentUser = auth.currentUser
        val uid = currentUser?.uid ?: "unknown"

        database.child("users").child(uid).child("membership").child("name").get()
            .addOnSuccessListener { snapshot ->
                val name = snapshot.getValue(String::class.java) ?: "Guest"
            val booking = mapOf(
                "date" to date,
                "time" to time,
                "duration" to duration,
                "userId" to uid,
                "userName" to name
            )

            val reservationKey = database.child("saunaReservations").push().key
            if (reservationKey != null) {
                database.child("saunaReservations").child(reservationKey).setValue(booking)
            }

            // Update saunaSlots availability
            database.child("saunaSlots").runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    val available = currentData.child("available").getValue(Int::class.java)
                    val inUse = currentData.child("inUse").getValue(Int::class.java)

                    if (available != null && inUse != null && available > 0) {
                        currentData.child("available").value = available - 1
                        currentData.child("inUse").value = inUse + 1
                        return Transaction.success(currentData)
                    }

                    return Transaction.abort()
                }

                override fun onComplete(error: DatabaseError?, committed: Boolean, snapshot: DataSnapshot?) {
                    if (committed) {
                        Toast.makeText(this@SaunaActivity, "Sauna booked for $date at $time", Toast.LENGTH_SHORT).show()
                        clearForm()
                    } else {
                        Toast.makeText(this@SaunaActivity, "No sauna slots available", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to retrieve user name", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearForm() {
        dateInput.text?.clear()
        timeInput.text?.clear()
        durationInput.text?.clear()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
