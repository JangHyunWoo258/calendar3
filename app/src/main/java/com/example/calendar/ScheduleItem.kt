package com.example.calendar

data class ScheduleItem(
    val schedule: String,
    val startTime: String,
    val endTime: String,
    val date: String, // 이 부분을 추가
    var key: String =""
    // ... 기타 프로퍼티들
){
    // 매개변수가 없는 생성자 추가
    constructor() : this("", "", "", "", "")
}