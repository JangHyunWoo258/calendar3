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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calendar.HomeFragment
import com.example.calendar.LogoutFragment
import com.example.calendar.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
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
    private lateinit var titleTextView: TextView

    // BottomNavigationView에서 사용할 프래그먼트들
    private val homeFragment = HomeFragment()
    private val searchFragment = SearchFragment()
    private val logoutFragment = LogoutFragment()
    override fun onBackPressed() {
        // 아무런 동작도 하지 않도록 비워 둡니다.
        // 또는 필요에 따라 특정 동작을 수행할 수 있습니다.
        // super.onBackPressed() // 이 부분을 주석 해제하면 기본 동작(화면 닫기)이 수행됩니다.
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        saveBtn = findViewById(R.id.saveBtn)
        updateBtn = findViewById(R.id.updateBtn)
        deleteBtn = findViewById(R.id.deleteBtn)
        diaryContent = findViewById(R.id.diaryContent)
        calendarView = findViewById(R.id.calendarView)
        titleTextView = findViewById(R.id.title)

        userID = intent.getStringExtra("userID") ?: ""
        database = FirebaseDatabase.getInstance().reference
        val firebaseAuth = FirebaseAuth.getInstance()

        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            navigateToLoginActivity()
            return
        }
        currentUser.uid.let { uid ->
            val userRef = FirebaseDatabase.getInstance().reference.child("users").child(uid)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val name = dataSnapshot.child("name").getValue(String::class.java)
                    name?.let {
                        titleTextView.text = it
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // 에러 처리
                }
            })
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

        // BottomNavigationView 처리
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        bottomNavigationView.selectedItemId = R.id.menu_home
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    replaceFragment(homeFragment)
                    true
                }
                R.id.menu_search -> {
                    startActivity(Intent(this, SearchActivity::class.java))

                    true
                }
                R.id.menu_logout -> {
                    replaceFragment(logoutFragment)
                    showLogoutConfirmationDialog()
                    true

                }
                else -> false
            }
        }

        // 초기 화면 설정
        replaceFragment(homeFragment)

        // 일정 목록을 불러오는 함수 호출

        loadDiary(selectedDate)


    }
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }


    private fun loadDiary(date: String) {
        val scheduleRef = database.child("schedules")

        scheduleRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val scheduleList = mutableListOf<ScheduleItem>()


                for (userSnapshot in dataSnapshot.children) {
                    val userName = userSnapshot.child("name").value as? String ?: ""



                    for (scheduleSnapshot in userSnapshot.children) {
                        val scheduleDate = scheduleSnapshot.child("date").value as? String ?: ""
                        if (scheduleDate == date) {
                            val schedule = scheduleSnapshot.child("schedule").value as? String ?: ""
                            val startTime = scheduleSnapshot.child("startTime").value as? String ?: ""
                            val endTime = scheduleSnapshot.child("endTime").value as? String ?: ""
                            val key = scheduleSnapshot.key ?: ""
                            val userName = scheduleSnapshot.child("userName").value as? String ?: ""
                            println("UserName: $userName")

                            // ScheduleItem을 생성하여 리스트에 추가
                            val scheduleItem = ScheduleItem(schedule, startTime, endTime, date, key, userName)
                            scheduleList.add(scheduleItem)
                        }
                    }
                }

                // 어댑터에 데이터 설정
                scheduleAdapter.setData(scheduleList)
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
    private fun showLogoutConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("로그아웃 확인")
        alertDialogBuilder.setMessage("로그아웃 하시겠습니까?")

        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            logoutUser()
        }

        alertDialogBuilder.setNegativeButton("No") { _, _ ->
            // 다이얼로그를 닫거나 추가 작업 수행
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        navigateToLoginActivity()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToLoginActivity() {
        // 로그인 액티비티로 이동하는 코드
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
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