package com.kenyajug.regression.repository;
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
import com.kenyajug.regression.utils.DateTimeUtils;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
@Repository
public non-sealed class AppLogRepository implements CrudRepository<AppLog>{
    private final JdbcClient jdbcClient;
    private final TransactionTemplate transactionTemplate;
    public AppLogRepository(JdbcClient jdbcClient, TransactionTemplate transactionTemplate) {
        this.jdbcClient = jdbcClient;
        this.transactionTemplate = transactionTemplate;
    }
    /**
     * Saves the given entity to the database.
     * If the entity already exists (e.g., same ID), it may update the record depending on implementation.
     *
     * @param entity the entity to save (must not be {@code null})
     */
    @Override
    public void save(AppLog entity) {
        transactionTemplate.executeWithoutResult(status -> {
            var insertSql = """
                         INSERT INTO app_logs (
                             uuid,
                             timestamp,
                             severity,
                             application_uuid,
                             log_source,
                             message
                         ) VALUES (
                             :uuid,
                             :timestamp,
                             :severity,
                             :application_uuid,
                             :log_source,
                             :message
                         );
                    """;
            jdbcClient
                    .sql(insertSql)
                    .param("uuid", entity.uuid())
                    .param("timestamp", DateTimeUtils.localDateTimeToUTCTime(entity.timestamp()))
                    .param("severity", entity.severity())
                    .param("application_uuid", entity.applicationId())
                    .param("log_source", entity.logSource())
                    .param("message", entity.message())
                    .update();
        });
    }
    /**
     * Finds an entity by its unique identifier.
     *
     * @param uuid the unique identifier of the entity
     * @return an {@link Optional} containing the found entity, or empty if not found
     */
    @Override
    public Optional<AppLog> findById(String uuid) {
        var selectSql = """
                SELECT * FROM app_logs
                WHERE uuid = :uuid
                ;
                """;
        return jdbcClient.sql(selectSql)
                .param("uuid",uuid)
                .query((resultSet, row) -> new AppLog(
                        resultSet.getString("uuid"),
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime(resultSet.getString("timestamp")),
                        resultSet.getString("severity"),
                        resultSet.getString("application_uuid"),
                        resultSet.getString("log_source"),
                        resultSet.getString("message")
                ))
                .optional();
    }
    /**
     * Retrieves all entities of type {@code T} from the database.
     *
     * @return a list of all entities; never {@code null}, but may be empty
     */
    @Override
    public List<AppLog> findAll() {
        var selectSql = """
                SELECT * FROM app_logs
                ;
                """;
        return jdbcClient.sql(selectSql)
                .query((resultSet, row) -> new AppLog(
                        resultSet.getString("uuid"),
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime(resultSet.getString("timestamp")),
                        resultSet.getString("severity"),
                        resultSet.getString("application_uuid"),
                        resultSet.getString("log_source"),
                        resultSet.getString("message")
                ))
                .list();
    }
    /**
     * Deletes the entity with the specified identifier from the database.
     * If no such entity exists, the operation is silently ignored.
     *
     * @param uuid the unique identifier of the entity to delete
     */
    @Override
    public void deleteById(String uuid) {
        var deleteSql = """
                DELETE FROM app_logs
                WHERE
                uuid = :uuid
                """;
        jdbcClient.sql(deleteSql)
                .param("uuid",uuid)
                .update();
    }
    /**
     * Deletes all entities of type {@code T} from the database.
     * Use with caution in production environments.
     */
    @Override
    public void deleteAll() {
        var deleteSql = """
                DELETE FROM app_logs;
                """;
        jdbcClient.sql(deleteSql)
                .update();
    }
    /**
     * Checks whether an entity with the given unique identifier exists in the data source.
     *
     * @param uuid the unique identifier of the entity to check (must not be {@code null})
     * @return {@code true} if an entity with the specified UUID exists, {@code false} otherwise
     */
    @Override
    public boolean existsById(String uuid) {
        var countSql = """
                SELECT COUNT(*) FROM app_logs
                WHERE
                uuid = :uuid
                """;
        var count = jdbcClient.sql(countSql)
                .param("uuid",uuid)
                .query((resultSet,row) -> resultSet.getLong(1))
                .single();
        return count > 0;
    }
    /**
     * Updates an existing entity identified by the given UUID with the provided new data.
     *
     * @param uuid   the unique identifier of the entity to update (must not be {@code null})
     * @param entity the updated entity data to apply (must not be {@code null});
     *               the UUID field inside the entity is typically ignored in favor of the provided {@code uuid}
     * @throws IllegalArgumentException if {@code uuid} or {@code entity} is {@code null}
     * @throws NoSuchElementException   if no entity with the given {@code uuid} exists in the data source
     */
    @Override
    public void updateById(String uuid, AppLog entity) throws NoSuchElementException {
        var updateSql = """
                UPDATE app_logs
                SET timestamp = :timestamp,
                    severity = :severity,
                    application_uuid = :application_uuid,
                    log_source = :log_source,
                    message = :message
                WHERE uuid = :uuid;
                ;
                """;
        jdbcClient.sql(updateSql)
                .param("timestamp",DateTimeUtils.localDateTimeToUTCTime(entity.timestamp()))
                .param("severity",entity.severity())
                .param("application_uuid",entity.applicationId())
                .param("log_source",entity.logSource())
                .param("message",entity.message())
                .param("uuid",uuid)
                .update();
    }
}
