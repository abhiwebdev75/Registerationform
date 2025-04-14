package com.example.registerationform
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

    class GymTrainersActivity : AppCompatActivity() {

        private lateinit var trainerRecyclerView: RecyclerView
        private lateinit var trainerAdapter: TrainerAdapter
        private val trainerList = ArrayList<Trainer>()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_gym_trainers)

            trainerRecyclerView = findViewById(R.id.trainerRecyclerView)

            // Add trainer data
            trainerList.add(Trainer("John Doe", "Yoga", "John has over 10 years of experience in Yoga and has helped numerous clients improve flexibility and strength.", R.drawable.trainer6))
            trainerList.add(Trainer("Jane Smith", "Zumba", "Jane is an energetic Zumba instructor who loves helping people stay fit with fun and engaging routines.", R.drawable.trainer6))
            trainerList.add(Trainer("Mike Johnson", "Pilates", "Mike has a passion for teaching Pilates and has helped many people improve their core strength.", R.drawable.trainer6))
            trainerList.add(Trainer("Emily Davis", "CrossFit", "Emily is a certified CrossFit trainer focused on helping clients achieve their fitness goals.", R.drawable.trainer6))

            // Set up RecyclerView
            trainerAdapter = TrainerAdapter(trainerList) { trainer ->
                // Handle booking appointment (e.g., show a toast)
                Toast.makeText(this, "Booking appointment with ${trainer.name}", Toast.LENGTH_SHORT).show()
                // You can also open a new activity or perform other actions here
            }
            trainerRecyclerView.layoutManager = LinearLayoutManager(this)
            trainerRecyclerView.adapter = trainerAdapter
        }
    }
