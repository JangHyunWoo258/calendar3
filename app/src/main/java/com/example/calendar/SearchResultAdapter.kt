package com.example.calendar

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SearchAdapter(private val context: Context) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private var searchResults: List<Schedule> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.schedule_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val schedule = searchResults[position]
        holder.bind(schedule)
    }

    override fun getItemCount(): Int {
        return searchResults.size
    }

    fun setSearchResults(results: List<Schedule>) {
        searchResults = results
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val combinedInfoTextView: TextView = itemView.findViewById(R.id.scheduleTextView)

        fun bind(schedule: Schedule) {
            val combinedInfo = "${schedule.schedule}, ${schedule.startTime} - ${schedule.endTime}, ${schedule.date}"
            combinedInfoTextView.text = combinedInfo
        }
    }
}