package com.example.registerationform

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var signUpBtn: Button
    private lateinit var nameField: EditText
    private lateinit var emailField: EditText
    private lateinit var phoneField: EditText
    private lateinit var passwordField: EditText
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ensure your layout file is named activity_sign_up.xml
        setContentView(R.layout.activity_sign_up)

        signUpBtn = findViewById(R.id.signUpBtn)
        nameField = findViewById(R.id.username)
        emailField = findViewById(R.id.emailField)
        phoneField = findViewById(R.id.phoneField)
        passwordField = findViewById(R.id.passwordField)

        auth = FirebaseAuth.getInstance()

        signUpBtn.setOnClickListener {
            val name = nameField.text.toString().trim()
            val email = emailField.text.toString().trim()
            val phone = phoneField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            // Ensure required fields are not empty
            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val currentUser = auth.currentUser
                            if (currentUser != null) {
                                val uid = currentUser.uid
                                val dbRef = FirebaseDatabase.getInstance().getReference("users")

                                // Use provided data, or default to "none" if missing.
                                val finalName = if (name.isNotEmpty()) name else "none"
                                val finalPhone = if (phone.isNotEmpty()) phone else "none"
                                val joinedOn = getCurrentDate()
                                // Set default membership values ("none")
                                val membership = mapOf("type" to "none", "expiry" to "none")

                                val userMap = mapOf(
                                    "name" to finalName,
                                    "email" to email,
                                    "phone" to finalPhone,
                                    "joinedOn" to joinedOn,
                                    "membership" to membership
                                )

                                // Save user data into the "users" node using the UID as the key.
                                dbRef.child(uid).setValue(userMap)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this,SignInActivity::class.java))
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            Toast.makeText(this, "Sign Up Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Returns the current date as "yyyy-MM-dd"
    private fun getCurrentDate(): String {
        val cal = Calendar.getInstance()
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
    }
}
