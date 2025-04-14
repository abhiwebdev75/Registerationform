package com.example.registerationform

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Profile : AppCompatActivity() {

    private lateinit var nameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var phoneEditText: TextInputEditText
    private lateinit var membershipEditText: TextInputEditText
    private lateinit var expiryDateEditText: TextInputEditText

    private lateinit var database: DatabaseReference
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance().reference

        // Initialize views
        val backButton = findViewById<ImageView>(R.id.profileBackButton)
        val updateButton = findViewById<Button>(R.id.updateButton)
        val logoutButton = findViewById<Button>(R.id.logoutButton)

        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        membershipEditText = findViewById(R.id.membershipEditText)
        expiryDateEditText = findViewById(R.id.expiryDateEditText)

        // Load user data from Firebase
        loadUserData()

        // Set click listeners
        backButton.setOnClickListener {
            finish()
        }

        updateButton.setOnClickListener {
            if (validateInputs()) {
                updateProfile()
            }
        }

        logoutButton.setOnClickListener {
            logoutUser()
        }
    }

    private fun loadUserData() {
        currentUser?.uid?.let { uid ->
            database.child("users").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.child("membership/name").getValue(String::class.java)
                    val email = snapshot.child("attendance/email").getValue(String::class.java)
                    val phone = snapshot.child("membership/phone").getValue(String::class.java)
                    val membership = snapshot.child("membership/type").getValue(String::class.java)
                    val expiry = snapshot.child("membership/expiryDate").getValue(String::class.java)

                    nameEditText.setText(name ?: "")
                    emailEditText.setText(email ?: "")
                    phoneEditText.setText(phone ?: "")
                    membershipEditText.setText(membership ?: "")
                    expiryDateEditText.setText(expiry ?: "")
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@Profile, "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun updateProfile() {
        val name = nameEditText.text.toString()
        val email = emailEditText.text.toString()
        val phone = phoneEditText.text.toString()
        val membership = membershipEditText.text.toString()
        val expiry = expiryDateEditText.text.toString()

        currentUser?.uid?.let { uid ->
            val updates = mapOf(
                "name" to name,
                "phone" to phone,
                "membership/type" to membership,
                "membership/expiryDate" to expiry,
                "email" to email
            )
            database.child("users").child(uid).updateChildren(updates)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun validateInputs(): Boolean {
        val name = nameEditText.text.toString()
        val email = emailEditText.text.toString()

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
            return false
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, SignUpActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
