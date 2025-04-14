package com.example.registerationform

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var signInBtn: Button
    private lateinit var emailSignIn: EditText
    private lateinit var passwordSignIn: EditText
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        signInBtn = findViewById(R.id.signInBtn)
        emailSignIn = findViewById(R.id.emailSignIn)
        passwordSignIn = findViewById(R.id.passwordSignIn)

        auth = FirebaseAuth.getInstance()

        signInBtn.setOnClickListener {
            val email = emailSignIn.text.toString().trim()
            val password = passwordSignIn.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Check for admin credentials
                if (email == "fithitchd057@gmail.com" && password == "24MCA20057") {
                    Toast.makeText(this, "Admin Login Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AdminDashboardActivity::class.java))
                    finish()
                    return@setOnClickListener
                }

                // Normal user login
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Sign In Successful", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, Index::class.java)
                            intent.putExtra("USERNAME", email)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Authentication Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
