package helpers

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateHelper {

    def stringToLocalDate(date: String, formatPattern:String = "dd.MM.yyyy"): LocalDate =
        java.time.LocalDate.parse(date, DateTimeFormatter.ofPattern(formatPattern))

}
