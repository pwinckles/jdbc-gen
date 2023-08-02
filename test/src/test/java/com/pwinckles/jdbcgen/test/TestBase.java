package com.pwinckles.jdbcgen.test;

import com.pwinckles.jdbcgen.BasePatch;
import com.pwinckles.jdbcgen.JdbcGenDb;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public abstract class TestBase<E, I, P extends BasePatch, C, F> {

    protected static Stream<Arguments> dbs() throws SQLException {
        return Stream.of(
                Arguments.of(DriverManager.getConnection("jdbc:hsqldb:mem:testdb;shutdown=true", "SA", "")),
                Arguments.of(DriverManager.getConnection("jdbc:h2:mem:testdb", "SA", "")));
    }

    private long serial = 1234L;
    protected JdbcGenDb<E, I, P, C, F> db;

    protected TestBase(JdbcGenDb<E, I, P, C, F> db) {
        this.db = db;
    }

    @ParameterizedTest
    @MethodSource("dbs")
    public void basicOperations(Connection conn) throws SQLException {
        try (conn) {
            createTable(conn);

            var original = newEntity();
            var id = db.insert(original, conn);
            original = setId(id, original);
            assertEntity(original, conn);

            var updated = updateEntity(original);
            db.update(updated, conn);
            assertEntity(updated, conn);

            var updatedPair = patchPartial(updated);
            db.update(id, updatedPair.getRight(), conn);
            assertEntity(updatedPair.getLeft(), conn);

            var updated2 = nullEntity(updatedPair.getLeft());
            db.update(updated2, conn);
            assertEntity(updated2, conn);

            var updated3 = updateEntity(updated2);
            var updated3Patch = patchAll(updated3);
            db.update(id, updated3Patch, conn);
            assertEntity(updated3, conn);

            var updated4 = nullEntity(updated3);
            var updated4Patch = nullPatchAll();
            db.update(id, updated4Patch, conn);
            assertEntity(updated4, conn);

            db.delete(id, conn);
            assertDoesNotExist(id, conn);
        }
    }

    @ParameterizedTest
    @MethodSource("dbs")
    public void insertWithSpecifiedId(Connection conn) throws SQLException {
        try (conn) {
            createTable(conn);

            var original = newEntityWithId();
            db.insert(original, conn);
            assertEntity(original, conn);

            var originals = List.of(newEntityWithId(), newEntityWithId(), newEntityWithId(), newEntityWithId());

            db.insert(originals, conn);
            assertEntities(originals, conn);
        }
    }

    @ParameterizedTest
    @MethodSource("dbs")
    public void partialInsert(Connection conn) throws SQLException {
        try (conn) {
            createTable(conn);
            var updatedPair = patchPartial(nullEntity(newEntity()));
            var partial = addRequiredFields(updatedPair.getLeft(), updatedPair.getRight());
            var id = db.insert(partial, conn);
            assertEntity(setId(id, updatedPair.getLeft()), conn);
        }
    }

    @ParameterizedTest
    @MethodSource("dbs")
    public void bulkOperations(Connection conn) throws SQLException {
        try (conn) {
            createTable(conn);

            var originals = new ArrayList<>(List.of(newEntity(), newEntity(), newEntity(), newEntity()));

            var ids = db.insert(originals, conn);

            for (int i = 0; i < originals.size(); i++) {
                originals.set(i, setId(ids.get(i), originals.get(i)));
            }

            assertCount(originals.size(), conn);
            assertAllEntities(originals, conn);

            var updates = List.of(updateEntity(originals.get(1)), updateEntity(originals.get(3)));
            db.update(updates, conn);
            assertAllEntities(List.of(originals.get(0), updates.get(0), originals.get(2), updates.get(1)), conn);

            var deletes = List.of(getId(originals.get(0)), getId(originals.get(3)));
            db.delete(deletes, conn);
            assertCount(2, conn);
            assertAllEntities(List.of(updates.get(0), originals.get(2)), conn);

            db.deleteAll(conn);
            assertCount(0, conn);
            assertAllEntities(Collections.emptyList(), conn);
        }
    }

    protected abstract I getId(E entity);

    protected abstract E setId(I id, E entity);

    protected abstract E newEntity();

    protected abstract E newEntityWithId();

    protected abstract E updateEntity(E entity);

    protected abstract P patchAll(E entity);

    protected abstract E nullEntity(E entity);

    protected abstract P nullPatchAll();

    protected abstract Pair<E, P> patchPartial(E entity);

    protected abstract P addRequiredFields(E entity, P patch);

    protected abstract void createTable(Connection conn) throws SQLException;

    protected long nextId() {
        return serial++;
    }

    protected void assertCount(long expected, Connection conn) throws SQLException {
        Assertions.assertThat(db.count(conn)).isEqualTo(expected);
    }

    protected void assertEntity(E expected, Connection conn) throws SQLException {
        Assertions.assertThat(db.select(getId(expected), conn))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    protected void assertEntities(List<E> expected, Connection conn) {
        var actual = expected.stream()
                .map(e -> {
                    try {
                        return db.select(getId(e), conn);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                })
                .collect(Collectors.toList());
        assertEntities(expected, actual);
    }

    protected void assertAllEntities(List<E> expected, Connection conn) throws SQLException {
        assertEntities(expected, db.selectAll(conn));
    }

    protected void assertEntities(List<E> expected, List<E> actual) {
        Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    protected void assertDoesNotExist(I id, Connection conn) throws SQLException {
        Assertions.assertThat(db.select(id, conn)).isNull();
    }
}
