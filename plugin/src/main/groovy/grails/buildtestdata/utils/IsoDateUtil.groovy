package grails.buildtestdata.utils
import groovy.transform.CompileStatic

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.function.Supplier
import java.util.regex.Pattern

/**
 * Provides a set of methods for parsing/formatting ISO 8601 dates.
 * https://en.wikipedia.org/wiki/ISO_8601
 *
 */
@SuppressWarnings(['EmptyCatchBlock'])
@CompileStatic
class IsoDateUtil {
    //yyyy-MM-dd "2017-12-27"
    static final Pattern LOCAL_DATE = ~/\d{4}-\d{2}-\d{2}$/
    //yyyy-MM-dd'T'HH:mm:ss.SSSZ
    static final Pattern GMT_MILLIS = ~/\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}Z/
    //yyyy-MM-dd'T'HH:mm:ssZ
    static final Pattern GMT_SECONDS = ~/\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}Z/
    //yyyy-MM-dd'T'HH:mm:ss
    static final Pattern TZ_LESS = ~/\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}/
    //yyyy-MM-dd'T'HH:mm
    static final Pattern TZ_LESS_HH_MM = ~/\d{4}-\d{2}-\d{2}T\d{2}:\d{2}/

    //see https://stackoverflow.com/questions/4032967/json-date-to-java-date#4033027
    static final ThreadLocal<SimpleDateFormat> LOCAL_DATE_FORMAT = ThreadLocal.withInitial({
        SimpleDateFormat fmatter = new SimpleDateFormat("yyyy-MM-dd")
        https://stackoverflow.com/questions/2891361/how-to-set-time-zone-of-a-java-util-date
        fmatter.setTimeZone(TimeZone.getTimeZone('UTC'))
        return fmatter
    } as Supplier<SimpleDateFormat>)

    static final ThreadLocal<SimpleDateFormat> DATE_TIME_FORMAT = ThreadLocal.withInitial({
        SimpleDateFormat fmatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        fmatter.setTimeZone(TimeZone.getTimeZone('UTC'))
        return fmatter
    } as Supplier<SimpleDateFormat>)

    /**
     * Parse date sent by client (mostly JSON like).
     * Expected formats: 2000-03-30, 2000-03-30T22:11:22.123Z , 2000-03-30T22:00:00Z or yyyy-MM-dd'T'HH:mm:ss
     * Assumes all timeZones are UTC
     *
     * //see https://stackoverflow.com/questions/10286204/the-right-json-date-format
     *
     * @param date formatted date
     * @return parsed date
     * @throws java.text.ParseException if it cannot recognize a date format
     */
    static Date parse(String date) {
        date = date?.trim()
        if (!date) return null

        //default for GMT_MILLIS match
        DateFormat dateFormat = DATE_TIME_FORMAT.get()

        //if-then is slightly faster than a switch here
        if (date.matches(GMT_MILLIS)) {
            return dateFormat.parse(date)
        } else if (date.matches(LOCAL_DATE)) {
            dateFormat = LOCAL_DATE_FORMAT.get()
        } else if (date.matches(GMT_SECONDS)) {
            date = date.replaceFirst('Z$', '.000Z')
        } else if (date.matches(TZ_LESS)) {
            date = "${date}.000Z"
        } else if (date.matches(TZ_LESS_HH_MM)) {
            date = "${date}:00.000Z"
        }

        return dateFormat.parse(date)
    }

    static LocalDate parseLocalDate(String date) {
        date = date?.trim()
        if (!date) return null

        try {
            return LocalDate.parse(date)
        } catch (DateTimeParseException e) {
            //try with full dateTime
            return LocalDate.parse(date, DateTimeFormatter.ISO_DATE_TIME)
        }

    }

    static LocalDateTime parseLocalDateTime(String date) {
        date = date?.trim()
        if (!date) return null

        if (date.matches(LOCAL_DATE)) {
            date = "${date}T00:00"
        }
        LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)

    }
}