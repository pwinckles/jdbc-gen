package com.pwinckles.jdbcgen.test.prototype;

import com.pwinckles.jdbcgen.test.ExampleEnum;
import com.pwinckles.jdbcgen.test.util.TestUtil;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ExampleTest {

    private ExampleDb exampleDb = new ExampleDb();

    private static Stream<Arguments> dbs() throws SQLException {
        return Stream.of(
                Arguments.of(DriverManager.getConnection("jdbc:hsqldb:mem:testdb;shutdown=true", "SA", "")),
                Arguments.of(DriverManager.getConnection("jdbc:h2:mem:testdb", "SA", "")));
    }

    @ParameterizedTest
    @MethodSource("dbs")
    public void basicOperations(Connection conn) throws SQLException {
        try (conn) {
            createTable(conn);

            var original = example();
            var id = exampleDb.insert(original, conn);
            assertEntity(original.setId(id), conn);

            var updated = original.setName("test - updated").setTimestamp(TestUtil.now());
            exampleDb.update(updated, conn);
            assertEntity(updated, conn);

            exampleDb.update(id, new ExampleDb.Patch().setName("test - partial"), conn);
            assertEntity(updated.setName("test - partial"), conn);

            exampleDb.update(id, new ExampleDb.Patch().setName(null).setTimestamp(null), conn);
            assertEntity(updated.setName(null).setTimestamp(null), conn);

            exampleDb.delete(id, conn);
            assertDoesNotExist(id, conn);
        }
    }

    @ParameterizedTest
    @MethodSource("dbs")
    public void insertWithSpecifiedId(Connection conn) throws SQLException {
        try (conn) {
            createTable(conn);

            var original = example().setId(112233L);
            exampleDb.insert(original, conn);
            assertEntity(original, conn);

            var originals = List.of(
                    example().setId(22L),
                    example().setId(33L),
                    example().setId(44L),
                    example().setId(55L));

            exampleDb.insert(originals, conn);
            assertEntities(originals, conn);
        }
    }

    @ParameterizedTest
    @MethodSource("dbs")
    public void partialInsert(Connection conn) throws SQLException {
        try (conn) {
            createTable(conn);
            var id = exampleDb.insert(new ExampleDb.Patch().setName("partial"), conn);
            assertEntity(new Example().setId(id).setName("partial"), conn);
        }
    }

    @ParameterizedTest
    @MethodSource("dbs")
    public void bulkOperations(Connection conn) throws SQLException {
        try (conn) {
            createTable(conn);

            var originals = List.of(example(), example(), example(), example());

            var ids = exampleDb.insert(originals, conn);

            for (int i = 0; i < originals.size(); i++) {
                originals.get(i).setId(ids.get(i));
            }

            assertCount(originals.size(), conn);
            assertAllEntities(originals, conn);

            var updates = List.of(
                    originals.get(1).setName("updated"),
                    originals.get(3).setName("updated too").setTimestamp(TestUtil.now()));
            exampleDb.update(updates, conn);
            assertAllEntities(originals, conn);

            var deletes = List.of(originals.get(0).getId(), originals.get(3).getId());
            exampleDb.delete(deletes, conn);
            assertCount(2, conn);
            assertAllEntities(List.of(originals.get(1), originals.get(2)), conn);

            exampleDb.deleteAll(conn);
            assertCount(0, conn);
            assertAllEntities(Collections.emptyList(), conn);
        }
    }

    @ParameterizedTest
    @MethodSource("dbs")
    public void selectAllOrderBy(Connection conn) throws SQLException {
        try (conn) {
            createTable(conn);

            var originals = new ArrayList<>(List.of(
                    new Example().setId(1L).setName("d"),
                    new Example().setId(2L).setName("c"),
                    new Example().setId(3L).setName("b"),
                    new Example().setId(4L).setName("a")));

            exampleDb.insert(originals, conn);

            var results = exampleDb.selectAll(sb -> sb.idAsc(), conn);
            assertEntities(originals, results);

            results = exampleDb.selectAll(sb -> sb.nameDesc(), conn);
            assertEntities(originals, results);

            Collections.reverse(originals);

            results = exampleDb.selectAll(sb -> sb.idDesc(), conn);
            assertEntities(originals, results);

            results = exampleDb.selectAll(sb -> sb.nameAsc(), conn);
            assertEntities(originals, results);
        }
    }

    @ParameterizedTest
    @MethodSource("dbs")
    public void selectAllFiltered(Connection conn) throws SQLException {
        try (conn) {
            createTable(conn);

            var originals = new ArrayList<>(List.of(
                    new Example().setId(1L).setName("d").setCount(10),
                    new Example().setId(2L).setName("c").setCount(100),
                    new Example().setId(3L).setName("b").setCount(0),
                    new Example().setId(4L).setName("a").setCount(5)));

            exampleDb.insert(originals, conn);

            var results =
                    exampleDb.select(fb -> fb.name().isEqualTo("b").or().count().isGreaterThan(10), conn);
            assertEntities(List.of(originals.get(1), originals.get(2)), results);

            results = exampleDb.select(
                    fb -> fb.group(gb -> gb.name().isEqualTo("a").and().count().isEqualTo(10))
                            .or()
                            .name()
                            .isEqualTo("c"),
                    conn);
            assertEntities(List.of(originals.get(1)), results);

            results = exampleDb.select(fb -> fb.name().isIn(List.of("a", "d")), conn);
            assertEntities(List.of(originals.get(0), originals.get(3)), results);

            results = exampleDb.select(fb -> fb.count().isNotIn(List.of(10L, 20L, 30L, 100L)), conn);
            assertEntities(List.of(originals.get(2), originals.get(3)), results);
        }
    }

    private void assertCount(long expected, Connection conn) throws SQLException {
        Assertions.assertThat(exampleDb.count(conn)).isEqualTo(expected);
    }

    private void assertEntity(Example expected, Connection conn) throws SQLException {
        Assertions.assertThat(exampleDb.select(expected.getId(), conn))
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    private void assertEntities(List<Example> expected, Connection conn) {
        var actual = expected.stream()
                .map(e -> {
                    try {
                        return exampleDb.select(e.getId(), conn);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                })
                .collect(Collectors.toList());
        assertEntities(expected, actual);
    }

    private void assertAllEntities(List<Example> expected, Connection conn) throws SQLException {
        assertEntities(expected, exampleDb.selectAll(conn));
    }

    private void assertEntities(List<Example> expected, List<Example> actual) {
        Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    private void assertDoesNotExist(long id, Connection conn) throws SQLException {
        Assertions.assertThat(exampleDb.select(id, conn)).isNull();
    }

    private Example example() {
        return new Example()
                .setName(RandomStringUtils.randomAlphanumeric(10))
                .setCount(RandomUtils.nextLong())
                .setTimestamp(TestUtil.now())
                .setExampleEnum(ExampleEnum.TWO);
    }

    private void createTable(Connection conn) throws SQLException {
        try (var stmt = conn.createStatement()) {
            stmt.execute(
                    "CREATE TABLE example (id IDENTITY PRIMARY KEY, name VARCHAR(255), count BIGINT, timestamp TIMESTAMP, enum VARCHAR(255))");
        }
    }
}
