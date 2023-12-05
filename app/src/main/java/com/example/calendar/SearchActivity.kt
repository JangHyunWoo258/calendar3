package com.example.calendar

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher

import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchActivity : AppCompatActivity() {

    private lateinit var editTextSearch: EditText
    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var searchAdapter: SearchAdapter

    private lateinit var database: DatabaseReference
    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        editTextSearch = findViewById(R.id.editTextSearch)
        searchRecyclerView = findViewById(R.id.searchRecyclerView)
        searchAdapter = SearchAdapter(this)

        database = FirebaseDatabase.getInstance().reference
        val firebaseAuth = FirebaseAuth.getInstance()

        // 사용자 ID 가져오기
        userID = firebaseAuth.currentUser?.uid ?: ""

        searchRecyclerView.adapter = searchAdapter
        searchRecyclerView.layoutManager = LinearLayoutManager(this)

        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 텍스트 변경 전 호출
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트가 변경될 때 호출
            }

            override fun afterTextChanged(s: Editable?) {
                // 텍스트 변경 후 호출
                performSearch(s.toString())
            }
        })
    }

    private fun performSearch(query: String) {
        val queryRef = database.child("schedules").child(userID)
            .orderByChild("schedule")
            .startAt(query)
            .endAt(query + "\uf8ff")

        queryRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val searchResults = mutableListOf<ScheduleItem>()

                for (scheduleSnapshot in snapshot.children) {
                    val schedule = scheduleSnapshot.getValue(ScheduleItem::class.java)
                    if (schedule != null) {
                        searchResults.add(schedule)
                    }
                }

                searchAdapter.setSearchResults(searchResults)
                searchAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("검색 실패: ${error.message}")
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}