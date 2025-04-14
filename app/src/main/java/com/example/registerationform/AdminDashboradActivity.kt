package com.example.registerationform

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

/** Data class to hold user info */
data class User(
    val uid: String,
    val name: String,
    val email: String,
    val phone: String,
    val joined: String,
    val membershipType: String,
    val expiry: String
)

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var userContainer: LinearLayout
    private lateinit var database: DatabaseReference
    private lateinit var searchView: SearchView

    // In-memory list of users for filtering
    private var userList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Use the correct layout file
        setContentView(R.layout.activity_admin_dashboard)

        // Retrieve view references from the layout
        userContainer = findViewById(R.id.userContainer)
        searchView = findViewById(R.id.searchView)
        database = FirebaseDatabase.getInstance().getReference("users")

        // Get button references
        val btnAttendance: Button = findViewById(R.id.btnAttendance)
        val btnExtraData: Button = findViewById(R.id.btnExtraData)
        val btnLogout: Button = findViewById(R.id.btnLogout)

        btnAttendance.setOnClickListener {
            val intent = Intent(this, AttendanceDataActivity::class.java)
            startActivity(intent)
        }

        btnExtraData.setOnClickListener {
            val intent = Intent(this, ExtraDataActivity::class.java)
            startActivity(intent)
        }
        btnLogout.setOnClickListener {
            // Optionally sign out from FirebaseAuth if needed:
            // FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "Logging out", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Load user data from Firebase
        loadUserData()

        // Set up SearchView listener to filter users by name
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { filterUsers(it) }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filterUsers(it) }
                return true
            }
        })
    }

    private fun loadUserData() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                // Remove all views to refresh list
                userContainer.removeAllViews()

                for (userSnap in snapshot.children) {
                    val uid = userSnap.key ?: continue
                    val name = userSnap.child("name").getValue(String::class.java) ?: "-"
                    val email = userSnap.child("email").getValue(String::class.java) ?: "-"
                    val phone = userSnap.child("phone").getValue(String::class.java) ?: "-"
                    val joined = userSnap.child("joinedOn").getValue(String::class.java) ?: "-"
                    var type = userSnap.child("membership/type").getValue(String::class.java)
                    var expiry = userSnap.child("membership/expiry").getValue(String::class.java)

                    // Apply default Trial membership if missing
                    if (type == null || expiry == null) {
                        type = "Trial"
                        expiry = getDateNDaysLater(7)
                        val defaultMembership = mapOf("type" to type, "expiry" to expiry)
                        database.child(uid).child("membership").setValue(defaultMembership)
                    }

                    // Create a user object and add it to our list
                    val user = User(uid, name, email, phone, joined, type, expiry)
                    userList.add(user)
                }
                // Initially display all users
                displayUsers(userList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminDashboardActivity, "Error loading users", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Filters the user list and displays matching users
    private fun filterUsers(query: String) {
        val filtered = userList.filter { user ->
            user.name.contains(query, ignoreCase = true)
        }
        displayUsers(filtered)
    }

    // Dynamically creates and displays user cards in the container
    private fun displayUsers(users: List<User>) {
        userContainer.removeAllViews()
        for (user in users) {
            val userCard = LayoutInflater.from(this@AdminDashboardActivity)
                .inflate(R.layout.user_card_admin, userContainer, false)
            userCard.findViewById<TextView>(R.id.textName).text = "Name: ${user.name}"
            userCard.findViewById<TextView>(R.id.textEmail).text = "Email: ${user.email}"
            userCard.findViewById<TextView>(R.id.textPhone).text = "Phone: ${user.phone}"
            userCard.findViewById<TextView>(R.id.textJoined).text = "Joined: ${user.joined}"
            userCard.findViewById<TextView>(R.id.textMembership).text = "Membership: ${user.membershipType}"
            userCard.findViewById<TextView>(R.id.textExpiry).text = "Expiry: ${user.expiry}"

            val editBtn = userCard.findViewById<Button>(R.id.btnEditUser)
            editBtn.setOnClickListener {
                showEditDialog(user.uid, user.name, user.email, user.phone, user.membershipType, user.expiry)
            }
            userContainer.addView(userCard)
        }
    }

    // Displays an AlertDialog to edit user info
    private fun showEditDialog(uid: String, name: String, email: String, phone: String, type: String, expiry: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_user, null)
        val nameField = dialogView.findViewById<EditText>(R.id.editName)
        val emailField = dialogView.findViewById<EditText>(R.id.editEmail)
        val phoneField = dialogView.findViewById<EditText>(R.id.editPhone)
        val typeField = dialogView.findViewById<EditText>(R.id.editMembership)
        val expiryField = dialogView.findViewById<EditText>(R.id.editExpiry)

        nameField.setText(name)
        emailField.setText(email)
        phoneField.setText(phone)
        typeField.setText(type)
        expiryField.setText(expiry)

        AlertDialog.Builder(this)
            .setTitle("Edit User")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val updates = mapOf(
                    "name" to nameField.text.toString(),
                    "email" to emailField.text.toString(),
                    "phone" to phoneField.text.toString(),
                    "membership/type" to typeField.text.toString(),
                    "membership/expiry" to expiryField.text.toString()
                )
                database.child(uid).updateChildren(updates).addOnSuccessListener {
                    Toast.makeText(this, "User updated", Toast.LENGTH_SHORT).show()
                    loadUserData()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // Utility: Returns a date string 'days' days in the future
    private fun getDateNDaysLater(days: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, days)
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
    }
}
