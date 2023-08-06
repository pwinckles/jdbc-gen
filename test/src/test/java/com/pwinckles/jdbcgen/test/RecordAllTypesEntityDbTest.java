package com.pwinckles.jdbcgen.test;

import com.pwinckles.jdbcgen.test.util.TestUtil;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class RecordAllTypesEntityDbTest
        extends BaseAllTypesTest<
                RecordAllTypesEntity,
                Long,
                RecordAllTypesEntityDb.Patch,
                RecordAllTypesEntityDb.FilterBuilder,
                RecordAllTypesEntityDb.SortBuilder> {

    public RecordAllTypesEntityDbTest() {
        super(new RecordAllTypesEntityDb());
    }

    @Override
    protected Long getId(RecordAllTypesEntity entity) {
        return entity.longId();
    }

    @Override
    protected RecordAllTypesEntity setId(Long id, RecordAllTypesEntity entity) {
        return new RecordAllTypesEntity(
                id,
                entity.longPrim(),
                entity.intObj(),
                entity.intPrim(),
                entity.shortObj(),
                entity.shortPrim(),
                entity.doubleObj(),
                entity.doublePrim(),
                entity.boolObj(),
                entity.boolPrim(),
                entity.string(),
                entity.instant(),
                entity.localDateTime(),
                entity.localDate(),
                entity.offsetDateTime(),
                entity.date(),
                entity.timestamp(),
                entity.byteArray(),
                entity.uuid(),
                entity.exampleEnum());
    }

    @Override
    protected RecordAllTypesEntity newEntity() {
        return new RecordAllTypesEntity(
                null,
                RandomUtils.nextLong(),
                RandomUtils.nextInt(),
                RandomUtils.nextInt(),
                (short) 1,
                (short) 2,
                RandomUtils.nextDouble(),
                RandomUtils.nextDouble(),
                true,
                false,
                RandomStringUtils.randomAlphanumeric(10),
                TestUtil.now(),
                TestUtil.nowLocalDateTime(),
                LocalDate.now(),
                TestUtil.nowOffsetDateTime(),
                new Date(2023, 6, 25),
                new Timestamp(System.currentTimeMillis()),
                RandomUtils.nextBytes(10),
                UUID.randomUUID(),
                ExampleEnum.TWO);
    }

    @Override
    protected RecordAllTypesEntity newEntityWithId() {
        return setId(nextId(), newEntity());
    }

    @Override
    protected RecordAllTypesEntity updateEntity(RecordAllTypesEntity entity) {
        return new RecordAllTypesEntity(
                entity.longId(),
                RandomUtils.nextLong(),
                RandomUtils.nextInt(),
                RandomUtils.nextInt(),
                (short) 3,
                (short) 4,
                RandomUtils.nextDouble(),
                RandomUtils.nextDouble(),
                false,
                true,
                RandomStringUtils.randomAlphanumeric(10),
                TestUtil.now(),
                TestUtil.nowLocalDateTime(),
                LocalDate.now(),
                TestUtil.nowOffsetDateTime(),
                new Date(2023, 6, 26),
                new Timestamp(System.currentTimeMillis()),
                RandomUtils.nextBytes(10),
                UUID.randomUUID(),
                ExampleEnum.THREE);
    }

    @Override
    protected RecordAllTypesEntityDb.Patch patchAll(RecordAllTypesEntity entity) {
        return new RecordAllTypesEntityDb.Patch()
                .setLongPrim(entity.longPrim())
                .setIntObj(entity.intObj())
                .setIntPrim(entity.intPrim())
                .setShortObj(entity.shortObj())
                .setShortPrim(entity.shortPrim())
                .setDoubleObj(entity.doubleObj())
                .setDoublePrim(entity.doublePrim())
                .setBoolObj(entity.boolObj())
                .setBoolPrim(entity.boolPrim())
                .setString(entity.string())
                .setInstant(entity.instant())
                .setLocalDateTime(entity.localDateTime())
                .setLocalDate(entity.localDate())
                .setOffsetDateTime(entity.offsetDateTime())
                .setDate(entity.date())
                .setTimestamp(entity.timestamp())
                .setByteArray(entity.byteArray())
                .setUuid(entity.uuid())
                .setExampleEnum(entity.exampleEnum());
    }

    @Override
    protected RecordAllTypesEntity nullEntity(RecordAllTypesEntity entity) {
        return new RecordAllTypesEntity(
                entity.longId(),
                entity.longPrim(),
                null,
                entity.intPrim(),
                null,
                entity.shortPrim(),
                null,
                entity.doublePrim(),
                null,
                entity.boolPrim(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    @Override
    protected RecordAllTypesEntityDb.Patch nullPatchAll() {
        return new RecordAllTypesEntityDb.Patch()
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
    protected Pair<RecordAllTypesEntity, RecordAllTypesEntityDb.Patch> patchPartial(RecordAllTypesEntity entity) {
        var updated = new RecordAllTypesEntity(
                entity.longId(),
                entity.longPrim(),
                entity.intObj(),
                entity.intPrim(),
                entity.shortObj(),
                entity.shortPrim(),
                entity.doubleObj(),
                entity.doublePrim(),
                entity.boolObj(),
                entity.boolPrim(),
                RandomStringUtils.randomAlphanumeric(15),
                TestUtil.now(),
                entity.localDateTime(),
                entity.localDate(),
                entity.offsetDateTime(),
                null,
                entity.timestamp(),
                entity.byteArray(),
                UUID.randomUUID(),
                entity.exampleEnum());

        return ImmutablePair.of(
                updated,
                new RecordAllTypesEntityDb.Patch()
                        .setString(updated.string())
                        .setUuid(updated.uuid())
                        .setInstant(updated.instant())
                        .setDate(updated.date()));
    }

    @Override
    protected RecordAllTypesEntityDb.Patch addRequiredFields(
            RecordAllTypesEntity entity, RecordAllTypesEntityDb.Patch patch) {
        return patch.setBoolPrim(entity.boolPrim())
                .setDoublePrim(entity.doublePrim())
                .setIntPrim(entity.intPrim())
                .setShortPrim(entity.shortPrim())
                .setLongPrim(entity.longPrim());
    }
}
