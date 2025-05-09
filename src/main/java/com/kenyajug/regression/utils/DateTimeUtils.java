package com.kenyajug.regression.utils;
/*
 * MIT License
 *
 * Copyright (c) 2025 Kenya JUG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;
public final class DateTimeUtils {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter ZONED_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
    public static final String TIMESTAMP_REGEX = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2} UTC$";
    public static final Pattern TIMESTAMP_PATTERN = Pattern.compile(TIMESTAMP_REGEX);
    public static final ZoneId UTC_TIME_ZONE_ID = ZoneId.of("UTC");
    private DateTimeUtils(){}
    /**
     * Converts a {@link LocalDate} to a formatted string using {@code DATE_FORMATTER}.
     *
     * @param localDate the LocalDate to convert
     * @return a formatted date string
     * Might throw DateTimeException if the date cannot be formatted
     */
    public static String convertDateToString(LocalDate localDate) {
        var dateTimeFormatter = DATE_FORMATTER;
        return localDate.format(dateTimeFormatter);
    }
    /**
     * Parses a formatted date string into a {@link LocalDate} using {@code DATE_FORMATTER}.
     *
     * @param dateString the date string to parse
     * @return a LocalDate parsed from the input string
     * Might throw DateTimeParseException if the input string cannot be parsed
     */
    public static LocalDate convertStringToLocalDate(String dateString) {
        var formatter = DATE_FORMATTER;
        return LocalDate.from(formatter.parse(dateString));
    }
    /**
     * Parses a date-time string into a {@link LocalDateTime} using {@code DATE_TIME_FORMATTER}.
     *
     * @param dateTimeString the date-time string to parse
     * @return a LocalDateTime parsed from the input string
     * Might throw DateTimeParseException if the input string cannot be parsed
     */
    public static LocalDateTime convertDateTimeStringToLocalDateTime(String dateTimeString) {
        var temporalValue = DATE_TIME_FORMATTER.parse(dateTimeString);
        return LocalDateTime.from(temporalValue);
    }
    /**
     * Converts a UTC zoned date-time string to a {@link LocalDateTime}, ignoring the zone offset.
     *
     * @param dateTimeString the UTC zoned date-time string to parse
     * @return a LocalDateTime extracted from the input string
     * Might throw DateTimeParseException if the input string cannot be parsed
     */
    public static LocalDateTime convertZonedUTCTimeStringToLocalDateTime(String dateTimeString) {
        var temporalValue = ZONED_DATE_TIME_FORMATTER.parse(dateTimeString);
        return LocalDateTime.from(temporalValue);
    }
    /**
     * Converts a {@link LocalDateTime} to a formatted date-time string using {@code DATE_TIME_FORMATTER}.
     *
     * @param localDateTime the LocalDateTime to convert
     * @return a formatted date-time string
     * Might throw DateTimeException if the date-time cannot be formatted
     */
    public static String convertLocalDateTimeToString(LocalDateTime localDateTime) {
        return localDateTime.format(DATE_TIME_FORMATTER);
    }
    /**
     * Converts a formatted UTC zoned date-time string to a {@link ZonedDateTime} in UTC.
     *
     * @param zonedDateTimeString the date-time string formatted using {@code ZONED_DATE_TIME_FORMATTER}
     * @return a ZonedDateTime object in UTC
     * Might throw DateTimeParseException if the input string cannot be parsed
     */
    public static ZonedDateTime convertUTCTimeStringToUTCZonedDateTime(String zonedDateTimeString) {
        var temporalValue = ZONED_DATE_TIME_FORMATTER.parse(zonedDateTimeString);
        var zonedDateTime = ZonedDateTime.from(temporalValue);
        return zonedDateTime.withZoneSameInstant(UTC_TIME_ZONE_ID);
    }
    /**
     * Converts a {@link ZonedDateTime} to a formatted string in UTC using {@code ZONED_DATE_TIME_FORMATTER}.
     *
     * @param zonedDateTime the ZonedDateTime to format
     * @return a formatted UTC date-time string
     */
    public static String convertUTCZonedDateTimeToString(ZonedDateTime zonedDateTime) {
        var _zonedDateTime = zonedDateTime.withZoneSameInstant(UTC_TIME_ZONE_ID);
        return _zonedDateTime.format(ZONED_DATE_TIME_FORMATTER);
    }
    /**
     * Validates whether the given string matches the UTC timestamp pattern defined by {@code TIMESTAMP_PATTERN}.
     *
     * @param timestamp the timestamp string to validate
     * @return true if the string is a valid UTC timestamp, false otherwise
     */
    public static boolean isValidUTCTimestamp(String timestamp) {
        return TIMESTAMP_PATTERN.matcher(timestamp).matches();
    }
    /**
     * Returns the current UTC time as a {@link LocalDateTime}.
     *
     * @return the current UTC time
     */
    public static LocalDateTime nowUTCTime() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(UTC_TIME_ZONE_ID);
        return LocalDateTime.of(
                zonedDateTime.getYear(),
                zonedDateTime.getMonth(),
                zonedDateTime.getDayOfMonth(),
                zonedDateTime.getHour(),
                zonedDateTime.getMinute(),
                zonedDateTime.getSecond()
        );
    }
    /**
     * Converts a {@link LocalDateTime} to a formatted UTC date-time string.
     *
     * @param localDateTime the LocalDateTime to convert
     * @return a formatted UTC date-time string
     */
    public static String localDateTimeToUTCTime(LocalDateTime localDateTime) {
        var zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of("UTC"));
        return convertUTCZonedDateTimeToString(zonedDateTime);
    }
}
