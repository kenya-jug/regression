package com.kenyajug.regression.utility_tests;
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
import com.kenyajug.regression.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

@Slf4j
public class DateTimeUtilsTest {
    @Test
    public void shouldParseLocalDateToStringTest() {
        var localDate = LocalDate.of(2001, 1, 18);
        var dateString = DateTimeUtils.convertDateToString(localDate);
        assertThat(dateString).isNotNull();
        assertThat(dateString).contains("2001");
        assertThat(dateString).contains("18");
        assertThat(dateString).isEqualTo("2001-01-18");
    }
    @Test
    public void shouldConvertStringToLocalDateTest() {
        var dateString = "2001-01-18";
        var localDate = DateTimeUtils.convertStringToLocalDate(dateString);
        assertThat(localDate).isNotNull();
    }
    @Test
    public void shouldConvertDateTimeToStringTest() {
        var expectedTimeStamp = LocalDateTime.of(2022, 12, 26, 8, 25, 12);
        var timeStampString = "2022-12-26 08:25:12";
        var timeStamp = DateTimeUtils.convertDateTimeStringToLocalDateTime(timeStampString);
        assertThat(timeStamp).isNotNull();
        assertThat(timeStamp.getYear()).isEqualTo(expectedTimeStamp.getYear());
        assertThat(timeStamp.getMonth()).isEqualTo(expectedTimeStamp.getMonth());
        assertThat(timeStamp.getDayOfMonth()).isEqualTo(expectedTimeStamp.getDayOfMonth());
        assertThat(timeStamp.getHour()).isEqualTo(expectedTimeStamp.getHour());
        assertThat(timeStamp.getMinute()).isEqualTo(expectedTimeStamp.getMinute());
        assertThat(timeStamp.getSecond()).isEqualTo(expectedTimeStamp.getSecond());
    }
    @Test
    public void shouldConvertUTCTimeStringToUTCZonedDateTime_Test(){
        var localDateTime = LocalDateTime.of(2023,11,22,5,30,12);
        var zonedTime = ZonedDateTime.of(localDateTime, DateTimeUtils.UTC_TIME_ZONE_ID);
        var zonedTimeString = DateTimeUtils.convertUTCZonedDateTimeToString(zonedTime);
        log.info("zonedTimeString: {}", zonedTimeString);
        assertThat(zonedTimeString).isNotNull();
        assertThat(zonedTimeString).isNotEmpty();
        assertThat(zonedTimeString).isEqualTo("2023-11-22 05:30:12 UTC");
        var _zonedDateTime = DateTimeUtils.convertUTCTimeStringToUTCZonedDateTime(zonedTimeString);
        log.info("_zonedDateTime {}",_zonedDateTime);
        assertThat(_zonedDateTime).isNotNull();
        assertThat(_zonedDateTime.getYear()).isEqualTo(2023);
        assertThat(_zonedDateTime.getMonthValue()).isEqualTo(11);
        assertThat(_zonedDateTime.getDayOfMonth()).isEqualTo(22);
        assertThat(_zonedDateTime.getHour()).isEqualTo(5);
        assertThat(_zonedDateTime.getMinute()).isEqualTo(30);
        assertThat(_zonedDateTime.getSecond()).isEqualTo(12);
    }
    @Test
    public void shouldValidateTimeStampTest(){
        var correctTimeStamp = "2023-11-22 05:30:12 UTC";
        var incorrectTimeStamp = "2023-11-22 05:30:12";
        var incorrectTimeStamp2 = "2023-11-22 05:30:12Z";
        assertThat(DateTimeUtils.isValidUTCTimestamp(correctTimeStamp)).isTrue();
        assertThat(DateTimeUtils.isValidUTCTimestamp(incorrectTimeStamp)).isFalse();
        assertThat(DateTimeUtils.isValidUTCTimestamp(incorrectTimeStamp2)).isFalse();
    }
    @Test
    public void shouldConvertZonedUTCTimeStringToLocalDateTime_Test(){
        var zonedDateTimeString = "2023-11-22 05:30:12 UTC";
        var expectedLocalDateTime = LocalDateTime.of(2023,11,22,5,30,12);
        var actualDateTime = DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime(zonedDateTimeString);
        assertThat(actualDateTime).isNotNull();
        assertThat(actualDateTime.isEqual(expectedLocalDateTime)).isTrue();
    }
    @Test
    public void shouldGenerateNowUTCTime_Test(){
        var nowUTC = DateTimeUtils.nowUTCTime();
        assertThat(nowUTC).isNotNull();
        log.info("Now UTC Time {}", nowUTC);
    }
    @Test
    public void should_ConvertLocalDateTimeToUTCTime_Test(){
        var localDateTime = LocalDateTime.of(2023,11,22,5,30,12);
        var utcTimeString = DateTimeUtils.localDateTimeToUTCTime(localDateTime);
        assertThat(utcTimeString).isNotEmpty();
        assertThat(utcTimeString).isEqualTo("2023-11-22 05:30:12 UTC");
    }
    @Test
    void shouldFormatLocalDateTimeCorrectly() {
        var input = LocalDateTime.of(2023, 5, 8, 14, 30, 15);
        var expected = "2023-05-08 14:30:15";
        var result = DateTimeUtils.convertLocalDateTimeToString(input);
        assertThat(expected).isEqualTo(result);
    }
}