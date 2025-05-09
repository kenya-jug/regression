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
import com.kenyajug.regression.entities.User;
import com.kenyajug.regression.utils.DateTimeUtils;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;
import java.util.List;
import java.util.Optional;
@Repository
public non-sealed class UserRepository implements CrudRepository<User> {
    private final JdbcClient jdbcClient;
    private final TransactionTemplate transactionTemplate;
    public UserRepository(JdbcClient jdbcClient, TransactionTemplate transactionTemplate) {
        this.jdbcClient = jdbcClient;
        this.transactionTemplate = transactionTemplate;
    }
    /**
     * Saves the given entity to the database.
     * If the entity already exists (e.g., same ID), it may update the record depending on implementation.
     *
     * @param user the entity to save (must not be {@code null})
     */
    @Override
    public void save(User user) {
        transactionTemplate.executeWithoutResult(status -> {
            var insertSql = """
                         INSERT INTO users (uuid, username, password, roles_list_json, created_at)
                         VALUES (:uuid, :username, :password, :roles_list_json, :created_at)
                    """;
            jdbcClient
                    .sql(insertSql)
                    .param("uuid", user.uuid())
                    .param("username", user.username())
                    .param("password", user.password())
                    .param("roles_list_json", user.roles_list_json())
                    .param("created_at", DateTimeUtils.localDateTimeToUTCTime(user.created_at()))
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
    public Optional<User> findById(String uuid) {
        var selectSql = """
                SELECT * FROM users
                WHERE uuid = :uuid
                ;
                """;
        return jdbcClient.sql(selectSql)
                .param("uuid",uuid)
                .query((resultSet, row) -> new User(
                        resultSet.getString("uuid"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("roles_list_json"),
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime(resultSet.getString("created_at"))
                ))
                .optional();
    }
    /**
     * Retrieves all entities of type {@code T} from the database.
     *
     * @return a list of all entities; never {@code null}, but may be empty
     */
    @Override
    public List<User> findAll() {
        var selectSql = """
                SELECT * FROM users;
                """;
        return jdbcClient.sql(selectSql)
                .query((resultSet,row) -> new User(
                        resultSet.getString("uuid"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("roles_list_json"),
                        DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime(resultSet.getString("created_at"))
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
                DELETE FROM users
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
                DELETE FROM users;
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
                SELECT COUNT(*) FROM users
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
     * Updates an existing entity identified by the given UUID.
     * <p>
     * The specific update behavior should be defined by the implementing class,
     * as this method does not accept any updated field values directly.
     *
     * @param uuid the unique identifier of the entity to update (must not be {@code null})
     * @throws IllegalArgumentException if the UUID is {@code null} or the entity does not exist
     */
    @Override
    public void updateById(String uuid, User user) {
        var updateSql = """
                UPDATE users
                SET username = :username,
                    password = :password,
                    roles_list_json = :roles_list_json,
                    created_at = :created_at
                WHERE uuid = :uuid
                ;
                """;
        jdbcClient.sql(updateSql)
                .param("username",user.username())
                .param("password",user.password())
                .param("roles_list_json",user.roles_list_json())
                .param("created_at",DateTimeUtils.localDateTimeToUTCTime(user.created_at()))
                .param("uuid",uuid)
                .update();
    }
}
