package com.example.calendar

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class ScheduleInputActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var userID: String
    private lateinit var selectedDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_input)

        // Firebase 인스턴스 초기화
        database = FirebaseDatabase.getInstance().reference
        val firebaseAuth = FirebaseAuth.getInstance()

        // 사용자 ID 가져오기
        userID = firebaseAuth.currentUser?.uid ?: ""

        // 전달받은 날짜 확인
        selectedDate = intent.getStringExtra("selectedDate") ?: ""
        if (selectedDate.isNotEmpty()) {
            // 날짜를 표시하는 TextView 업데이트
            updateSelectedDateTextView()
        }

        val btnSaveSchedule = findViewById<Button>(R.id.buttonSave)
        val editTextSchedule = findViewById<EditText>(R.id.editTextSchedule)
        val timePickerStart = findViewById<TimePicker>(R.id.timePickerStart)
        val timePickerEnd = findViewById<TimePicker>(R.id.timePickerEnd)

        btnSaveSchedule.setOnClickListener {
            val schedule = editTextSchedule.text.toString()
            val hourStart = timePickerStart.currentHour
            val minuteStart = timePickerStart.currentMinute
            val hourEnd = timePickerEnd.currentHour
            val minuteEnd = timePickerEnd.currentMinute

            saveScheduleToFirebase(schedule, hourStart, minuteStart, hourEnd, minuteEnd)
        }
    }

    private fun updateSelectedDateTextView() {
        val selectedDateTextView = findViewById<TextView>(R.id.textViewDate)
        selectedDateTextView.text = selectedDate
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun saveScheduleToFirebase(
        schedule: String,
        hourStart: Int,
        minuteStart: Int,
        hourEnd: Int,
        minuteEnd: Int
    ) {
        // "schedules" 노드에 일정 저장
        val scheduleRef = database.child("schedules").child(userID).push()

        val startTime = String.format(Locale.getDefault(), "%02d:%02d", hourStart, minuteStart)
        val endTime = String.format(Locale.getDefault(), "%02d:%02d", hourEnd, minuteEnd)

        // 사용자의 이름을 가져오기
        val userRef = database.child("users").child(userID)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userName = dataSnapshot.child("name").value as? String ?: ""

                // 일정 데이터 구성
                val scheduleData = HashMap<String, Any>()
                scheduleData["schedule"] = schedule
                scheduleData["startTime"] = startTime
                scheduleData["endTime"] = endTime
                scheduleData["date"] = selectedDate
                scheduleData["userName"] = userName // 사용자 이름 추가

                // 일정 데이터를 데이터베이스에 저장
                scheduleRef.setValue(scheduleData)
                    .addOnSuccessListener {
                        showToast("일정 저장 완료")
                        navigateToMainActivity()
                    }
                    .addOnFailureListener { e ->
                        showToast("일정 저장 실패: ${e.message}")
                    }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                showToast("사용자 정보 불러오기 실패: ${databaseError.message}")
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
