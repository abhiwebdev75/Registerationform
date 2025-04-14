package com.example.registerationform

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class TrainerAdapter(
    private val trainerList: List<Trainer>,
    private val onBookAppointmentListener: (Trainer) -> Unit
) : RecyclerView.Adapter<TrainerAdapter.TrainerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trainer_item, parent, false)
        return TrainerViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrainerViewHolder, position: Int) {
        val trainer = trainerList[position]
        holder.trainerName.text = trainer.name
        holder.trainerSpecialty.text = trainer.specialty
        holder.trainerDescription.text = trainer.description
        holder.trainerImage.setImageResource(trainer.imageResId)

        // Set click listener for the "Book Appointment" button
        holder.bookAppointmentButton.setOnClickListener {
            onBookAppointmentListener(trainer)
        }
    }

    override fun getItemCount(): Int {
        return trainerList.size
    }

    class TrainerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val trainerImage: ImageView = itemView.findViewById(R.id.trainerImage)
        val trainerName: TextView = itemView.findViewById(R.id.trainerName)
        val trainerSpecialty: TextView = itemView.findViewById(R.id.trainerSpecialty)
        val trainerDescription: TextView = itemView.findViewById(R.id.trainerDescription)
        val bookAppointmentButton: Button = itemView.findViewById(R.id.bookAppointmentButton)
    }
}
