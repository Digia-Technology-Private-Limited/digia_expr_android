package com.digia.digiaexpr

import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class DateDebugTest {
    @Test
    fun testDateParsing() {
        val isoString = "2024-06-03T23:42:36Z"
        val isoParser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val date = isoParser.parse(isoString)!!
        val calendar = Calendar.getInstance().apply { time = date }
        
        println("Date: $date")
        println("Calendar timezone: ${calendar.timeZone}")
        println("Day of month: ${calendar.get(Calendar.DAY_OF_MONTH)}")
        println("Hour: ${calendar.get(Calendar.HOUR_OF_DAY)}")
    }
}
