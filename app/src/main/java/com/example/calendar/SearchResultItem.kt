package com.example.calendar

data class SearchResultItem(val schedule: String)
data class Schedule(
    val schedule: String?,
    val startTime: String?,
    val endTime: String?,
    val date: String?
) {
    // 매개변수가 없는 기본 생성자 추가
    constructor() : this("", "", "", "")
}