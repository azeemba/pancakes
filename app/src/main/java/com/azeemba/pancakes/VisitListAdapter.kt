package com.azeemba.pancakes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.Instant

class VisitListAdapter(private val visits: List<Visit>): RecyclerView.Adapter<VisitListAdapter.ViewHolder>(){
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById<TextView>(R.id.visit_title)
        val timestamp: TextView = itemView.findViewById<TextView>(R.id.visit_timestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        val visitView = inflater.inflate(R.layout.visit_item, parent, false)
        return ViewHolder(visitView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val visit = visits.get(position)
        holder.title.setText(visit.title)
        holder.title.textSize = 20f

        val presentableTime = java.time.format.DateTimeFormatter.ISO_INSTANT.format(
            Instant.ofEpochSecond(visit.timestamp)).replace("T", " ")
        holder.timestamp.setText(presentableTime)
    }

    override fun getItemCount(): Int {
        return visits.size
    }
}