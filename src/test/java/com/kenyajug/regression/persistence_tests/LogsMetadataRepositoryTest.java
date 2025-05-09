package com.kenyajug.regression.persistence_tests;
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
import com.kenyajug.regression.entities.LogsMetadata;
import com.kenyajug.regression.repository.LogsMetadataRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;
import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class LogsMetadataRepositoryTest {
    @Autowired
    private JdbcClient jdbcClient;
    @Autowired
    private LogsMetadataRepository repository;
    private final String logId = "Root_log_UUID1";
    @AfterEach
    public void cleanUp(){
        var clearTable = """
                DELETE FROM logs_metadata;
                """;
        jdbcClient.sql(clearTable).update();
    }
    @Test
    public void shouldSaveObjectTest(){
        var entity = new LogsMetadata(
                "UUID1",
                logId,
                "OS",
                "Ubuntu Desktop 24.04.2 LTS");
        repository.save(entity);
        var optionalEntity = repository.findById(entity.uuid());
        assertThat(optionalEntity).isNotEmpty();
        var persisted = optionalEntity.get();
        assertThat(persisted).isNotNull();
        assertThat(persisted.uuid()).isEqualTo(entity.uuid());
        assertThat(persisted.logId()).isEqualTo(entity.logId());
        assertThat(persisted.metadataType()).isEqualTo(entity.metadataType());
        assertThat(persisted.metadataValue()).isEqualTo(entity.metadataValue());
    }
    @Test
    public void shouldFindAllObjectsTest(){
        var entity1 = new LogsMetadata(
                "UUID1",
                logId,
                "OS",
                "Ubuntu Desktop 24.04.2 LTS");
        var entity2 = new LogsMetadata(
                "UUID2",
                logId,
                "OS",
                "Ubuntu Desktop 24.04.2 LTS");
        repository.save(entity1);
        repository.save(entity2);
        var users = repository.findAll();
        assertThat(users).isNotEmpty();
        var persistedOptional = users.stream()
                .sorted(Comparator.comparing(LogsMetadata::uuid))
                .findFirst();
        assertThat(persistedOptional).isNotEmpty();
        var persisted = persistedOptional.get();
        assertThat(persisted).isNotNull();
        assertThat(persisted.uuid()).isEqualTo("UUID1");
        assertThat(persisted.logId()).isEqualTo(entity1.logId());
        assertThat(persisted.metadataType()).isEqualTo(entity1.metadataType());
        assertThat(persisted.metadataValue()).isEqualTo(entity1.metadataValue());
    }
    @Test
    public void shouldDeleteByIdTest(){
        var entity = new LogsMetadata(
                "UUID1",
                logId,
                "OS",
                "Ubuntu Desktop 24.04.2 LTS");
        repository.save(entity);
        var optionalEntity = repository.findById(entity.uuid());
        assertThat(optionalEntity).isNotEmpty();
        var persisted = optionalEntity.get();
        assertThat(persisted).isNotNull();
        assertThat(persisted.uuid()).isEqualTo(entity.uuid());
        repository.deleteById(entity.uuid());
        optionalEntity = repository.findById(entity.uuid());
        assertThat(optionalEntity).isEmpty();
    }
    @Test
    public void shouldDeleteAllObjectsTest(){
        var entity1 = new LogsMetadata(
                "UUID1",
                logId,
                "OS",
                "Ubuntu Desktop 24.04.2 LTS");
        var entity2 = new LogsMetadata(
                "UUID2",
                logId,
                "OS",
                "Ubuntu Desktop 24.04.2 LTS");
        repository.save(entity1);
        repository.save(entity2);
        var entities = repository.findAll();
        assertThat(entities).isNotEmpty();
        var persistedOptional = entities.stream()
                .sorted(Comparator.comparing(LogsMetadata::uuid))
                .findFirst();
        assertThat(persistedOptional).isNotEmpty();
        var persisted = persistedOptional.get();
        assertThat(persisted).isNotNull();
        assertThat(persisted.uuid()).isEqualTo("UUID1");
        repository.deleteAll();
        entities = repository.findAll();
        assertThat(entities).isEmpty();
    }
    @Test
    public void shouldCheckObjectExistenceById_Test(){
        var entity = new LogsMetadata(
                "UUID1",
                logId,
                "OS",
                "Ubuntu Desktop 24.04.2 LTS");
        repository.save(entity);
        var exists = repository.existsById(entity.uuid());
        assertThat(exists).isTrue();
        repository.deleteAll();
        exists = repository.existsById(entity.uuid());
        assertThat(exists).isFalse();
    }
    @Test
    public void shouldUpdateObjectTest(){
        var entity = new LogsMetadata(
                "UUID1",
                logId,
                "OS",
                "Ubuntu Desktop 24.04.2 LTS");
        repository.save(entity);
        var exists = repository.existsById(entity.uuid());
        assertThat(exists).isTrue();
        var entityUpdated = new LogsMetadata(
                "UUID1",
                logId,
                "OS",
                "Ubuntu Desktop 22.04.2 LTS");
        repository.updateById(entity.uuid(),entityUpdated);
        var updatedOptional = repository.findById(entity.uuid());
        assertThat(updatedOptional).isNotEmpty();
        var persisted = updatedOptional.get();
        assertThat(persisted).isNotNull();
        assertThat(persisted.uuid()).isEqualTo(entityUpdated.uuid());
        assertThat(persisted.logId()).isEqualTo(entityUpdated.logId());
        assertThat(persisted.metadataType()).isEqualTo(entityUpdated.metadataType());
        assertThat(persisted.metadataValue()).isEqualTo(entityUpdated.metadataValue());
    }
    @Test
    public void shouldFindLogsMetadataByParentLogTest(){
        var entity1 = new LogsMetadata(
                "UUID1",
                logId,
                "OS",
                "Ubuntu Desktop 24.04.2 LTS");
        var entity2 = new LogsMetadata(
                "UUID2",
                "LOG_UUID_2",
                "OS",
                "Ubuntu Desktop 24.04.2 LTS");
        repository.save(entity1);
        repository.save(entity2);
        var entities = repository.findAll();
        assertThat(entities).isNotEmpty();
        var matching = repository.findByRootLogId("LOG_UUID_2");
        assertThat(matching).isNotEmpty();
        assertThat(matching.size()).isEqualTo(1);
        var match = matching.getFirst();
        assertThat(match).isNotNull();
        assertThat(match.uuid()).isEqualTo(entity2.uuid());
        assertThat(match.logId()).isEqualTo(entity2.logId());
        assertThat(match.metadataType()).isEqualTo(entity2.metadataType());
        assertThat(match.metadataValue()).isEqualTo(entity2.metadataValue());
    }
}
