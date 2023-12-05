import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calendar.R
import com.example.calendar.ScheduleItem

class ScheduleAdapter(private var scheduleList: List<ScheduleItem>, private var userName: String) :
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

        // 사용자 이름 파란색으로 표시
        val spannableString = SpannableString(" ${scheduleItem.userName}\n일정: ${scheduleItem.schedule}\n시작 시간: ${scheduleItem.startTime}\n종료 시간: ${scheduleItem.endTime}")
        val userNameStartIndex = spannableString.indexOf(scheduleItem.userName)
        val userNameEndIndex = userNameStartIndex + scheduleItem.userName.length

        if (userNameStartIndex != -1) {
            spannableString.setSpan(
                ForegroundColorSpan(Color.BLUE),
                userNameStartIndex,
                userNameEndIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        holder.scheduleText.text = spannableString
    }

    override fun getItemCount(): Int {
        return scheduleList.size
    }

    // 외부에서 데이터를 설정하는 메서드
    fun setData(newList: List<ScheduleItem> ){
        this.scheduleList = newList

        notifyDataSetChanged()
    }
}