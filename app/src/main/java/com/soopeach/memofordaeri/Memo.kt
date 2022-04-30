package com.soopeach.memofordaeri

data class Memo (
    // 메모의 제목
    val title : String,
    // 메모의 내용
    val content : String,
    // 메모가 작성된 날짜
    val date : String,
    // 비밀메모 여뷰
    val secret : Boolean
)