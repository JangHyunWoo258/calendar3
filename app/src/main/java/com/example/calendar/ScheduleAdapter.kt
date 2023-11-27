import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calendar.R
import com.example.calendar.ScheduleItem

class ScheduleAdapter(private var scheduleList: List<ScheduleItem>) :
    RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val scheduleText: TextView = itemView.findViewById(R.id.scheduleText)
        val timeText: TextView = itemView.findViewById(R.id.timeText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_schedule, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val scheduleItem = scheduleList[position]
        val formattedSchedule ="시작 시간: ${scheduleItem.startTime}\n종료 시간: ${scheduleItem.endTime}"
        holder.scheduleText.text = scheduleItem.schedule
        holder.timeText.text = formattedSchedule

    }

    override fun getItemCount(): Int {
        return scheduleList.size
    }

    // 외부에서 데이터를 설정하는 메서드
    fun setData(newList: List<ScheduleItem>) {
        scheduleList = newList
        notifyDataSetChanged()
    }
}