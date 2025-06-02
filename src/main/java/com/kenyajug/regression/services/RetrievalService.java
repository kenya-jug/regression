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
import com.kenyajug.regression.entities.LogsDataSource;
import com.kenyajug.regression.entities.LogsMetadata;
import com.kenyajug.regression.repository.AppLogRepository;
import com.kenyajug.regression.repository.ApplicationsRepository;
import com.kenyajug.regression.repository.LogsDataSourceRepository;
import com.kenyajug.regression.repository.LogsMetadataRepository;
import com.kenyajug.regression.resources.ApplicationResource;
import com.kenyajug.regression.resources.DatasourceResource;
import com.kenyajug.regression.resources.LogResource;
import com.kenyajug.regression.utils.DateTimeUtils;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
@Service
public class RetrievalService implements IRetrievalService{
    private final AppLogRepository logRepository;
    private final ApplicationsRepository applicationsRepository;
    private final LogsDataSourceRepository dataSourceRepository;
    private final LogsMetadataRepository metadataRepository;
    public RetrievalService(AppLogRepository logRepository, ApplicationsRepository applicationsRepository, LogsDataSourceRepository dataSourceRepository, LogsMetadataRepository metadataRepository) {
        this.logRepository = logRepository;
        this.applicationsRepository = applicationsRepository;
        this.dataSourceRepository = dataSourceRepository;
        this.metadataRepository = metadataRepository;
    }
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
    @Override
    public List<LogResource> listLogs(LocalDate date, String severity, String applicationId, String datasource) {
        var optionalApplication = applicationsRepository.findById(applicationId);
        if (optionalApplication.isEmpty()) return List.of();
        var application = optionalApplication.get();
        var applicationResource = new ApplicationResource(application.uuid(),application.name(),application.runtimeEnvironment());
        var datasoureList = dataSourceRepository.findByApplicationId(application.uuid());
        List<LogResource> result = new ArrayList<>();
        for (LogsDataSource logsDataSource : datasoureList) {
            List<AppLog> logs = logRepository.findByApplicationAndDatasource(application.uuid(),logsDataSource.uuid())
                    .stream()
                    .filter(e -> DateTimeUtils.isSameDay(e.timestamp().toLocalDate(),date))
                    .toList();
            if (!(severity.isEmpty()) && !(severity.equals("All")))
                logs = logs.stream().filter(e -> e.severity().equals(severity)).toList();
            for (AppLog log : logs) {
                var time = log.timestamp().toLocalTime();
                var timeFormatter = DateTimeUtils.localTimeString(time);
                var datasourceResource = new DatasourceResource(logsDataSource.name(),logsDataSource.uuid());
                var resource = new LogResource(
                        timeFormatter,
                        log.severity(),
                        applicationResource,
                        datasourceResource,
                        log.uuid(),
                        log.message());
                result.add(resource);
            }
        }
        return result;
    }
    /**
     * Retrieves all log entries recorded for the current day.
     *
     * @return a list of {@link LogResource} objects representing today's logs,
     * regardless of severity, application, or data source.
     */
    @Override
    public List<LogResource> listAllTodayLogs() {
        List<AppLog> logs = logRepository.findAll()
                .stream()
                .filter(e -> DateTimeUtils.isSameDay(e.timestamp().toLocalDate(), LocalDate.now()))
                .toList();
        List<LogResource> result = new ArrayList<>();
        for (AppLog log : logs) {
            var time = log.timestamp().toLocalTime();
            var timeFormatter = DateTimeUtils.localTimeString(time);
            var datasourceId = log.logSource();
            var optionalDatasource = dataSourceRepository.findById(datasourceId);
            if (optionalDatasource.isEmpty()) return List.of();
            var logsDataSource = optionalDatasource.get();
            var applicationId = logsDataSource.applicationId();
            var optionalApplication = applicationsRepository.findById(applicationId);
            if (optionalApplication.isEmpty()) return List.of();
            var application = optionalApplication.get();
            var applicationResource = new ApplicationResource(application.uuid(),application.name(),application.runtimeEnvironment());
            var datasourceResource = new DatasourceResource(logsDataSource.name(),logsDataSource.uuid());
            var resource = new LogResource(
                    timeFormatter,
                    log.severity(),
                    applicationResource,datasourceResource,
                    log.uuid(),
                    log.message());
            result.add(resource);
        }
        return result;
    }
    /**
     * Retrieves a list of all available application resources.
     *
     * @return a list of {@link ApplicationResource} objects representing all applications.
     */
    @Override
    public List<ApplicationResource> listAllApplications() {
        return applicationsRepository.findAll()
                .stream()
                .map(e -> new ApplicationResource(e.uuid(),e.name(),e.runtimeEnvironment()))
                .toList();
    }
    /**
     * Retrieves a list of all available data source resources.
     *
     * @return a list of {@link DatasourceResource} objects representing all configured data sources.
     */
    @Override
    public List<DatasourceResource> listAllDataSources() {
        return dataSourceRepository.findAll()
                .stream()
                .map(e -> new DatasourceResource(e.name(),e.uuid()))
                .toList();
    }
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
     * the list is zero-filled for hours with no log entries
     * @throws IllegalArgumentException if the severity is null or not recognized
     */
    @Override
    public List<Long> composeChartDataBySeverity(LocalDate logsDate, String severity) {
        var allLogs = logRepository.findBySeverityAndDate(severity, logsDate)
                .stream()
                .sorted(Comparator.comparing(AppLog::timestamp))
                .toList();
        var allHours = DateTimeUtils.ALL_HOURS;
        List<Long> hourlyCount = new ArrayList<>();
        for (LocalTime hour : allHours) {
            var count = allLogs.stream().filter(e -> e.timestamp().getHour() == hour.getHour()).count();
            hourlyCount.add(count);
        }
        return hourlyCount;
    }
    /**
     * Finds a log resource by its unique identifier.
     *
     * @param logId the unique identifier of the log to find
     * @return an {@code Optional} containing the {@code LogResource} if found, or empty if no log matches the given ID
     */
    @Override
    public Optional<LogResource> findLogsById(String logId) {
        var appLog = logRepository.findById(logId);
        if (appLog.isEmpty()) return Optional.empty();
        var optionalDatasource = dataSourceRepository.findById(appLog.get().logSource());
        if (optionalDatasource.isEmpty()) return Optional.empty();
        var logsDataSource = optionalDatasource.get();
        var applicationId = logsDataSource.applicationId();
        var optionalApplication = applicationsRepository.findById(applicationId);
        if (optionalApplication.isEmpty()) return Optional.empty();
        var application = optionalApplication.get();
        var applicationResource = new ApplicationResource(
                application.uuid(),
                application.name(),
                application.runtimeEnvironment()
        );
        var datasourceResource = new DatasourceResource(logsDataSource.name(),logsDataSource.uuid());
        var resource = new LogResource(
                DateTimeUtils.convertLocalDateTimeToString(appLog.get().timestamp()),
                appLog.get().severity(),
                applicationResource,
                datasourceResource,
                appLog.get().uuid(),
                appLog.get().message()
        );
        return Optional.of(resource);
    }

    /**
     * Finds a log metadata resource by its logs unique identifier.
     *
     * @param logId the unique identifier of the log to find
     * @return an {@code List} containing the {@code LogsMetadata} list if found, or empty if no log matches the given ID
     */
    @Override
    public List<LogsMetadata> findMetadataByLogId(String logId) {
        return metadataRepository.findByRootLogId(logId);
    }

    @Override
    public Optional<AppLog> findAppLogById(String logId) {
        var appLog = logRepository.findById(logId);
        if (appLog.isEmpty()) return Optional.empty();
        return appLog;
    }

}
