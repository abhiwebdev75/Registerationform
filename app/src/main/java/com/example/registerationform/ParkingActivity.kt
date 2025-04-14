package com.example.registerationform

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class ParkingActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var availableSlotsTextView: TextView
    private lateinit var occupiedSlotsTextView: TextView
    private lateinit var totalSlotsTextView: TextView

    private lateinit var vehicleNumberEditText: EditText
    private lateinit var parkingTypeSpinner: Spinner
    private lateinit var parkingDateInput: EditText
    private lateinit var parkingTimeInput: EditText
    private lateinit var parkingDurationInput: AutoCompleteTextView
    private lateinit var reserveParkingButton: Button

    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking)

        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        // Bind views
        availableSlotsTextView = findViewById(R.id.availableSlotsTextView)
        occupiedSlotsTextView = findViewById(R.id.occupiedSlotsTextView)
        totalSlotsTextView = findViewById(R.id.totalSlotsTextView)
        vehicleNumberEditText = findViewById(R.id.vehicleNumberEditText)
        parkingTypeSpinner = findViewById(R.id.parkingTypeSpinner)
        parkingDateInput = findViewById(R.id.parkingDateInput)
        parkingTimeInput = findViewById(R.id.parkingTimeInput)
        parkingDurationInput = findViewById(R.id.parkingDurationInput)
        reserveParkingButton = findViewById(R.id.reserveParkingButton)

        setupSpinner()
        setupDurationDropdown()
        setupDateInput()
        setupTimeInput()
        loadSlotData()

        reserveParkingButton.setOnClickListener {
            reserveParkingSlot()
        }
    }

    private fun setupSpinner() {
        val types = listOf("2 wheeler", "4 wheeler", "EV Charging")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, types)
        parkingTypeSpinner.adapter = adapter
    }

    private fun setupDurationDropdown() {
        val durations = listOf("30 mins", "1 hour", "2 hours", "3 hours", "All day")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, durations)
        parkingDurationInput.setAdapter(adapter)
    }

    private fun setupDateInput() {
        parkingDateInput.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, y, m, d ->
                parkingDateInput.setText("$d/${m + 1}/$y")
            }, year, month, day).show()
        }
    }

    private fun setupTimeInput() {
        parkingTimeInput.setOnClickListener {
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(this, { _, h, m ->
                parkingTimeInput.setText(String.format("%02d:%02d", h, m))
            }, hour, minute, true).show()
        }
    }

    private fun loadSlotData() {
        database.child("parkingSlots").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val available = snapshot.child("available").getValue(Int::class.java) ?: 0
                val occupied = snapshot.child("occupied").getValue(Int::class.java) ?: 0
                val total = snapshot.child("total").getValue(Int::class.java) ?: 0

                availableSlotsTextView.text = "$available Available"
                occupiedSlotsTextView.text = "$occupied Occupied"
                totalSlotsTextView.text = "$total Total"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ParkingActivity, "Failed to load slot data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun reserveParkingSlot() {
        val vehicleNumber = vehicleNumberEditText.text.toString().trim()
        val parkingType = parkingTypeSpinner.selectedItem?.toString() ?: ""
        val date = parkingDateInput.text.toString()
        val time = parkingTimeInput.text.toString()
        val duration = parkingDurationInput.text.toString()

        if (vehicleNumber.isEmpty() || date.isEmpty() || time.isEmpty() || duration.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = currentUser.uid

        // Fetch user name from "users" node
        database.child("users").child(uid).child("name").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.getValue(String::class.java) ?: "Unknown"

                val reservation = mapOf(
                    "uid" to uid,
                    "name" to name,
                    "vehicleNumber" to vehicleNumber,
                    "parkingType" to parkingType,
                    "date" to date,
                    "time" to time,
                    "duration" to duration
                )

                val reservationKey = database.child("reservations").push().key
                if (reservationKey != null) {
                    database.child("reservations").child(reservationKey).setValue(reservation)
                }

                // Update parking slots
                val slotsRef = database.child("parkingSlots")
                slotsRef.runTransaction(object : Transaction.Handler {
                    override fun doTransaction(currentData: MutableData): Transaction.Result {
                        val available = currentData.child("available").getValue(Int::class.java)
                        val occupied = currentData.child("occupied").getValue(Int::class.java)

                        if (available != null && occupied != null && available > 0) {
                            currentData.child("available").value = available - 1
                            currentData.child("occupied").value = occupied + 1
                            return Transaction.success(currentData)
                        }
                        return Transaction.abort()
                    }

                    override fun onComplete(
                        error: DatabaseError?,
                        committed: Boolean,
                        snapshot: DataSnapshot?
                    ) {
                        if (committed) {
                            Toast.makeText(this@ParkingActivity, "Reservation Successful", Toast.LENGTH_SHORT).show()
                            clearForm()
                        } else {
                            Toast.makeText(this@ParkingActivity, "No available slots", Toast.LENGTH_SHORT).show()
                        }
                        error?.let { Log.e("TRANSACTION", "Error: ${it.message}") }
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ParkingActivity, "Failed to fetch user name", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun clearForm() {
        vehicleNumberEditText.text.clear()
        parkingDateInput.text.clear()
        parkingTimeInput.text.clear()
        parkingDurationInput.text.clear()
        parkingTypeSpinner.setSelection(0)
    }
}
