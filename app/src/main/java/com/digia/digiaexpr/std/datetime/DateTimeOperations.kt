package com.digia.digiaexpr.std.datetime

import com.digia.digiaexpr.callable.ExprCallable
import com.digia.digiaexpr.evaluator.ASTEvaluator
import com.digia.digiaexpr.std.util.toValue
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateTimeOperations {
    val functions: Map<String, ExprCallable> = mapOf(
        "isoFormat" to IsoFormatOp()
    )
}

class IsoFormatOp : ExprCallable {

    override val name: String = "isoFormat"

    override fun arity(): Int = 2

    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
        if (arguments.size < arity()) {
            throw IllegalArgumentException("Incorrect argument size")
        }

        val iso: String? = toValue(evaluator, arguments[0])
        val format: String? = toValue(evaluator, arguments[1])

        return isoFormat(iso, format)
    }

    fun isoFormat(iso: String?, momentFormat: String?): String? {
        if (iso.isNullOrBlank() || momentFormat.isNullOrBlank()) return null

        val instant = try {
            Instant.parse(iso)
        } catch (e: Exception) {
            return null
        }

        val zoned = instant.atZone(ZoneId.systemDefault())

        val hasOrdinal = momentFormat.contains("Do")

        val javaFormat = momentToJavaFormat(momentFormat)
        var result = DateTimeFormatter.ofPattern(javaFormat).format(zoned)

        if (hasOrdinal) {
            val day = zoned.dayOfMonth
            result = result.replace(
                "{ORDINAL}",
                ordinal(day)
            )
        }

        return result
    }

    private fun ordinal(day: Int): String {
        return when {
            day in 11..13 -> "${day}th"
            day % 10 == 1 -> "${day}st"
            day % 10 == 2 -> "${day}nd"
            day % 10 == 3 -> "${day}rd"
            else -> "${day}th"
        }
    }

    /**
     * Converts Moment.js format → Java DateTimeFormatter format
     * IMPORTANT: order matters (longest tokens first)
     */
    private fun momentToJavaFormat(format: String): String {
        return format
            // Ordinal placeholder as Java-safe literal
            .replace("Do", "'__ORDINAL__'")

            // Year
            .replace("YYYY", "yyyy")
            .replace("YY", "yy")

            // Month
            .replace("MMMM", "MMMM")
            .replace("MMM", "MMM")
            .replace("MM", "MM")

            // Day
            .replace("DD", "dd")
            .replace("D", "d")
    }
}


//    private fun isoFormat(isoString: String?, format: String?): String? {
//        if (isoString == null || format == null) {
//            return null
//        }
//
//        return try {
//            // Parse ISO 8601 using SimpleDateFormat
//            val isoParser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH).apply {
//                timeZone = TimeZone.getTimeZone("UTC")
//            }
//            val date = isoParser.parse(isoString) ?: return null
//            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH).apply {
//                time = date
//            }
//
//            // Strip outer quotes if present
//            val processedFormat = if (format.startsWith("'") && format.endsWith("'")) {
//                format.substring(1, format.length - 1)
//            } else {
//                format
//            }
//
//            // Handle 'Do' pattern for ordinal day (1st, 2nd, 3rd, etc.)
//            val result = if (processedFormat.contains("Do")) {
//                val day = calendar.get(Calendar.DAY_OF_MONTH)
//                val ordinalSuffix = when {
//                    day in 11..13 -> "th"
//                    day % 10 == 1 -> "st"
//                    day % 10 == 2 -> "nd"
//                    day % 10 == 3 -> "rd"
//                    else -> "th"
//                }
//
//                // Replace Do with ordinal day and format the rest
//                val formatWithoutDo = processedFormat.replace("Do", "").trim()
//
//                val formatted = if (formatWithoutDo.isNotEmpty()) {
//                    val monthFormat = convertMomentToJavaFormat(formatWithoutDo)
//                    " " + SimpleDateFormat(monthFormat, Locale.ENGLISH).format(date)
//                } else {
//                    ""
//                }
//
//                "$day$ordinalSuffix$formatted"
//            } else {
//                // Convert moment.js format to Java SimpleDateFormat
//                val javaFormat = convertMomentToJavaFormat(processedFormat)
//                SimpleDateFormat(javaFormat, Locale.ENGLISH).format(date)
//            }
//
//            result
//        } catch (e: Exception) {
//            null
//        }
//    }
//
//    private fun convertMomentToJavaFormat(momentFormat: String): String {
//        return momentFormat
//            .replace("YYYY", "yyyy")
//            .replace("YY", "yy")
//            .replace("MMMM", "MMMM")
//            .replace("MMM", "MMM")
//            .replace("MM", "MM")
//            .replace("DD", "dd")
//            .replace("D", "d")
//            .replace("HH", "HH")
//            .replace("H", "H")
//            .replace("mm", "mm")
//            .replace("ss", "ss")
//    }
//}

//abstract class DateTimeOperations {
//    companion object {
//        val functions: Map<String, ExprCallable> = mapOf("isoFormat" to _IsoFormatOp())
//    }
//}
//
//class _IsoFormatOp : ExprCallable {
//    override fun arity() = 2
//
//    override fun call(evaluator: ASTEvaluator, arguments: List<Any>): Any? {
//        if (arguments.size < arity()) {
//            return "Incorrect argument size"
//        }
//
//        val isoStringDate = toValue<String>(evaluator, arguments[0])
//        val path = toValue<String>(evaluator, arguments[1])
//
//        return _isoFormat(isoStringDate, path)
//    }
//
//    override val name: String
//        get() = "isoFormat"
//}
//
//fun _isoFormat(isoString: String?, format: String?): String? {
//    if (isoString == null || format == null) {
//        return null
//    }
//
//    val dateTime = ZonedDateTime.parse(isoString)
//    val formatter = DateTimeFormatter.ofPattern(format)
//    return dateTime.format(formatter)
//}
