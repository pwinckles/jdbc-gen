package com.pwinckles.jdbcgen.test;

import com.pwinckles.jdbcgen.OrderDirection;
import com.pwinckles.jdbcgen.test.util.TestUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class GetterSetterAllTypesEntityDbTest extends BaseAllTypesTest<GetterSetterAllTypesEntity, Long, GetterSetterAllTypesEntityDb.Patch, GetterSetterAllTypesEntityDb.Column> {

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
                    newEntityWithId().setString("a")
            ));

            db.insert(originals, conn);

            var results = db.selectAll(GetterSetterAllTypesEntityDb.Column.LONG_ID, OrderDirection.ASCENDING, conn);
            assertEntities(originals, results);

            results = db.selectAll(GetterSetterAllTypesEntityDb.Column.STRING, OrderDirection.DESCENDING, conn);
            assertEntities(originals, results);

            Collections.reverse(originals);

            results = db.selectAll(GetterSetterAllTypesEntityDb.Column.LONG_ID, OrderDirection.DESCENDING, conn);
            assertEntities(originals, results);

            results = db.selectAll(GetterSetterAllTypesEntityDb.Column.STRING, OrderDirection.ASCENDING, conn);
            assertEntities(originals, results);
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
                .setUuid(UUID.randomUUID());
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
                .setUuid(UUID.randomUUID());
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
                .setUuid(entity.getUuid());
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
                .setUuid(null);
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
                .setUuid(null);
    }

    @Override
    protected Pair<GetterSetterAllTypesEntity, GetterSetterAllTypesEntityDb.Patch> patchPartial(GetterSetterAllTypesEntity entity) {
        var updated = entity.clone()
                .setString(RandomStringUtils.randomAlphanumeric(15))
                .setUuid(UUID.randomUUID())
                .setInstant(TestUtil.now())
                .setDate(null);

        return ImmutablePair.of(updated, new GetterSetterAllTypesEntityDb.Patch()
                .setString(updated.getString())
                .setUuid(updated.getUuid())
                .setInstant(updated.getInstant())
                .setDate(updated.getDate()));
    }

    @Override
    protected GetterSetterAllTypesEntityDb.Patch addRequiredFields(GetterSetterAllTypesEntity entity, GetterSetterAllTypesEntityDb.Patch patch) {
        return patch.setBoolPrim(entity.isBoolPrim())
                .setDoublePrim(entity.getDoublePrim())
                .setIntPrim(entity.getIntPrim())
                .setShortPrim(entity.getShortPrim())
                .setLongPrim(entity.getLongPrim());
    }

}
