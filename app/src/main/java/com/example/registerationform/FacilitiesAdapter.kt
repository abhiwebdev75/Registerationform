package com.example.registerationform

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FacilitiesAdapter(private val facilities: List<FacilityItem>, private val context: Context) :
    RecyclerView.Adapter<FacilitiesAdapter.FacilityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacilityViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_facility, parent, false)
        return FacilityViewHolder(view)
    }

    override fun onBindViewHolder(holder: FacilityViewHolder, position: Int) {
        val facility = facilities[position]
        holder.nameTextView.text = facility.name
        holder.descriptionTextView.text = facility.description
        holder.imageView.setImageResource(facility.imageResId)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, facility.activityClass)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = facilities.size

    class FacilityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.facilityImage)
        val nameTextView: TextView = itemView.findViewById(R.id.facilityName)
        val descriptionTextView: TextView = itemView.findViewById(R.id.facilityDescription)
    }
}
