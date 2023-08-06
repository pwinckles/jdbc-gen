package com.pwinckles.jdbcgen.test;

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

public class EscapedQuotedEntityDbTest
        extends TestBase<
                QuotedEntity, Long, QuotedEntityDb.Patch, QuotedEntityDb.FilterBuilder, QuotedEntityDb.SortBuilder> {

    public EscapedQuotedEntityDbTest() {
        super(new QuotedEntityDb());
    }

    @Override
    protected void createTable(Connection conn) throws SQLException {
        try (var stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE \"all_types\" ("
                    + "\"at_id\" IDENTITY PRIMARY KEY,"
                    + " \"at_long_prim\" BIGINT NOT NULL,"
                    + " \"at_int_obj\" INT,"
                    + " \"at_int_prim\" INT NOT NULL,"
                    + " \"at_short_obj\" SMALLINT,"
                    + " \"at_short_prim\" SMALLINT NOT NULL,"
                    + " \"at_double_obj\" DOUBLE,"
                    + " \"at_double_prim\" DOUBLE NOT NULL,"
                    + " \"at_bool_obj\" BOOLEAN,"
                    + " \"at_bool_prim\" BOOLEAN NOT NULL,"
                    + " \"at_string\" VARCHAR(255),"
                    + " \"at_instant\" TIMESTAMP,"
                    + " \"at_local_date_time\" TIMESTAMP,"
                    + " \"at_local_date\" DATE,"
                    + " \"at_offset_date_time\" TIMESTAMP WITH TIME ZONE,"
                    + " \"at_date\" DATE,"
                    + " \"at_timestamp\" TIMESTAMP,"
                    + " \"at_byte_array\" BLOB,"
                    + " \"at_uuid\" UUID,"
                    + " \"at_enum\" VARCHAR(255)"
                    + ")");
        }
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

            var results = db.selectAll(sb -> sb.longIdAsc(), conn);
            assertEntities(originals, results);

            results = db.selectAll(sb -> sb.stringDesc(), conn);
            assertEntities(originals, results);

            Collections.reverse(originals);

            results = db.selectAll(sb -> sb.longIdDesc(), conn);
            assertEntities(originals, results);

            results = db.selectAll(sb -> sb.stringAsc(), conn);
            assertEntities(originals, results);
        }
    }

    @Override
    protected Long getId(QuotedEntity entity) {
        return entity.getLongId();
    }

    @Override
    protected QuotedEntity setId(Long id, QuotedEntity entity) {
        return entity.clone().setLongId(id);
    }

    @Override
    protected QuotedEntity newEntity() {
        return new QuotedEntity()
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
    protected QuotedEntity newEntityWithId() {
        return newEntity().setLongId(nextId());
    }

    @Override
    protected QuotedEntity updateEntity(QuotedEntity entity) {
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
    protected QuotedEntityDb.Patch patchAll(QuotedEntity entity) {
        return new QuotedEntityDb.Patch()
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
    protected QuotedEntity nullEntity(QuotedEntity entity) {
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
    protected QuotedEntityDb.Patch nullPatchAll() {
        return new QuotedEntityDb.Patch()
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
    protected Pair<QuotedEntity, QuotedEntityDb.Patch> patchPartial(QuotedEntity entity) {
        var updated = entity.clone()
                .setString(RandomStringUtils.randomAlphanumeric(15))
                .setUuid(UUID.randomUUID())
                .setInstant(TestUtil.now())
                .setDate(null);

        return ImmutablePair.of(
                updated,
                new QuotedEntityDb.Patch()
                        .setString(updated.getString())
                        .setUuid(updated.getUuid())
                        .setInstant(updated.getInstant())
                        .setDate(updated.getDate()));
    }

    @Override
    protected QuotedEntityDb.Patch addRequiredFields(QuotedEntity entity, QuotedEntityDb.Patch patch) {
        return patch.setBoolPrim(entity.isBoolPrim())
                .setDoublePrim(entity.getDoublePrim())
                .setIntPrim(entity.getIntPrim())
                .setShortPrim(entity.getShortPrim())
                .setLongPrim(entity.getLongPrim());
    }
}
