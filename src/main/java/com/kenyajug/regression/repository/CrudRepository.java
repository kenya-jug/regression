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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
/**
 * A generic CRUD (Create, Read, Update, Delete) repository interface
 * for performing basic database operations on any entity type.
 *
 * @param <T> the type of the entity managed by this repository
 */
public sealed interface CrudRepository<T>
        permits AppLogRepository, ApplicationsRepository, LogsMetadataRepository, UserRepository
{

    /**
     * Saves the given entity to the database.
     * If the entity already exists (e.g., same ID), it may update the record depending on implementation.
     *
     * @param entity the entity to save (must not be {@code null})
     */
    void save(T entity);

    /**
     * Finds an entity by its unique identifier.
     *
     * @param uuid the unique identifier of the entity
     * @return an {@link Optional} containing the found entity, or empty if not found
     */
    Optional<T> findById(String uuid);

    /**
     * Retrieves all entities of type {@code T} from the database.
     *
     * @return a list of all entities; never {@code null}, but may be empty
     */
    List<T> findAll();

    /**
     * Deletes the entity with the specified identifier from the database.
     * If no such entity exists, the operation is silently ignored.
     *
     * @param uuid the unique identifier of the entity to delete
     */
    void deleteById(String uuid);

    /**
     * Deletes all entities of type {@code T} from the database.
     * Use with caution in production environments.
     */
    void deleteAll();
    /**
     * Checks whether an entity with the given unique identifier exists in the data source.
     *
     * @param uuid the unique identifier of the entity to check (must not be {@code null})
     * @return {@code true} if an entity with the specified UUID exists, {@code false} otherwise
     */
    boolean existsById(String uuid);
    /**
     * Updates an existing entity identified by the given UUID with the provided new data.
     *
     * @param uuid   the unique identifier of the entity to update (must not be {@code null})
     * @param entity the updated entity data to apply (must not be {@code null});
     *               the UUID field inside the entity is typically ignored in favor of the provided {@code uuid}
     * @throws IllegalArgumentException if {@code uuid} or {@code entity} is {@code null}
     * @throws NoSuchElementException if no entity with the given {@code uuid} exists in the data source
     */
    void updateById(String uuid, T entity) throws NoSuchElementException;
}
