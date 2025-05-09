package com.kenyajug.regression.persistence_tests;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MigrationTest {
    @Autowired
    private DataSource dataSource;
    @Test
    @Order(1)
    void databaseShouldHaveUsersTable() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            ResultSet rs = conn.getMetaData().getTables(null, null, "users", null);
            Assertions.assertTrue(rs.next(), "Expected 'users' table to exist");
        }
    }
    @Test
    @Order(2)
    void databaseShouldHaveApplicationsTable() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            ResultSet rs = conn.getMetaData().getTables(null, null, "applications", null);
            Assertions.assertTrue(rs.next(), "Expected 'applications' table to exist");
        }
    }
    @Test
    @Order(3)
    void databaseShouldHaveAppLogsTable() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            ResultSet rs = conn.getMetaData().getTables(null, null, "app_logs", null);
            Assertions.assertTrue(rs.next(), "Expected 'app_logs' table to exist");
        }
    }
    @Test
    @Order(4)
    void databaseShouldHaveLogsMetadataTable() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            ResultSet rs = conn.getMetaData().getTables(null, null, "logs_metadata", null);
            Assertions.assertTrue(rs.next(), "Expected 'logs_metadata' table to exist");
        }
    }
}

