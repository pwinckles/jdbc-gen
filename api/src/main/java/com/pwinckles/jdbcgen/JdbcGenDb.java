package com.pwinckles.jdbcgen;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

/**
 * Implementations of this interface provide basic DB access using plain JDBC. Implementations are generated at
 * compile time for entities that are annotated with {@link JdbcGen}.
 *
 * @param <E> The entity type
 * @param <I> The type of the entity's ID
 * @param <P> The type of entity's patch class, this is a generated class
 * @param <F> The type of the filter builder, this is a generated class
 * @param <S> The type of the sort builder, this is a generated class
 */
public interface JdbcGenDb<E, I, P extends BasePatch, F, S> {

    /**
     * Selects a single entity from the DB by its ID. Null is returned if the entity is not found.
     *
     * @param id the entity's ID
     * @param conn the JDBC connection
     * @return the entity or null
     * @throws SQLException
     */
    E select(I id, Connection conn) throws SQLException;

    /**
     * Selects all of the entities that match the specified filter. If there are none, then an empty list is returned.
     * The results are ordered as specified.
     *
     * @param selectBuilder customize the select query
     * @param conn the JDBC connection
     * @return a list of entities or an empty list
     * @throws SQLException
     */
    List<E> select(Consumer<SelectBuilder<F, S>> selectBuilder, Connection conn) throws SQLException;

    /**
     * Selects all of the entities. If there are none, then an empty list is returned. The results are not explicitly
     * ordered.
     *
     * @param conn the JDBC connection
     * @return a list of entities or an empty list
     * @throws SQLException
     */
    List<E> selectAll(Connection conn) throws SQLException;

    /**
     * Counts the number of entities.
     *
     * @param conn the JDBC connection
     * @return the number of entities
     * @throws SQLException
     */
    long count(Connection conn) throws SQLException;

    /**
     * Counts the number of entities.
     *
     * @param filterBuilder construct a filter to constrain the count
     * @param conn the JDBC connection
     * @return the number of entities
     * @throws SQLException
     */
    long count(Consumer<F> filterBuilder, Connection conn) throws SQLException;

    /**
     * Inserts a new entity into the DB, and returns the entity's ID. This method populates every column that's defined
     * on the entity, but the ID may be left blank if it is generated on insert.
     *
     * @param entity the entity to insert
     * @param conn the JDBC connection
     * @return the entity's ID
     * @throws SQLException
     */
    I insert(E entity, Connection conn) throws SQLException;

    /**
     * Inserts a new entity into the DB, and returns the entity's ID. This method only populates the columns that are
     * specified on the supplied object.
     *
     * @param entity the partial entity to insert
     * @param conn the JDBC connection
     * @return the entity's ID
     * @throws SQLException
     */
    I insert(P entity, Connection conn) throws SQLException;

    /**
     * Batch inserts all of the entities into the DB, and returns their IDs. This method populates every column that's
     * defined on the entity, but the ID may be left blank if it is generated on insert.
     * <p>
     * Note: Either every entity should specify an ID or none of them should; a mixture will not work.
     *
     * @param entities the entities to insert
     * @param conn the JDBC connection
     * @return the entities' IDs
     * @throws SQLException
     */
    List<I> insert(List<E> entities, Connection conn) throws SQLException;

    /**
     * Updates all of the fields, except for the ID, on an entity.
     *
     * @param entity the entity to update
     * @param conn the JDBC connection
     * @return the number of affected rows
     * @throws SQLException
     */
    int update(E entity, Connection conn) throws SQLException;

    /**
     * Updates a subset of fields on an entity.
     *
     * @param id the ID of the entity to update
     * @param entity the partial entity data
     * @param conn the JDBC connection
     * @return the number of affected rows
     * @throws SQLException
     */
    int update(I id, P entity, Connection conn) throws SQLException;

    /**
     * Batch updates all of the fields, except for the ID, in the specified entities.
     *
     * @param entities the list of entities to update
     * @param conn the JDBC connection
     * @return the numbe rof affected rows
     * @throws SQLException
     */
    int[] update(List<E> entities, Connection conn) throws SQLException;

    /**
     * Deletes an entity by its ID. If the entity does not exist, then 0 is returned.
     *
     * @param id the ID of the entity to delete
     * @param conn the JDBC connection
     * @return the number of affected rows
     * @throws SQLException
     */
    int delete(I id, Connection conn) throws SQLException;

    /**
     * Batch deletes a list of entities by ID.
     *
     * @param ids the IDs of the entities to delete
     * @param conn the JDBC connection
     * @return an array representing the number of affected rows from each query
     * @throws SQLException
     */
    int[] delete(List<I> ids, Connection conn) throws SQLException;

    /**
     * Deletes all of the entities that match the specified filter.
     *
     * @param filterBuilder construct a filter to constrain the delete
     * @param conn the JDBC connection
     * @return an array representing the number of affected rows from each query
     * @throws SQLException
     */
    int delete(Consumer<F> filterBuilder, Connection conn) throws SQLException;

    /**
     * Deletes all of the entities in the DB.
     *
     * @param conn the JDBC connection
     * @return the number of affected rows
     * @throws SQLException
     */
    int deleteAll(Connection conn) throws SQLException;
}
