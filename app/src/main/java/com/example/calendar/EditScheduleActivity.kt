package com.example.calendar

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EditScheduleActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var userID: String
    private lateinit var editTextSchedule: EditText
    private lateinit var timePickerStart: TimePicker
    private lateinit var timePickerEnd: TimePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_schedule)

        userID = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        database = FirebaseDatabase.getInstance().reference

        editTextSchedule = findViewById(R.id.editTextSchedule)
        timePickerStart = findViewById(R.id.timePickerStart)
        timePickerEnd = findViewById(R.id.timePickerEnd)

        val buttonUpdate: Button = findViewById(R.id.buttonUpdate)

        // 수정 버튼 클릭 이벤트
        buttonUpdate.setOnClickListener {
            updateSchedule()
        }
    }

    private fun updateSchedule() {
        val newSchedule = editTextSchedule.text.toString().trim()
        val newStartTime = "${timePickerStart.hour}:${timePickerStart.minute}"
        val newEndTime = "${timePickerEnd.hour}:${timePickerEnd.minute}"

        // TODO: 수정할 데이터를 파이어베이스에 반영하는 코드 작성

        // 수정이 완료되면 액티비티 종료
        finish()
    }
}