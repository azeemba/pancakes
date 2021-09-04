package com.azeemba.pancakes

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import java.time.Instant

class VisitListAdapter(private val visits: List<Visit>): RecyclerView.Adapter<VisitListAdapter.ViewHolder>(){
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.visit_title)
        val timestamp: TextView = itemView.findViewById(R.id.visit_timestamp)
        val url: TextView = itemView.findViewById(R.id.visit_url)

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

        holder.url.setText(visit.url)

        val presentableTime = java.time.format.DateTimeFormatter.ISO_INSTANT.format(
            Instant.ofEpochSecond(visit.timestamp)).replace("T", " ")
        holder.timestamp.setText(presentableTime)

        holder.title.setOnClickListener {
            val url = holder.url.text
            val clipboard = it.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Pancake page", url)
            clipboard.setPrimaryClip(clip)

            Snackbar.make(it, "${url} copied", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun getItemCount(): Int {
        return visits.size
    }
}