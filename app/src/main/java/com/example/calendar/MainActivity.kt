package com.example.calendar

import ScheduleAdapter
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    lateinit var saveBtn: Button
    lateinit var updateBtn: Button
    lateinit var deleteBtn: Button
    lateinit var diaryContent: TextView
    private lateinit var database: DatabaseReference
    private lateinit var userID: String
    private lateinit var calendarView: CalendarView
    private lateinit var currentDate: String
    private var selectedDate: String = ""
    private lateinit var recyclerView: RecyclerView
    private lateinit var scheduleAdapter: ScheduleAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        saveBtn = findViewById(R.id.saveBtn)
        updateBtn = findViewById(R.id.updateBtn)
        deleteBtn = findViewById(R.id.deleteBtn)
        diaryContent = findViewById(R.id.diaryContent)
        calendarView = findViewById(R.id.calendarView)

        userID = intent.getStringExtra("userID") ?: ""
        database = FirebaseDatabase.getInstance().reference
        val firebaseAuth = FirebaseAuth.getInstance()

        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            navigateToLoginActivity()
            return
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val date = "$year-${month + 1}-$dayOfMonth"
            selectedDate = date
            currentDate = date
            loadDiary(date)
        }

        deleteBtn.setOnClickListener {
            navigateToScheduleList()
            loadDiary(selectedDate)
        }

        updateBtn.setOnClickListener {
            navigateToScheduleList()
            loadDiary(selectedDate)
        }

        saveBtn.setOnClickListener {
            val intent = Intent(this, ScheduleInputActivity::class.java)
            intent.putExtra("selectedDate", selectedDate)
            startActivity(intent)
            loadDiary(selectedDate)
        }
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        scheduleAdapter = ScheduleAdapter(ArrayList(),"") // 초기에는 빈 목록으로 시작
        recyclerView.adapter = scheduleAdapter

        // 일정 목록을 불러오는 함수 호출

        loadDiary(selectedDate)
    }


    private fun loadDiary(date: String) {
        val scheduleRef = database.child("schedules").child(userID)
        val userRef = database.child("users").child(userID)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userName = dataSnapshot.child("name").value as? String ?: ""

                scheduleRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val scheduleList = mutableListOf<ScheduleItem>()

                        for (scheduleSnapshot in dataSnapshot.children) {
                            val scheduleDate = scheduleSnapshot.child("date").value as? String ?: ""
                            if (scheduleDate == date) {
                                val schedule = scheduleSnapshot.child("schedule").value as? String ?: ""
                                val startTime = scheduleSnapshot.child("startTime").value as? String ?: ""
                                val endTime = scheduleSnapshot.child("endTime").value as? String ?: ""
                                val key = scheduleSnapshot.key ?: ""

                                // ScheduleItem을 생성하여 리스트에 추가
                                val scheduleItem = ScheduleItem(schedule, startTime, endTime, date, key)
                                scheduleList.add(scheduleItem)
                            }
                        }

                        // 어댑터에 데이터 설정
                        scheduleAdapter.setData(scheduleList, userName)
                        scheduleAdapter.notifyDataSetChanged()

                        // 리스트가 비어있을 때 메시지 표시
                        if (scheduleList.isEmpty()) {
                            diaryContent.text = "일정이 없습니다."
                        } else {
                            diaryContent.text = "" // 일정이 있으면 기존 메시지를 지움
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        showToast("일정 불러오기 실패: ${databaseError.message}")
                    }
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {
                showToast("이름 불러오기 실패: ${databaseError.message}")
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToLoginActivity() {
        // 로그인 액티비티로 이동하는 코드
    }

    private fun navigateToScheduleList() {
        val intent = Intent(this, ScheduleListActivity::class.java)
        intent.putExtra("userID", userID)
        startActivity(intent)

    }
    private fun navigateToScheduleInputActivity() {
        val intent = Intent(this, ScheduleInputActivity::class.java)
        intent.putExtra("selectedDate", selectedDate)
        startActivity(intent)

    }

}
