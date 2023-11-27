package com.example.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ScheduleListAdapter(
    private var scheduleList: List<ScheduleItem>,
    private val onItemClick: (ScheduleItem) -> Unit
) : RecyclerView.Adapter<ScheduleListAdapter.ScheduleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_schedule, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val scheduleItem = scheduleList[position]
        holder.bind(scheduleItem)
        holder.itemView.setOnClickListener { onItemClick(scheduleItem) }
    }

    override fun getItemCount(): Int {
        return scheduleList.size
    }

    fun setData(newList: List<ScheduleItem>) {
        scheduleList = newList
        notifyDataSetChanged()
    }

    class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val scheduleTextView: TextView = itemView.findViewById(R.id.scheduleText)

        fun bind(scheduleItem: ScheduleItem) {
            scheduleTextView.text = scheduleItem.schedule
            // 기타 필요한 UI 업데이트
        }
    }
}