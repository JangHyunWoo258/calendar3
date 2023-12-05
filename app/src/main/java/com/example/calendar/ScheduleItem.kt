package com.example.calendar

data class ScheduleItem(
    val schedule: String,
    val startTime: String,
    val endTime: String,
    val date: String,
    var key: String = "",
    var userName: String
) {
    // 매개변수 없는 생성자
    constructor() : this("", "", "", "","","")
}