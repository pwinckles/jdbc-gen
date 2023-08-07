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

public class GetterSetterAllTypesEntityInnerDbTest
        extends BaseAllTypesTest<
                Wrapper.GetterSetterAllTypesEntity,
                Long,
                GetterSetterAllTypesEntityInnerDb.Patch,
                GetterSetterAllTypesEntityInnerDb.FilterBuilder,
                GetterSetterAllTypesEntityInnerDb.SortBuilder> {

    public GetterSetterAllTypesEntityInnerDbTest() {
        super(new GetterSetterAllTypesEntityInnerDb());
    }

    @Override
    protected Long getId(Wrapper.GetterSetterAllTypesEntity entity) {
        return entity.getLongId();
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

    @Override
    protected Wrapper.GetterSetterAllTypesEntity setId(Long id, Wrapper.GetterSetterAllTypesEntity entity) {
        return entity.clone().setLongId(id);
    }

    @Override
    protected Wrapper.GetterSetterAllTypesEntity newEntity() {
        return new Wrapper.GetterSetterAllTypesEntity()
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
    protected Wrapper.GetterSetterAllTypesEntity newEntityWithId() {
        return newEntity().setLongId(nextId());
    }

    @Override
    protected Wrapper.GetterSetterAllTypesEntity updateEntity(Wrapper.GetterSetterAllTypesEntity entity) {
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
    protected GetterSetterAllTypesEntityInnerDb.Patch patchAll(Wrapper.GetterSetterAllTypesEntity entity) {
        return new GetterSetterAllTypesEntityInnerDb.Patch()
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
    protected Wrapper.GetterSetterAllTypesEntity nullEntity(Wrapper.GetterSetterAllTypesEntity entity) {
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
    protected GetterSetterAllTypesEntityInnerDb.Patch nullPatchAll() {
        return new GetterSetterAllTypesEntityInnerDb.Patch()
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
    protected Pair<Wrapper.GetterSetterAllTypesEntity, GetterSetterAllTypesEntityInnerDb.Patch> patchPartial(
            Wrapper.GetterSetterAllTypesEntity entity) {
        var updated = entity.clone()
                .setString(RandomStringUtils.randomAlphanumeric(15))
                .setUuid(UUID.randomUUID())
                .setInstant(TestUtil.now())
                .setDate(null);

        return ImmutablePair.of(
                updated,
                new GetterSetterAllTypesEntityInnerDb.Patch()
                        .setString(updated.getString())
                        .setUuid(updated.getUuid())
                        .setInstant(updated.getInstant())
                        .setDate(updated.getDate()));
    }

    @Override
    protected GetterSetterAllTypesEntityInnerDb.Patch addRequiredFields(
            Wrapper.GetterSetterAllTypesEntity entity, GetterSetterAllTypesEntityInnerDb.Patch patch) {
        return patch.setBoolPrim(entity.isBoolPrim())
                .setDoublePrim(entity.getDoublePrim())
                .setIntPrim(entity.getIntPrim())
                .setShortPrim(entity.getShortPrim())
                .setLongPrim(entity.getLongPrim());
    }
}
