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
import com.kenyajug.regression.entities.Application;
import com.kenyajug.regression.entities.User;
import com.kenyajug.regression.repository.ApplicationsRepository;
import com.kenyajug.regression.utils.DateTimeUtils;
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
public class ApplicationsRepositoryTest {
    @Autowired
    private JdbcClient jdbcClient;
    @Autowired
    private ApplicationsRepository repository;
    private final String appOwner = "UUID1";
    @AfterEach
    public void cleanUp(){
        var clearTable = """
                DELETE FROM applications;
                DELETE FROM users;
                """;
        jdbcClient.sql(clearTable).update();
    }
    @Test
    public void shouldSaveObjectTest(){
        var entity = new Application(
                "UUID1",
                "Instagram",
                "12.05.11.43787463Beta",
                "staging-ci",
                appOwner,
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC")
        );
        repository.save(entity);
        var optionalEntity = repository.findById(entity.uuid());
        assertThat(optionalEntity).isNotEmpty();
        var persisted = optionalEntity.get();
        assertThat(persisted).isNotNull();
        assertThat(persisted.uuid()).isEqualTo(entity.uuid());
        assertThat(persisted.name()).isEqualTo(entity.name());
        assertThat(persisted.appVersion()).isEqualTo(entity.appVersion());
        assertThat(persisted.runtimeEnvironment()).isEqualTo(entity.runtimeEnvironment());
        assertThat(persisted.owner()).isEqualTo(appOwner);
        assertThat(persisted.createdAt()).isEqualTo(entity.createdAt());
    }
    @Test
    public void shouldFindAllObjectsTest(){
        var entity1 = new Application(
                "UUID1",
                "Instagram",
                "12.05.11.43787463Beta",
                "staging-ci",
                appOwner,
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC")
        );
        var entity2 = new Application(
                "UUID2",
                "WhatsApp",
                "12.05.11.43787463Beta",
                "staging-ci",
                appOwner,
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2026-08-11 11:09:22 UTC")
        );
        repository.save(entity1);
        repository.save(entity2);
        var users = repository.findAll();
        assertThat(users).isNotEmpty();
        var persistedOptional = users.stream()
                .sorted(Comparator.comparing(Application::uuid))
                .findFirst();
        assertThat(persistedOptional).isNotEmpty();
        var persisted = persistedOptional.get();
        assertThat(persisted).isNotNull();
        assertThat(persisted.uuid()).isEqualTo("UUID1");
        assertThat(persisted.name()).isEqualTo(entity1.name());
        assertThat(persisted.appVersion()).isEqualTo(entity1.appVersion());
        assertThat(persisted.runtimeEnvironment()).isEqualTo(entity1.runtimeEnvironment());
        assertThat(persisted.owner()).isEqualTo(appOwner);
        assertThat(persisted.createdAt()).isEqualTo(entity1.createdAt());
    }
    @Test
    public void shouldDeleteByIdTest(){
        var entity = new Application(
                "UUID1",
                "Instagram",
                "12.05.11.43787463Beta",
                "staging-ci",
                appOwner,
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC")
        );
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
        var entity1 = new Application(
                "UUID1",
                "Instagram",
                "12.05.11.43787463Beta",
                "staging-ci",
                appOwner,
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC")
        );
        var entity2 = new Application(
                "UUID2",
                "WhatsApp",
                "12.05.11.43787463Beta",
                "staging-ci",
                appOwner,
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2024-08-11 11:09:22 UTC")
        );
        repository.save(entity1);
        repository.save(entity2);
        var entities = repository.findAll();
        assertThat(entities).isNotEmpty();
        var persistedOptional = entities.stream()
                .sorted(Comparator.comparing(Application::uuid))
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
        var entity = new Application(
                "UUID1",
                "Instagram",
                "12.05.11.43787463Beta",
                "staging-ci",
                appOwner,
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC")
        );
        repository.save(entity);
        var exists = repository.existsById(entity.uuid());
        assertThat(exists).isTrue();
        repository.deleteAll();
        exists = repository.existsById(entity.uuid());
        assertThat(exists).isFalse();
    }
    @Test
    public void shouldUpdateObjectTest(){
        var entity = new Application(
                "UUID1",
                "Instagram",
                "12.05.11.43787Beta",
                "staging-ci",
                appOwner,
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC")
        );
        repository.save(entity);
        var exists = repository.existsById(entity.uuid());
        assertThat(exists).isTrue();
        var entityUpdated = new Application(
                "UUID1",
                "Instagram",
                "12.05.22.988731.stable",
                "prod-ci",
                appOwner,
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-12-11 21:19:52 UTC")
        );
        repository.updateById(entity.uuid(),entityUpdated);
        var updatedOptional = repository.findById(entity.uuid());
        assertThat(updatedOptional).isNotEmpty();
        var persisted = updatedOptional.get();
        assertThat(persisted).isNotNull();
        assertThat(persisted.uuid()).isEqualTo(entity.uuid());
        assertThat(persisted.name()).isEqualTo(entityUpdated.name());
        assertThat(persisted.appVersion()).isEqualTo(entityUpdated.appVersion());
        assertThat(persisted.runtimeEnvironment()).isEqualTo(entityUpdated.runtimeEnvironment());
        assertThat(persisted.owner()).isEqualTo(appOwner);
        assertThat(persisted.createdAt()).isEqualTo(entityUpdated.createdAt());
    }
    @Test
    public void shouldFindByOwnerTest(){
        var entity = new Application(
                "UUID1",
                "Instagram",
                "12.05.11.43787Beta",
                "staging-ci",
                appOwner,
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC")
        );
        var owner = new User(
                appOwner,
                "Elle Cordova",
                "encoded_password_",
                "{admin}",
                DateTimeUtils.convertZonedUTCTimeStringToLocalDateTime("2025-08-11 11:09:22 UTC")
        );
        repository.save(entity);
        var optionalEntity = repository.findByOwner(owner);
        assertThat(optionalEntity).isNotEmpty();
        var persisted = optionalEntity.getFirst();
        assertThat(persisted).isNotNull();
        assertThat(persisted.uuid()).isEqualTo(entity.uuid());
        assertThat(persisted.uuid()).isEqualTo(entity.uuid());
        assertThat(persisted.name()).isEqualTo("Instagram");
        assertThat(persisted.appVersion()).isEqualTo("12.05.11.43787Beta");
        assertThat(persisted.runtimeEnvironment()).isEqualTo("staging-ci");
    }
}
