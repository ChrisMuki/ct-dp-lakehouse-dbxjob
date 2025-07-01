package ct.dna.utils

import java.sql.Date
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

object UTC {
  def dateOf(days: Int) = new Date(86400000L * days)

  /** Obtains an instance of `java.sql.Date` from a text string such as `"2007-12-03"`.
    *
    * The string must represent a valid date and is parsed using [[java.time.format.DateTimeFormatter#ISO_LOCAL_DATE]].
    *
    * @param text
    *   the text to parse, such as `"2007-12-03"`, must not be null
    * @return
    *   the parsed local date, never null
    * @throws java.time.format.DateTimeParseException
    *   if the text cannot be parsed
    */
  def dateOf(text: CharSequence) = new Date(LocalDate.parse(text).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli())

  /** Obtains an instance of `java.sql.Timestamp` from a text string such as `2007-12-03T10:15:30.00Z` . <p> The string must represent a valid instant in UTC
    * and is parsed using [[java.time.format.DateTimeFormatter.ISO_INSTANT]].
    *
    * @param text
    *   the text to parse, not null
    * @return
    *   the parsed java.sql.Timestamp, not null
    * @throws DateTimeParseException
    *   if the text cannot be parsed
    */
  def timestampOf(text: CharSequence) = Timestamp.from(Instant.parse(text))

  def timestampOf(epochSecond: Long, nanoAdjustment: Long) = Timestamp.from(java.time.Instant.ofEpochSecond(epochSecond, nanoAdjustment))

  def requireUTC = assert(
    java.time.ZoneId.systemDefault == java.time.ZoneId.of("UTC"),
    "For these operations it is reuired to use UTC system clock. Consider javaOption '-Duser.timezone=UTC'"
  )
}
