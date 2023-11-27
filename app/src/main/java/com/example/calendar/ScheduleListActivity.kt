package com.example.calendar

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import android.content.Intent
import android.widget.EditText
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog


class ScheduleListActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var userID: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var scheduleListAdapter: ScheduleListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_list)

        userID = intent.getStringExtra("userID") ?: ""
        database = FirebaseDatabase.getInstance().reference

        recyclerView = findViewById(R.id.recyclerViewScheduleList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        scheduleListAdapter = ScheduleListAdapter(ArrayList()) { scheduleItem ->
            showEditOptionsDialog(scheduleItem)
        }
        recyclerView.adapter = scheduleListAdapter

        // 여기서 loadSchedules 함수 호출
        loadSchedules()
    }

    private fun loadSchedules() {
        val scheduleRef = database.child("schedules").child(userID)

        scheduleRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val scheduleList = mutableListOf<ScheduleItem>()

                for (scheduleSnapshot in dataSnapshot.children) {
                    val schedule = scheduleSnapshot.getValue(ScheduleItem::class.java)
                    schedule?.let {
                        it.key = scheduleSnapshot.key.toString()// 키 할당
                        scheduleList.add(it)
                    }
                }

                scheduleListAdapter.setData(scheduleList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                showToast("일정 불러오기 실패: ${databaseError.message}")
            }
        })
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    private fun updateSchedule(
        scheduleItem: ScheduleItem,
        newSchedule: String,
        newStartTime: String,
        newEndTime: String
    ) {
        val scheduleRef = database.child("schedules").child(userID).child(scheduleItem.key)

        // 수정할 데이터 설정
        val updatedData = HashMap<String, Any>()
        updatedData["schedule"] = newSchedule
        updatedData["startTime"] = newStartTime
        updatedData["endTime"] = newEndTime

        scheduleRef.updateChildren(updatedData)
            .addOnSuccessListener {
                showToast("일정 수정 완료")
                // 리스트 갱신을 제거합니다.
                // loadSchedules()
                navigateToMainActivity()
            }
            .addOnFailureListener { e ->
                showToast("일정 수정 실패: ${e.message}")
            }
    }

    // ... (기타 필요한 함수 및 코드 추가)


    private fun deleteSchedule(scheduleItem: ScheduleItem) {
        val scheduleRef = database.child("schedules").child(userID).child(scheduleItem.key)
        scheduleRef.removeValue()
            .addOnSuccessListener {
                showToast("일정 삭제 완료")
                // 리스트 갱신을 제거합니다.
                // loadSchedules()
                navigateToMainActivity()
            }
            .addOnFailureListener { e ->
                showToast("일정 삭제 실패: ${e.message}")
            }
    }

    // 기타 필요한 함수 및 코드 추가
    private fun showEditOptionsDialog(scheduleItem: ScheduleItem) {
        AlertDialog.Builder(this)
            .setTitle("수정 또는 삭제")
            .setPositiveButton("수정") { _, _ ->
                // 수정 다이얼로그로 이동
                showEditDialog(scheduleItem)
            }
            .setNegativeButton("삭제") { _, _ ->
                // 삭제 기능 구현
                showDeleteDialog(scheduleItem)
            }
            .show()
    }

    private fun showEditDialog(scheduleItem: ScheduleItem) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_schedule, null, false)
        val editTextSchedule = dialogView.findViewById<EditText>(R.id.editTextSchedule)
        val timePickerStart = dialogView.findViewById<TimePicker>(R.id.timePickerStart)
        val timePickerEnd = dialogView.findViewById<TimePicker>(R.id.timePickerEnd)

        // 기존 일정 내용 및 시작 시간, 종료 시간 설정
        editTextSchedule.setText(scheduleItem.schedule)
        val startTimeParts = scheduleItem.startTime.split(":")
        val endTimeParts = scheduleItem.endTime.split(":")
        timePickerStart.hour = startTimeParts[0].toInt()
        timePickerStart.minute = startTimeParts[1].toInt()
        timePickerEnd.hour = endTimeParts[0].toInt()
        timePickerEnd.minute = endTimeParts[1].toInt()

        // 다이얼로그 생성
        AlertDialog.Builder(this)
            .setTitle("일정 수정")
            .setView(dialogView)
            .setPositiveButton("수정") { _, _ ->
                val newSchedule = editTextSchedule.text.toString().trim()
                val newStartTime = "${timePickerStart.hour}:${timePickerStart.minute}"
                val newEndTime = "${timePickerEnd.hour}:${timePickerEnd.minute}"

                // 수정 기능 구현
                updateSchedule(scheduleItem, newSchedule, newStartTime, newEndTime)
            }
            .setNegativeButton("취소", null)
            .show()
    }
    private fun showDeleteDialog(scheduleItem: ScheduleItem) {
        AlertDialog.Builder(this)
            .setTitle("일정 삭제")
            .setMessage("일정을 삭제하시겠습니까?")
            .setPositiveButton("삭제") { _, _ ->
                deleteSchedule(scheduleItem)
            }
            .setNegativeButton("취소", null)
            .show()
    }
    private fun navigateToMainActivity() {
        // 메인 엑티비티로 이동
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("userID", userID)
        startActivity(intent)

        // 현재 엑티비티 종료
        finish()
    }
}