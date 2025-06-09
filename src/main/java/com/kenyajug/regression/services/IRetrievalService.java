package com.kenyajug.regression.services;
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
import com.kenyajug.regression.entities.AppLog;
import com.kenyajug.regression.entities.LogsMetadata;
import com.kenyajug.regression.resources.ApplicationResource;
import com.kenyajug.regression.resources.DatasourceResource;
import com.kenyajug.regression.resources.LogResource;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IRetrievalService {
    /**
     * Retrieves a list of log resources filtered by the specified date, optional severity level,
     * application identifier, and data source.
     *
     * @param date          the date for which logs should be retrieved (required).
     * @param severity      an optional severity filter (e.g., "INFO", "WARN", "ERROR"). If null or empty, all severities are included.
     * @param applicationId the ID of the application to filter logs by. If null or empty, logs from all applications are included.
     * @param datasource    the ID of the data source to filter logs by. If null or empty, logs from all data sources are included.
     * @return a list of {@link LogResource} objects matching the given filters.
     */
    List<LogResource> listLogs(LocalDate date, String severity, String applicationId, String datasource);
    /**
     * Retrieves all log entries recorded for the current day.
     *
     * @return a list of {@link LogResource} objects representing today's logs,
     *         regardless of severity, application, or data source.
     */
    List<LogResource> listAllTodayLogs();
    /**
     * Retrieves a list of all available application resources.
     *
     * @return a list of {@link ApplicationResource} objects representing all applications.
     */
    List<ApplicationResource> listAllApplications();
    /**
     * Retrieves a list of all available data source resources.
     *
     * @return a list of {@link DatasourceResource} objects representing all configured data sources.
     */
    List<DatasourceResource> listAllDataSources();
    /**
     * Composes a list of hourly log counts for a given severity level on a specific date.
     *
     * <p>This method aggregates log data for a 24-hour period (from 00:00 to 23:59)
     * and returns a list of 24 integers, where each index corresponds to an hour of the day.
     * The value at each index represents the number of log entries that occurred
     * during that hour for the specified severity level.</p>
     *
     * @param logsDate the date for which to collect log data (must not be null)
     * @param severity the log severity level to filter by (e.g., "INFO", "WARN", "ERROR")
     * @return a list of 24 longs representing hourly log counts for the given severity;
     *         the list is zero-filled for hours with no log entries
     * @throws IllegalArgumentException if the severity is null or not recognized
     */
    List<Long> composeChartDataBySeverity(LocalDate logsDate, String severity);
    /**
     * Finds a log resource by its unique identifier.
     *
     * @param logId the unique identifier of the log to find
     * @return an {@code Optional} containing the {@code LogResource} if found, or empty if no log matches the given ID
     */
    Optional<LogResource> findLogsById(String logId);
    /**
     * Finds a log metadata resource by its logs unique identifier.
     *
     * @param logId the unique identifier of the log to find
     * @return an {@code List} containing the {@code LogsMetadata} list if found, or empty if no log matches the given ID
     */
    List<LogsMetadata> findMetadataByLogId(String logId);


    Optional<AppLog> findAppLogById(String logId);
}
