package com.pwinckles.jdbcgen.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.pwinckles.jdbcgen.test.util.TestUtil;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class GetterSetterAllTypesEntityDbTest
        extends BaseAllTypesTest<
                GetterSetterAllTypesEntity,
                Long,
                GetterSetterAllTypesEntityDb.Patch,
                GetterSetterAllTypesEntityDb.FilterBuilder,
                GetterSetterAllTypesEntityDb.SortBuilder> {

    public GetterSetterAllTypesEntityDbTest() {
        super(new GetterSetterAllTypesEntityDb());
    }

    @ParameterizedTest
    @MethodSource("dbs")
    public void selectAllOrderBy(Connection conn) throws SQLException {
        try (conn) {
            createTable(conn);

            var originals = new ArrayList<>(List.of(
                    newEntityWithId().setString("d"),
                    newEntityWithId().setString("c"),
                    newEntityWithId().setString("b"),
                    newEntityWithId().setString("a")));

            db.insert(originals, conn);

            var results = db.select(sb -> sb.sort(s -> s.longIdAsc()), conn);
            assertEntities(originals, results);

            results = db.select(sb -> sb.sort(s -> s.stringDesc()), conn);
            assertEntities(originals, results);

            Collections.reverse(originals);

            results = db.select(sb -> sb.sort(s -> s.longIdDesc()), conn);
            assertEntities(originals, results);

            results = db.select(sb -> sb.sort(s -> s.stringAsc()), conn);
            assertEntities(originals, results);
        }
    }

    @ParameterizedTest
    @MethodSource("dbs")
    public void selectAllOrderByMultiple(Connection conn) throws SQLException {
        try (conn) {
            createTable(conn);

            var originals = List.of(
                    newEntityWithId().setString("c"), // 0
                    newEntityWithId().setString("a"), // 1
                    newEntityWithId().setString("b"), // 2
                    newEntityWithId().setString("b"), // 3
                    newEntityWithId().setString("b"), // 4
                    newEntityWithId().setString("a") // 5
                    );

            db.insert(originals, conn);

            var results = db.select(sb -> sb.sort(s -> s.stringAsc().longIdDesc()), conn);
            assertEntities(
                    List.of(
                            originals.get(5),
                            originals.get(1),
                            originals.get(4),
                            originals.get(3),
                            originals.get(2),
                            originals.get(0)),
                    results);
        }
    }

    @ParameterizedTest
    @MethodSource("dbs")
    public void selectFilteredAndSorted(Connection conn) throws SQLException {
        try (conn) {
            createTable(conn);

            var originals = List.of(
                    newEntityWithId().setString("c"), // 0
                    newEntityWithId().setString("a"), // 1
                    newEntityWithId().setString("b"), // 2
                    newEntityWithId().setString("b"), // 3
                    newEntityWithId().setString("b"), // 4
                    newEntityWithId().setString("a") // 5
                    );

            db.insert(originals, conn);

            var results =
                    db.select(sb -> sb.filter(f -> f.string().isEqualTo("b")).sort(s -> s.longIdDesc()), conn);
            assertEntities(List.of(originals.get(4), originals.get(3), originals.get(2)), results);
        }
    }

    @ParameterizedTest
    @MethodSource("dbs")
    public void selectFilteredByString(Connection conn) throws SQLException {
        try (conn) {
            createTable(conn);

            var entities = List.of(
                    newEntityWithId().setString("a"), // 0
                    newEntityWithId().setString("example"), // 1
                    newEntityWithId().setString("test-1"), // 2
                    newEntityWithId().setString("TEST-1"), // 3
                    newEntityWithId().setString("TEST-1"), // 4
                    newEntityWithId().setString("m"), // 5
                    newEntityWithId().setString("test-2"), // 6
                    newEntityWithId().setString("z"), // 7
                    newEntityWithId().setString("w"), // 8
                    newEntityWithId().setString("test-3"), // 9
                    newEntityWithId().setString(null), // 10
                    newEntityWithId().setString(null) // 11
                    );

            db.insert(entities, conn);

            var selected = db.select(sb -> sb.filter(f -> f.string().isLike("test-%")), conn);
            assertEntities(listWith(entities, 2, 6, 9), selected);

            selected = db.select(sb -> sb.filter(f -> f.string().isLikeInsensitive("TesT-%")), conn);
            assertEntities(listWith(entities, 2, 3, 4, 6, 9), selected);

            selected = db.select(sb -> sb.filter(f -> f.string().isNotLike("test-%")), conn);
            assertEntities(listWithout(entities, 2, 6, 9, 10, 11), selected);

            selected = db.select(sb -> sb.filter(f -> f.string().isNotLikeInsensitive("tESt-%")), conn);
            assertEntities(listWithout(entities, 2, 3, 4, 6, 9, 10, 11), selected);

            selected = db.select(sb -> sb.filter(f -> f.string().isEqualTo("TEST-1")), conn);
            assertEntities(listWith(entities, 3, 4), selected);

            selected = db.select(sb -> sb.filter(f -> f.string().isEqualToInsensitive("TEST-1")), conn);
            assertEntities(listWith(entities, 2, 3, 4), selected);

            selected = db.select(sb -> sb.filter(f -> f.string().isNotEqualTo("example")), conn);
            assertEntities(listWithout(entities, 1, 10, 11), selected);

            selected = db.select(sb -> sb.filter(f -> f.string().isNotEqualToInsensitive("EXAMPLE")), conn);
            assertEntities(listWithout(entities, 1, 10, 11), selected);

            selected = db.select(sb -> sb.filter(f -> f.string().isNull()), conn);
            assertEntities(listWith(entities, 10, 11), selected);

            selected = db.select(sb -> sb.filter(f -> f.string().isNotNull()), conn);
            assertEntities(listWithout(entities, 10, 11), selected);

            selected = db.select(sb -> sb.filter(f -> f.string().isIn(List.of("a", "z"))), conn);
            assertEntities(listWith(entities, 0, 7), selected);

            selected = db.select(sb -> sb.filter(f -> f.string().isNotIn(List.of("a", "w", "z"))), conn);
            assertEntities(listWithout(entities, 0, 7, 8, 10, 11), selected);

            selected = db.select(
                    sb -> sb.filter(f ->
                            f.string().isIn(List.of("a", "z")).or().string().isEqualTo("example")),
                    conn);
            assertEntities(listWith(entities, 0, 1, 7), selected);

            selected = db.select(
                    sb -> sb.filter(
                            f -> f.string().isNotLike("test-%").or().string().isNull()),
                    conn);
            assertEntities(listWithout(entities, 2, 6, 9), selected);

            selected = db.select(sb -> sb.filter(f -> f.string().isGreaterThanOrEqualTo("w")), conn);
            assertEntities(listWith(entities, 7, 8), selected);

            selected = db.select(sb -> sb.filter(f -> f.string().isEqualTo("blahblah")), conn);
            assertThat(selected).isEmpty();
        }
    }

    @ParameterizedTest
    @MethodSource("dbs")
    public void selectFilteredByBoolean(Connection conn) throws SQLException {
        try (conn) {
            createTable(conn);

            var entities = List.of(
                    newEntityWithId().setBoolPrim(true).setBoolObj(true), // 0
                    newEntityWithId().setBoolPrim(true).setBoolObj(true), // 1
                    newEntityWithId().setBoolPrim(false).setBoolObj(false), // 2
                    newEntityWithId().setBoolPrim(true).setBoolObj(true), // 3
                    newEntityWithId().setBoolPrim(false).setBoolObj(false), // 4
                    newEntityWithId().setBoolPrim(true).setBoolObj(null), // 5
                    newEntityWithId().setBoolPrim(false).setBoolObj(null) // 6
                    );

            db.insert(entities, conn);

            var selected = db.select(sb -> sb.filter(f -> f.boolPrim().isTrue()), conn);
            assertEntities(listWith(entities, 0, 1, 3, 5), selected);

            selected = db.select(sb -> sb.filter(f -> f.boolObj().isTrue()), conn);
            assertEntities(listWith(entities, 0, 1, 3), selected);

            selected = db.select(sb -> sb.filter(f -> f.boolPrim().isFalse()), conn);
            assertEntities(listWith(entities, 2, 4, 6), selected);

            selected = db.select(sb -> sb.filter(f -> f.boolObj().isFalse()), conn);
            assertEntities(listWith(entities, 2, 4), selected);

            selected = db.select(sb -> sb.filter(f -> f.boolObj().isNull()), conn);
            assertEntities(listWith(entities, 5, 6), selected);
        }
    }

    @ParameterizedTest
    @MethodSource("dbs")
    public void selectFilteredByLong(Connection conn) throws SQLException {
        try (conn) {
            createTable(conn);

            var entities = List.of(
                    newEntityWithId().setLongPrim(1L), // 0
                    newEntityWithId().setLongPrim(10L), // 1
                    newEntityWithId().setLongPrim(2L), // 2
                    newEntityWithId().setLongPrim(5L), // 3
                    newEntityWithId().setLongPrim(2L), // 4
                    newEntityWithId().setLongPrim(100L), // 5
                    newEntityWithId().setLongPrim(1000L), // 6
                    newEntityWithId().setLongPrim(50L), // 7
                    newEntityWithId().setLongPrim(75L), // 8
                    newEntityWithId().setLongPrim(-10L), // 9
                    newEntityWithId().setLongPrim(-1L), // 10
                    newEntityWithId().setLongPrim(100L) // 11
                    );

            db.insert(entities, conn);

            var selected = db.select(sb -> sb.filter(f -> f.longPrim().isEqualTo(2L)), conn);
            assertEntities(listWith(entities, 2, 4), selected);

            selected = db.select(sb -> sb.filter(f -> f.longPrim().isLessThanOrEqualTo(5L)), conn);
            assertEntities(listWith(entities, 0, 2, 3, 4, 9, 10), selected);

            selected = db.select(sb -> sb.filter(f -> f.longPrim().isLessThan(5L)), conn);
            assertEntities(listWith(entities, 0, 2, 4, 9, 10), selected);

            selected = db.select(sb -> sb.filter(f -> f.longPrim().isGreaterThanOrEqualTo(100L)), conn);
            assertEntities(listWith(entities, 5, 6, 11), selected);

            selected = db.select(sb -> sb.filter(f -> f.longPrim().isGreaterThan(100L)), conn);
            assertEntities(listWith(entities, 6), selected);

            selected = db.select(sb -> sb.filter(f -> f.longPrim().isNotEqualTo(2L)), conn);
            assertEntities(listWithout(entities, 2, 4), selected);

            selected = db.select(sb -> sb.filter(f -> f.longPrim().isIn(List.of(1L, 3L, 5L, 7L, 9L))), conn);
            assertEntities(listWith(entities, 0, 3), selected);

            selected = db.select(sb -> sb.filter(f -> f.longPrim().isNotIn(List.of(10L, 100L, 1000L))), conn);
            assertEntities(listWithout(entities, 1, 5, 6, 11), selected);

            selected = db.select(
                    sb -> sb.filter(
                            f -> f.longPrim().isEqualTo(10L).or().longPrim().isEqualTo(-10L)),
                    conn);
            assertEntities(listWith(entities, 1, 9), selected);
        }
    }

    @ParameterizedTest
    @MethodSource("dbs")
    public void selectFilteredByInt(Connection conn) throws SQLException {
        try (conn) {
            createTable(conn);

            var entities = List.of(
                    newEntityWithId().setIntPrim(1), // 0
                    newEntityWithId().setIntPrim(10), // 1
                    newEntityWithId().setIntPrim(2), // 2
                    newEntityWithId().setIntPrim(5), // 3
                    newEntityWithId().setIntPrim(2), // 4
                    newEntityWithId().setIntPrim(100), // 5
                    newEntityWithId().setIntPrim(1000), // 6
                    newEntityWithId().setIntPrim(50), // 7
                    newEntityWithId().setIntPrim(75), // 8
                    newEntityWithId().setIntPrim(-10), // 9
                    newEntityWithId().setIntPrim(-1), // 10
                    newEntityWithId().setIntPrim(100) // 11
                    );

            db.insert(entities, conn);

            var selected = db.select(sb -> sb.filter(f -> f.intPrim().isEqualTo(2)), conn);
            assertEntities(listWith(entities, 2, 4), selected);

            selected = db.select(sb -> sb.filter(f -> f.intPrim().isLessThanOrEqualTo(5)), conn);
            assertEntities(listWith(entities, 0, 2, 3, 4, 9, 10), selected);

            selected = db.select(sb -> sb.filter(f -> f.intPrim().isLessThan(5)), conn);
            assertEntities(listWith(entities, 0, 2, 4, 9, 10), selected);

            selected = db.select(sb -> sb.filter(f -> f.intPrim().isGreaterThanOrEqualTo(100)), conn);
            assertEntities(listWith(entities, 5, 6, 11), selected);

            selected = db.select(sb -> sb.filter(f -> f.intPrim().isGreaterThan(100)), conn);
            assertEntities(listWith(entities, 6), selected);

            selected = db.select(sb -> sb.filter(f -> f.intPrim().isNotEqualTo(2)), conn);
            assertEntities(listWithout(entities, 2, 4), selected);

            selected = db.select(sb -> sb.filter(f -> f.intPrim().isIn(List.of(1, 3, 5, 7, 9))), conn);
            assertEntities(listWith(entities, 0, 3), selected);

            selected = db.select(sb -> sb.filter(f -> f.intPrim().isNotIn(List.of(10, 100, 1000))), conn);
            assertEntities(listWithout(entities, 1, 5, 6, 11), selected);

            selected = db.select(
                    sb -> sb.filter(
                            f -> f.intPrim().isEqualTo(10).or().intPrim().isEqualTo(-10)),
                    conn);
            assertEntities(listWith(entities, 1, 9), selected);
        }
    }

    @ParameterizedTest
    @MethodSource("dbs")
    public void selectFilteredByShort(Connection conn) throws SQLException {
        try (conn) {
            createTable(conn);

            var entities = List.of(
                    newEntityWithId().setShortPrim((short) 1), // 0
                    newEntityWithId().setShortPrim((short) 10), // 1
                    newEntityWithId().setShortPrim((short) 2), // 2
                    newEntityWithId().setShortPrim((short) 5), // 3
                    newEntityWithId().setShortPrim((short) 2), // 4
                    newEntityWithId().setShortPrim((short) 100), // 5
                    newEntityWithId().setShortPrim((short) 1000), // 6
                    newEntityWithId().setShortPrim((short) 50), // 7
                    newEntityWithId().setShortPrim((short) 75), // 8
                    newEntityWithId().setShortPrim((short) -10), // 9
                    newEntityWithId().setShortPrim((short) -1), // 10
                    newEntityWithId().setShortPrim((short) 100) // 11
                    );

            db.insert(entities, conn);

            var selected = db.select(sb -> sb.filter(f -> f.shortPrim().isEqualTo((short) 2)), conn);
            assertEntities(listWith(entities, 2, 4), selected);

            selected = db.select(sb -> sb.filter(f -> f.shortPrim().isLessThanOrEqualTo((short) 5)), conn);
            assertEntities(listWith(entities, 0, 2, 3, 4, 9, 10), selected);

            selected = db.select(sb -> sb.filter(f -> f.shortPrim().isLessThan((short) 5)), conn);
            assertEntities(listWith(entities, 0, 2, 4, 9, 10), selected);

            selected = db.select(sb -> sb.filter(f -> f.shortPrim().isGreaterThanOrEqualTo((short) 100)), conn);
            assertEntities(listWith(entities, 5, 6, 11), selected);

            selected = db.select(sb -> sb.filter(f -> f.shortPrim().isGreaterThan((short) 100)), conn);
            assertEntities(listWith(entities, 6), selected);

            selected = db.select(sb -> sb.filter(f -> f.shortPrim().isNotEqualTo((short) 2)), conn);
            assertEntities(listWithout(entities, 2, 4), selected);

            selected = db.select(
                    sb -> sb.filter(
                            f -> f.shortPrim().isIn(List.of((short) 1, (short) 3, (short) 5, (short) 7, (short) 9))),
                    conn);
            assertEntities(listWith(entities, 0, 3), selected);

            selected = db.select(
                    sb -> sb.filter(f -> f.shortPrim().isNotIn(List.of((short) 10, (short) 100, (short) 1000))), conn);
            assertEntities(listWithout(entities, 1, 5, 6, 11), selected);

            selected = db.select(
                    sb -> sb.filter(f ->
                            f.shortPrim().isEqualTo((short) 10).or().shortPrim().isEqualTo((short) -10)),
                    conn);
            assertEntities(listWith(entities, 1, 9), selected);
        }
    }

    @ParameterizedTest
    @MethodSource("dbs")
    public void selectFilteredByDouble(Connection conn) throws SQLException {
        try (conn) {
            createTable(conn);

            var entities = List.of(
                    newEntityWithId().setDoublePrim(1.5), // 0
                    newEntityWithId().setDoublePrim(10.1), // 1
                    newEntityWithId().setDoublePrim(2.0), // 2
                    newEntityWithId().setDoublePrim(5.6), // 3
                    newEntityWithId().setDoublePrim(2.12), // 4
                    newEntityWithId().setDoublePrim(100.01), // 5
                    newEntityWithId().setDoublePrim(1000.123), // 6
                    newEntityWithId().setDoublePrim(50.9), // 7
                    newEntityWithId().setDoublePrim(75.0), // 8
                    newEntityWithId().setDoublePrim(-10.1), // 9
                    newEntityWithId().setDoublePrim(-1.01), // 10
                    newEntityWithId().setDoublePrim(100.00) // 11
                    );

            db.insert(entities, conn);

            var selected = db.select(sb -> sb.filter(f -> f.doublePrim().isEqualTo(2)), conn);
            assertEntities(listWith(entities, 2), selected);

            selected = db.select(sb -> sb.filter(f -> f.doublePrim().isLessThanOrEqualTo(5.6)), conn);
            assertEntities(listWith(entities, 0, 2, 3, 4, 9, 10), selected);

            selected = db.select(sb -> sb.filter(f -> f.doublePrim().isLessThan(5.6)), conn);
            assertEntities(listWith(entities, 0, 2, 4, 9, 10), selected);

            selected = db.select(sb -> sb.filter(f -> f.doublePrim().isGreaterThanOrEqualTo(100.01)), conn);
            assertEntities(listWith(entities, 5, 6), selected);

            selected = db.select(sb -> sb.filter(f -> f.doublePrim().isGreaterThan(100.01)), conn);
            assertEntities(listWith(entities, 6), selected);

            selected = db.select(sb -> sb.filter(f -> f.doublePrim().isNotEqualTo(2)), conn);
            assertEntities(listWithout(entities, 2), selected);

            selected = db.select(sb -> sb.filter(f -> f.doublePrim().isIn(List.of(1.5, 3.1, 5.6, 7.2, 9.3))), conn);
            assertEntities(listWith(entities, 0, 3), selected);

            selected = db.select(sb -> sb.filter(f -> f.doublePrim().isNotIn(List.of(10.1, 100.01, 1000.123))), conn);
            assertEntities(listWithout(entities, 1, 5, 6), selected);

            selected = db.select(
                    sb -> sb.filter(f ->
                            f.doublePrim().isEqualTo(10.1).or().doublePrim().isEqualTo(-10.1)),
                    conn);
            assertEntities(listWith(entities, 1, 9), selected);
        }
    }

    @ParameterizedTest
    @MethodSource("dbs")
    public void selectFilteredByUuid(Connection conn) throws SQLException {
        try (conn) {
            createTable(conn);

            var uuid1 = UUID.randomUUID();
            var uuid2 = UUID.randomUUID();
            var uuid3 = UUID.randomUUID();
            var uuid4 = UUID.randomUUID();
            var uuid5 = UUID.randomUUID();

            var entities = List.of(
                    newEntityWithId().setUuid(uuid1), // 0
                    newEntityWithId().setUuid(uuid2), // 1
                    newEntityWithId().setUuid(uuid1), // 2
                    newEntityWithId().setUuid(uuid3), // 3
                    newEntityWithId().setUuid(uuid1), // 4
                    newEntityWithId().setUuid(uuid4), // 5
                    newEntityWithId().setUuid(uuid2), // 6
                    newEntityWithId().setUuid(uuid5), // 7
                    newEntityWithId().setUuid(null) // 8
                    );

            db.insert(entities, conn);

            var selected = db.select(sb -> sb.filter(f -> f.uuid().isEqualTo(uuid2)), conn);
            assertEntities(listWith(entities, 1, 6), selected);

            selected = db.select(sb -> sb.filter(f -> f.uuid().isNotEqualTo(uuid1)), conn);
            assertEntities(listWithout(entities, 0, 2, 4, 8), selected);

            selected = db.select(sb -> sb.filter(f -> f.uuid().isNull()), conn);
            assertEntities(listWith(entities, 8), selected);

            selected = db.select(sb -> sb.filter(f -> f.uuid().isNotNull()), conn);
            assertEntities(listWithout(entities, 8), selected);

            selected = db.select(sb -> sb.filter(f -> f.uuid().isIn(List.of(uuid3, uuid4))), conn);
            assertEntities(listWith(entities, 3, 5), selected);

            selected = db.select(sb -> sb.filter(f -> f.uuid().isNotIn(List.of(uuid3, uuid4))), conn);
            assertEntities(listWithout(entities, 3, 5, 8), selected);
        }
    }

    @ParameterizedTest
    @MethodSource("dbs")
    public void selectFilteredByTime(Connection conn) throws SQLException {
        try (conn) {
            createTable(conn);

            var time1 = TestUtil.nowLocalDateTime();
            var time2 = TestUtil.nowLocalDateTime().minusMinutes(10);
            var time3 = TestUtil.nowLocalDateTime().minusDays(7);
            var time4 = TestUtil.nowLocalDateTime().minusHours(12);
            var time5 = TestUtil.nowLocalDateTime().minusMonths(2);

            var entities = List.of(
                    newEntityWithId().setLocalDateTime(time1), // 0
                    newEntityWithId().setLocalDateTime(time2), // 1
                    newEntityWithId().setLocalDateTime(time1), // 2
                    newEntityWithId().setLocalDateTime(time3), // 3
                    newEntityWithId().setLocalDateTime(time1), // 4
                    newEntityWithId().setLocalDateTime(time4), // 5
                    newEntityWithId().setLocalDateTime(time2), // 6
                    newEntityWithId().setLocalDateTime(time5), // 7
                    newEntityWithId().setLocalDateTime(null) // 8
                    );

            db.insert(entities, conn);

            var selected = db.select(sb -> sb.filter(f -> f.localDateTime().isEqualTo(time2)), conn);
            assertEntities(listWith(entities, 1, 6), selected);

            selected = db.select(sb -> sb.filter(f -> f.localDateTime().isNotEqualTo(time2)), conn);
            assertEntities(listWithout(entities, 1, 6, 8), selected);

            selected = db.select(sb -> sb.filter(f -> f.localDateTime().isGreaterThan(time4)), conn);
            assertEntities(listWith(entities, 0, 1, 2, 4, 6), selected);

            selected = db.select(sb -> sb.filter(f -> f.localDateTime().isGreaterThanOrEqualTo(time4)), conn);
            assertEntities(listWith(entities, 0, 1, 2, 4, 5, 6), selected);

            selected = db.select(sb -> sb.filter(f -> f.localDateTime().isLessThan(time4)), conn);
            assertEntities(listWith(entities, 3, 7), selected);

            selected = db.select(sb -> sb.filter(f -> f.localDateTime().isLessThanOrEqualTo(time4)), conn);
            assertEntities(listWith(entities, 3, 5, 7), selected);

            selected = db.select(sb -> sb.filter(f -> f.localDateTime().isNull()), conn);
            assertEntities(listWith(entities, 8), selected);

            selected = db.select(sb -> sb.filter(f -> f.localDateTime().isNotNull()), conn);
            assertEntities(listWithout(entities, 8), selected);

            selected = db.select(
                    sb -> sb.filter(f -> f.localDateTime().isIn(List.of(time1, time5, TestUtil.nowLocalDateTime()))),
                    conn);
            assertEntities(listWith(entities, 0, 2, 4, 7), selected);

            selected = db.select(
                    sb -> sb.filter(f -> f.localDateTime().isNotIn(List.of(time1, time5, TestUtil.nowLocalDateTime()))),
                    conn);
            assertEntities(listWithout(entities, 0, 2, 4, 7, 8), selected);
        }
    }

    @ParameterizedTest
    @MethodSource("dbs")
    public void selectFilteredComplex(Connection conn) throws SQLException {
        try (conn) {
            createTable(conn);

            var uuid1 = UUID.randomUUID();
            var uuid2 = UUID.randomUUID();
            var uuid3 = UUID.randomUUID();
            var uuid4 = UUID.randomUUID();
            var uuid5 = UUID.randomUUID();

            var time1 = TestUtil.nowLocalDateTime();
            var time2 = TestUtil.nowLocalDateTime().minusMinutes(10);
            var time3 = TestUtil.nowLocalDateTime().minusDays(7);
            var time4 = TestUtil.nowLocalDateTime().minusHours(12);
            var time5 = TestUtil.nowLocalDateTime().minusMonths(2);

            var entities = List.of(
                    newEntityWithId()
                            .setString("one")
                            .setIntPrim(1)
                            .setUuid(uuid1)
                            .setLocalDateTime(null), // 0
                    newEntityWithId()
                            .setString("five")
                            .setIntPrim(2)
                            .setUuid(uuid2)
                            .setLocalDateTime(time3), // 1
                    newEntityWithId()
                            .setString("two")
                            .setIntPrim(3)
                            .setUuid(uuid1)
                            .setLocalDateTime(time2), // 2
                    newEntityWithId()
                            .setString("two")
                            .setIntPrim(4)
                            .setUuid(uuid3)
                            .setLocalDateTime(time4), // 3
                    newEntityWithId()
                            .setString("two")
                            .setIntPrim(1)
                            .setUuid(uuid1)
                            .setLocalDateTime(time1), // 4
                    newEntityWithId()
                            .setString(null)
                            .setIntPrim(2)
                            .setUuid(uuid4)
                            .setLocalDateTime(time2), // 5
                    newEntityWithId()
                            .setString("three")
                            .setIntPrim(3)
                            .setUuid(uuid2)
                            .setLocalDateTime(null), // 6
                    newEntityWithId()
                            .setString("one")
                            .setIntPrim(3)
                            .setUuid(uuid5)
                            .setLocalDateTime(time5), // 7
                    newEntityWithId()
                            .setString("four")
                            .setIntPrim(3)
                            .setUuid(null)
                            .setLocalDateTime(time1) // 8
                    );

            db.insert(entities, conn);

            var selected = db.select(
                    sb -> sb.filter(
                            f -> f.uuid().isEqualTo(uuid1).and().string().isEqualTo("two")),
                    conn);
            assertEntities(listWith(entities, 2, 4), selected);

            selected = db.select(
                    sb -> sb.filter(f -> f.localDateTime()
                            .isNull()
                            .and()
                            .group(gb ->
                                    gb.intPrim().isGreaterThan(1).or().string().isEqualTo("four"))),
                    conn);
            assertEntities(listWith(entities, 6), selected);

            selected = db.select(
                    sb -> sb.filter(f -> f.group(gb ->
                                    gb.intPrim().isGreaterThan(2).and().uuid().isNotIn(List.of(uuid3, uuid1)))
                            .or()
                            .localDateTime()
                            .isLessThan(TestUtil.nowLocalDateTime().minusHours(13))),
                    conn);
            assertEntities(listWith(entities, 1, 6, 7), selected);

            selected = db.select(
                    sb -> sb.filter(f -> f.notGroup(gb ->
                                    gb.string().isEqualTo("two").or().intPrim().isEqualTo(2))
                            .and()
                            .localDateTime()
                            .isGreaterThan(time2)),
                    conn);
            assertEntities(listWith(entities, 8), selected);
        }
    }

    @ParameterizedTest
    @MethodSource("dbs")
    public void filteredCount(Connection conn) throws SQLException {
        try (conn) {
            createTable(conn);

            var entities = List.of(
                    newEntityWithId().setString("a"), // 0
                    newEntityWithId().setString("example"), // 1
                    newEntityWithId().setString("test-1"), // 2
                    newEntityWithId().setString("TEST-1"), // 3
                    newEntityWithId().setString("TEST-1"), // 4
                    newEntityWithId().setString("m"), // 5
                    newEntityWithId().setString("test-2"), // 6
                    newEntityWithId().setString("z"), // 7
                    newEntityWithId().setString("w"), // 8
                    newEntityWithId().setString("test-3"), // 9
                    newEntityWithId().setString(null), // 10
                    newEntityWithId().setString(null) // 11
                    );

            db.insert(entities, conn);

            var count = db.count(fb -> fb.string().isLike("test-%"), conn);
            assertEquals(3, count);
        }
    }

    @ParameterizedTest
    @MethodSource("dbs")
    public void filteredDelete(Connection conn) throws SQLException {
        try (conn) {
            createTable(conn);

            var entities = List.of(
                    newEntityWithId().setString("a"), // 0
                    newEntityWithId().setString("example"), // 1
                    newEntityWithId().setString("test-1"), // 2
                    newEntityWithId().setString("TEST-1"), // 3
                    newEntityWithId().setString("TEST-1"), // 4
                    newEntityWithId().setString("m"), // 5
                    newEntityWithId().setString("test-2"), // 6
                    newEntityWithId().setString("z"), // 7
                    newEntityWithId().setString("w"), // 8
                    newEntityWithId().setString("test-3"), // 9
                    newEntityWithId().setString(null), // 10
                    newEntityWithId().setString(null) // 11
                    );

            db.insert(entities, conn);

            var deleted = db.delete(fb -> fb.string().isLike("test-%"), conn);
            assertEquals(3, deleted);

            assertDoesNotExist(entities.get(2).getLongId(), conn);
            assertDoesNotExist(entities.get(6).getLongId(), conn);
            assertDoesNotExist(entities.get(9).getLongId(), conn);
        }
    }

    @ParameterizedTest
    @MethodSource("dbs")
    public void selectFilteredByEnum(Connection conn) throws SQLException {
        try (conn) {
            createTable(conn);

            var entities = List.of(
                    newEntityWithId().setExampleEnum(ExampleEnum.THREE), // 0
                    newEntityWithId().setExampleEnum(ExampleEnum.ONE), // 1
                    newEntityWithId().setExampleEnum(ExampleEnum.TWO), // 2
                    newEntityWithId().setExampleEnum(ExampleEnum.TWO), // 3
                    newEntityWithId().setExampleEnum(ExampleEnum.THREE), // 4
                    newEntityWithId().setExampleEnum(ExampleEnum.THREE), // 5
                    newEntityWithId().setExampleEnum(null) // 6
                    );

            db.insert(entities, conn);

            var selected = db.select(sb -> sb.filter(f -> f.exampleEnum().isEqualTo(ExampleEnum.ONE)), conn);
            assertEntities(listWith(entities, 1), selected);

            selected = db.select(sb -> sb.filter(f -> f.exampleEnum().isNotEqualTo(ExampleEnum.THREE)), conn);
            assertEntities(listWithout(entities, 0, 4, 5, 6), selected);

            selected = db.select(
                    sb -> sb.filter(f -> f.exampleEnum().isIn(List.of(ExampleEnum.TWO, ExampleEnum.THREE))), conn);
            assertEntities(listWith(entities, 0, 2, 3, 4, 5), selected);

            selected = db.select(sb -> sb.filter(f -> f.exampleEnum().isNull()), conn);
            assertEntities(listWith(entities, 6), selected);

            selected = db.select(sb -> sb.filter(f -> f.exampleEnum().isNotNull()), conn);
            assertEntities(listWithout(entities, 6), selected);

            selected = db.select(sb -> sb.filter(f -> f.exampleEnum().isGreaterThan(ExampleEnum.ONE)), conn);
            assertEntities(listWithout(entities, 1, 6), selected);

            selected = db.select(sb -> sb.filter(f -> f.exampleEnum().isGreaterThanOrEqualTo(ExampleEnum.THREE)), conn);
            assertEntities(listWithout(entities, 1, 6), selected);

            selected = db.select(sb -> sb.filter(f -> f.exampleEnum().isLessThan(ExampleEnum.THREE)), conn);
            assertEntities(listWith(entities, 1), selected);

            selected = db.select(sb -> sb.filter(f -> f.exampleEnum().isLessThanOrEqualTo(ExampleEnum.THREE)), conn);
            assertEntities(listWith(entities, 0, 1, 4, 5), selected);
        }
    }

    @ParameterizedTest
    @MethodSource("dbs")
    public void selectSortByEnum(Connection conn) throws SQLException {
        try (conn) {
            createTable(conn);

            var entities = List.of(
                    newEntityWithId().setExampleEnum(ExampleEnum.THREE), // 0
                    newEntityWithId().setExampleEnum(ExampleEnum.ONE), // 1
                    newEntityWithId().setExampleEnum(ExampleEnum.TWO), // 2
                    newEntityWithId().setExampleEnum(ExampleEnum.TWO), // 3
                    newEntityWithId().setExampleEnum(ExampleEnum.THREE), // 4
                    newEntityWithId().setExampleEnum(ExampleEnum.THREE), // 5
                    newEntityWithId().setExampleEnum(null) // 6
                    );

            db.insert(entities, conn);

            var selected = db.select(sb -> sb.sort(s -> s.exampleEnumAsc()), conn);
            assertEntities(
                    List.of(
                            entities.get(6),
                            entities.get(1),
                            entities.get(0),
                            entities.get(4),
                            entities.get(5),
                            entities.get(2),
                            entities.get(3)),
                    selected);
        }
    }

    @ParameterizedTest
    @MethodSource("dbs")
    public void paginate(Connection conn) throws SQLException {
        try (conn) {
            createTable(conn);

            var entities = List.of(
                    newEntityWithId().setString("a"), // 0
                    newEntityWithId().setString("example"), // 1
                    newEntityWithId().setString("test-1"), // 2
                    newEntityWithId().setString("TEST-1"), // 3
                    newEntityWithId().setString("TEST-1"), // 4
                    newEntityWithId().setString("m"), // 5
                    newEntityWithId().setString("test-2"), // 6
                    newEntityWithId().setString("z"), // 7
                    newEntityWithId().setString("w"), // 8
                    newEntityWithId().setString("test-3"), // 9
                    newEntityWithId().setString(null) // 10
                    );

            db.insert(entities, conn);

            var selected = db.select(sb -> sb.paginate(0, 3), conn);
            assertEntities(listWith(entities, 0, 1, 2), selected);

            selected = db.select(sb -> sb.paginate(1, 3), conn);
            assertEntities(listWith(entities, 3, 4, 5), selected);

            selected = db.select(sb -> sb.paginate(2, 3), conn);
            assertEntities(listWith(entities, 6, 7, 8), selected);

            selected = db.select(sb -> sb.paginate(3, 3), conn);
            assertEntities(listWith(entities, 9, 10), selected);

            selected = db.select(sb -> sb.paginate(4, 3), conn);
            assertThat(selected).isEmpty();
        }
    }

    @ParameterizedTest
    @MethodSource("dbs")
    public void sortedPaginated(Connection conn) throws SQLException {
        try (conn) {
            createTable(conn);

            var entities = List.of(
                    newEntityWithId().setString("a"), // 0
                    newEntityWithId().setString("example"), // 1
                    newEntityWithId().setString("test-1"), // 2
                    newEntityWithId().setString("TEST-1"), // 3
                    newEntityWithId().setString("TEST-1"), // 4
                    newEntityWithId().setString("m"), // 5
                    newEntityWithId().setString("test-2"), // 6
                    newEntityWithId().setString("z"), // 7
                    newEntityWithId().setString("w"), // 8
                    newEntityWithId().setString("test-3"), // 9
                    newEntityWithId().setString(null) // 10
                    );

            db.insert(entities, conn);

            var selected = db.select(sb -> sb.sort(s -> s.longIdDesc()).paginate(0, 3), conn);
            assertEntities(listWith(entities, 10, 9, 8), selected);

            selected = db.select(sb -> sb.sort(s -> s.longIdDesc()).paginate(1, 3), conn);
            assertEntities(listWith(entities, 7, 6, 5), selected);

            selected = db.select(sb -> sb.sort(s -> s.longIdDesc()).paginate(2, 3), conn);
            assertEntities(listWith(entities, 4, 3, 2), selected);

            selected = db.select(sb -> sb.sort(s -> s.longIdDesc()).paginate(3, 3), conn);
            assertEntities(listWith(entities, 1, 0), selected);

            selected = db.select(sb -> sb.sort(s -> s.longIdDesc()).paginate(4, 3), conn);
            assertThat(selected).isEmpty();
        }
    }

    @ParameterizedTest
    @MethodSource("dbs")
    public void filteredSortedPaginated(Connection conn) throws SQLException {
        try (conn) {
            createTable(conn);

            var entities = List.of(
                    newEntityWithId().setString("a"), // 0
                    newEntityWithId().setString("example"), // 1
                    newEntityWithId().setString("test-1"), // 2
                    newEntityWithId().setString("TEST-1"), // 3
                    newEntityWithId().setString("TEST-1"), // 4
                    newEntityWithId().setString("m"), // 5
                    newEntityWithId().setString("test-2"), // 6
                    newEntityWithId().setString("z"), // 7
                    newEntityWithId().setString("w"), // 8
                    newEntityWithId().setString("test-3"), // 9
                    newEntityWithId().setString(null) // 10
                    );

            db.insert(entities, conn);

            var selected = db.select(
                    sb -> sb.filter(f -> f.string().isLikeInsensitive("test-%"))
                            .sort(s -> s.longIdDesc())
                            .paginate(0, 3),
                    conn);
            assertEntities(listWith(entities, 9, 6, 4), selected);

            selected = db.select(
                    sb -> sb.filter(f -> f.string().isLikeInsensitive("test-%"))
                            .sort(s -> s.longIdDesc())
                            .paginate(1, 3),
                    conn);
            assertEntities(listWith(entities, 3, 2), selected);
        }
    }

    @Override
    protected Long getId(GetterSetterAllTypesEntity entity) {
        return entity.getLongId();
    }

    @Override
    protected GetterSetterAllTypesEntity setId(Long id, GetterSetterAllTypesEntity entity) {
        return entity.clone().setLongId(id);
    }

    @Override
    protected GetterSetterAllTypesEntity newEntity() {
        return new GetterSetterAllTypesEntity()
                .setLongPrim(RandomUtils.nextLong())
                .setIntObj(RandomUtils.nextInt())
                .setIntPrim(RandomUtils.nextInt())
                .setShortObj((short) 1)
                .setShortPrim((short) 2)
                .setDoubleObj(RandomUtils.nextDouble())
                .setDoublePrim(RandomUtils.nextDouble())
                .setBoolObj(true)
                .setBoolPrim(false)
                .setString(RandomStringUtils.randomAlphanumeric(10))
                .setInstant(TestUtil.now())
                .setLocalDateTime(TestUtil.nowLocalDateTime())
                .setLocalDate(LocalDate.now())
                .setOffsetDateTime(TestUtil.nowOffsetDateTime())
                .setDate(new Date(2023, 6, 25))
                .setTimestamp(new Timestamp(System.currentTimeMillis()))
                .setByteArray(RandomUtils.nextBytes(10))
                .setUuid(UUID.randomUUID())
                .setExampleEnum(ExampleEnum.TWO);
    }

    @Override
    protected GetterSetterAllTypesEntity newEntityWithId() {
        return newEntity().setLongId(nextId());
    }

    @Override
    protected GetterSetterAllTypesEntity updateEntity(GetterSetterAllTypesEntity entity) {
        return entity.clone()
                .setLongPrim(RandomUtils.nextLong())
                .setIntObj(RandomUtils.nextInt())
                .setIntPrim(RandomUtils.nextInt())
                .setShortObj((short) 3)
                .setShortPrim((short) 4)
                .setDoubleObj(RandomUtils.nextDouble())
                .setDoublePrim(RandomUtils.nextDouble())
                .setBoolObj(false)
                .setBoolPrim(true)
                .setString(RandomStringUtils.randomAlphanumeric(10))
                .setInstant(TestUtil.now())
                .setLocalDateTime(TestUtil.nowLocalDateTime())
                .setLocalDate(LocalDate.now())
                .setOffsetDateTime(TestUtil.nowOffsetDateTime())
                .setDate(new Date(2023, 6, 26))
                .setTimestamp(new Timestamp(System.currentTimeMillis()))
                .setByteArray(RandomUtils.nextBytes(10))
                .setUuid(UUID.randomUUID())
                .setExampleEnum(ExampleEnum.THREE);
    }

    @Override
    protected GetterSetterAllTypesEntityDb.Patch patchAll(GetterSetterAllTypesEntity entity) {
        return new GetterSetterAllTypesEntityDb.Patch()
                .setLongPrim(entity.getLongPrim())
                .setIntObj(entity.getIntObj())
                .setIntPrim(entity.getIntPrim())
                .setShortObj(entity.getShortObj())
                .setShortPrim(entity.getShortPrim())
                .setDoubleObj(entity.getDoubleObj())
                .setDoublePrim(entity.getDoublePrim())
                .setBoolObj(entity.getBoolObj())
                .setBoolPrim(entity.isBoolPrim())
                .setString(entity.getString())
                .setInstant(entity.getInstant())
                .setLocalDateTime(entity.getLocalDateTime())
                .setLocalDate(entity.getLocalDate())
                .setOffsetDateTime(entity.getOffsetDateTime())
                .setDate(entity.getDate())
                .setTimestamp(entity.getTimestamp())
                .setByteArray(entity.getByteArray())
                .setUuid(entity.getUuid())
                .setExampleEnum(entity.getExampleEnum());
    }

    @Override
    protected GetterSetterAllTypesEntity nullEntity(GetterSetterAllTypesEntity entity) {
        return entity.clone()
                .setIntObj(null)
                .setShortObj(null)
                .setDoubleObj(null)
                .setBoolObj(null)
                .setString(null)
                .setInstant(null)
                .setLocalDateTime(null)
                .setLocalDate(null)
                .setOffsetDateTime(null)
                .setDate(null)
                .setTimestamp(null)
                .setByteArray(null)
                .setUuid(null)
                .setExampleEnum(null);
    }

    @Override
    protected GetterSetterAllTypesEntityDb.Patch nullPatchAll() {
        return new GetterSetterAllTypesEntityDb.Patch()
                .setIntObj(null)
                .setShortObj(null)
                .setDoubleObj(null)
                .setBoolObj(null)
                .setString(null)
                .setInstant(null)
                .setLocalDateTime(null)
                .setLocalDate(null)
                .setOffsetDateTime(null)
                .setDate(null)
                .setTimestamp(null)
                .setByteArray(null)
                .setUuid(null)
                .setExampleEnum(null);
    }

    @Override
    protected Pair<GetterSetterAllTypesEntity, GetterSetterAllTypesEntityDb.Patch> patchPartial(
            GetterSetterAllTypesEntity entity) {
        var updated = entity.clone()
                .setString(RandomStringUtils.randomAlphanumeric(15))
                .setUuid(UUID.randomUUID())
                .setInstant(TestUtil.now())
                .setDate(null);

        return ImmutablePair.of(
                updated,
                new GetterSetterAllTypesEntityDb.Patch()
                        .setString(updated.getString())
                        .setUuid(updated.getUuid())
                        .setInstant(updated.getInstant())
                        .setDate(updated.getDate()));
    }

    @Override
    protected GetterSetterAllTypesEntityDb.Patch addRequiredFields(
            GetterSetterAllTypesEntity entity, GetterSetterAllTypesEntityDb.Patch patch) {
        return patch.setBoolPrim(entity.isBoolPrim())
                .setDoublePrim(entity.getDoublePrim())
                .setIntPrim(entity.getIntPrim())
                .setShortPrim(entity.getShortPrim())
                .setLongPrim(entity.getLongPrim());
    }

    private List<GetterSetterAllTypesEntity> listWith(List<GetterSetterAllTypesEntity> original, int... indicesToKeep) {
        var copy = new ArrayList<GetterSetterAllTypesEntity>(indicesToKeep.length);
        for (var i : indicesToKeep) {
            copy.add(original.get(i));
        }
        return copy;
    }

    private List<GetterSetterAllTypesEntity> listWithout(
            List<GetterSetterAllTypesEntity> original, int... indicesToRemove) {
        var copy = new ArrayList<>(original);
        for (int i = 0; i < indicesToRemove.length; i++) {
            copy.remove(indicesToRemove[i] - i);
        }
        return copy;
    }
}
