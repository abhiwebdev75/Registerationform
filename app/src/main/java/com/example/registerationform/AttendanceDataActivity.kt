package com.example.registerationform

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class AttendanceDataActivity : AppCompatActivity() {

    private lateinit var attendanceContainer: LinearLayout
    private lateinit var summaryContainer: LinearLayout
    private lateinit var dbRef: DatabaseReference
    private val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance_data)

        attendanceContainer = findViewById(R.id.attendanceContainer)
        summaryContainer = findViewById(R.id.summaryContainer)
        dbRef = FirebaseDatabase.getInstance().getReference("users")

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), 1)
        }

        loadUserAttendanceData()
    }

    private fun loadUserAttendanceData() {
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                attendanceContainer.removeAllViews()
                summaryContainer.removeAllViews()

                for (userSnap in snapshot.children) {
                    val uid = userSnap.key ?: continue
                    val name = userSnap.child("name").getValue(String::class.java) ?: "-"
                    val phone = userSnap.child("phone").getValue(String::class.java) ?: "-"

                    val card = LayoutInflater.from(this@AttendanceDataActivity)
                        .inflate(R.layout.item_attendance_card, attendanceContainer, false)
                    card.findViewById<TextView>(R.id.attendanceName).text = name

                    val btnPresent = card.findViewById<Button>(R.id.btnMarkPresent)
                    val btnAbsent = card.findViewById<Button>(R.id.btnMarkAbsent)

                    btnPresent.setOnClickListener {
                        markAttendance(uid, name, phone, true)
                    }
                    btnAbsent.setOnClickListener {
                        markAttendance(uid, name, phone, false)
                    }

                    attendanceContainer.addView(card)

                    // Summary Row
                    val summaryRow = TextView(this@AttendanceDataActivity)
                    summaryRow.textSize = 16f
                    dbRef.child(uid).child("attendance").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(attSnap: DataSnapshot) {
                            val total = attSnap.childrenCount
                            var presentCount = 0
                            for (att in attSnap.children) {
                                if (att.value == "Present") presentCount++
                            }
                            summaryRow.text = "$name: $presentCount / $total days present"
                            summaryContainer.addView(summaryRow)
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AttendanceDataActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun markAttendance(uid: String, name: String, phone: String, isPresent: Boolean) {
        val status = if (isPresent) "Present" else "Absent"
        dbRef.child(uid).child("attendance").child(todayDate).setValue(status)
        sendSms(phone, "Hello $name, your attendance is marked as $status for today.")
        Toast.makeText(this, "$name marked $status", Toast.LENGTH_SHORT).show()
        loadUserAttendanceData()
    }

    private fun sendSms(phone: String, message: String) {
        try {
            SmsManager.getDefault().sendTextMessage(phone, null, message, null, null)
        } catch (e: Exception) {
            Toast.makeText(this, "SMS failed to send", Toast.LENGTH_SHORT).show()
        }
    }
}
