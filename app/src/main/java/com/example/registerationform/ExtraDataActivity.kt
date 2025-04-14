package com.example.registerationform

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class ExtraDataActivity : AppCompatActivity() {

    private lateinit var parkingTable: TableLayout
    private lateinit var saunaTable: TableLayout
    private lateinit var lockerTable: TableLayout

    private lateinit var parkingResTable: TableLayout
    private lateinit var saunaResTable: TableLayout
    private lateinit var lockerResTable: TableLayout

    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_extra_data)

        // Initialize tables
        parkingTable = findViewById(R.id.parkingTable)
        saunaTable = findViewById(R.id.saunaTable)
        lockerTable = findViewById(R.id.lockerTable)

        parkingResTable = findViewById(R.id.parkingReservationsTable)
        saunaResTable = findViewById(R.id.saunaReservationsTable)
        lockerResTable = findViewById(R.id.lockerReservationsTable)

        database = FirebaseDatabase.getInstance()

        setupSlotHeaders()
        setupReservationHeaders()

        loadParkingData()
        loadSaunaData()
        loadLockerData()

        loadParkingReservations()
        loadSaunaReservations()
        loadLockerReservations()
    }

    private fun setupSlotHeaders() {
        addHeader(parkingTable, listOf("Slot", "Status"))
        addHeader(saunaTable, listOf("Slot", "Status"))
        addHeader(lockerTable, listOf("Lockers", "Status"))
    }

    private fun setupReservationHeaders() {
        val headers = listOf("User", "Date", "Time", "Duration")
        val pheaders = listOf("User", "Vechile No.", "Type","Date", "Time", "Duration")
        val lheaders = listOf("User", "Locker", "Duration")
        addHeader(parkingResTable, pheaders)
        addHeader(saunaResTable, headers)
        addHeader(lockerResTable, lheaders)
    }

    private fun addHeader(table: TableLayout, headers: List<String>) {
        val row = TableRow(this)
        headers.forEach { title ->
            val text = TextView(this).apply {
                text = title
                setPadding(12, 12, 12, 12)
                setTextColor(resources.getColor(R.color.white, theme))
                textSize = 16f
                gravity = Gravity.CENTER
            }
            row.addView(text)
        }
        table.addView(row)
    }

    private fun addRow(table: TableLayout, values: List<String>) {
        val row = TableRow(this)
        values.forEach { value ->
            val text = TextView(this).apply {
                text = value
                setPadding(12, 12, 12, 12)
                setTextColor(resources.getColor(R.color.white, theme))
                gravity = Gravity.CENTER
            }
            row.addView(text)
        }
        table.addView(row)
    }

    private fun loadLockerData() {
        val ref = database.getReference("lockerSlots")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val available = snapshot.child("available").getValue(Int::class.java) ?: 0
                val occupied = snapshot.child("occupied").getValue(Int::class.java) ?: 0
                val total = snapshot.child("total").getValue(Int::class.java) ?: 0

                // Add rows to a table layout or update UI
                addRow(lockerTable, listOf("Available", available.toString()))
                addRow(lockerTable, listOf("Occupied", occupied.toString()))
                addRow(lockerTable, listOf("Total", total.toString()))
            }

            override fun onCancelled(error: DatabaseError) {
               }
        })
    }


    private fun loadParkingData() {
        val ref = database.getReference("parkingSlots")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val available = snapshot.child("available").getValue(Int::class.java) ?: 0
                val occupied = snapshot.child("occupied").getValue(Int::class.java) ?: 0
                val total = snapshot.child("total").getValue(Int::class.java) ?: 0

                // Add rows to a table layout or update UI
                addRow(parkingTable, listOf("Available", available.toString()))
                addRow(parkingTable, listOf("Occupied", occupied.toString()))
                addRow(parkingTable, listOf("Total", total.toString()))
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun loadSaunaData() {
        val ref = database.getReference("saunaSlots")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val available = snapshot.child("available").getValue(Int::class.java) ?: 0
                val occupied = snapshot.child("occupied").getValue(Int::class.java) ?: 0
                val total = snapshot.child("total").getValue(Int::class.java) ?: 0

                // Add rows to a table layout or update UI
                addRow(saunaTable, listOf("Available", available.toString()))
                addRow(saunaTable, listOf("Occupied", occupied.toString()))
                addRow(saunaTable, listOf("Total", total.toString()))
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun loadParkingReservations() {
        val ref = database.getReference("reservations")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (res in snapshot.children) {
                    val user = res.child("name").getValue(String::class.java) ?: "-"
                    val vehicleNumber = res.child("vehicleNumber").getValue(String::class.java) ?: "-"
                    val parkingType = res.child("parkingType").getValue(String::class.java) ?: "-"
                    val date = res.child("date").getValue(String::class.java) ?: "-"
                    val time = res.child("time").getValue(String::class.java) ?: "-"
                    val duration = res.child("duration").getValue(String::class.java) ?: "-"
                    addRow(parkingResTable, listOf(user, vehicleNumber,parkingType, date, time, duration))
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadSaunaReservations() {
        val ref = database.getReference("saunaReservations")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (res in snapshot.children) {
                    val user = res.child("user").getValue(String::class.java) ?: "-"
                    val date = res.child("date").getValue(String::class.java) ?: "-"
                    val time = res.child("time").getValue(String::class.java) ?: "-"
                    val duration = res.child("duration").getValue(String::class.java) ?: "-"
                    addRow(saunaResTable, listOf(user, date, time, duration))
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadLockerReservations() {
        val ref = database.getReference("lockerReservations")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (res in snapshot.children) {
                    val user = res.child("user").getValue(String::class.java) ?: "-"
                    val slot = res.child("lockerNumber").getValue(String::class.java) ?:"-"
                    val duration = res.child("duration").getValue(String::class.java) ?: "-"
                    addRow(lockerResTable, listOf(user, slot, duration))
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
