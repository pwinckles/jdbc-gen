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

public class GetterConstructorAllTypesEntityDbTest
        extends BaseAllTypesTest<
                GetterConstructorAllTypesEntity,
                Long,
                GetterConstructorAllTypesEntityDb.Patch,
                GetterConstructorAllTypesEntityDb.Column> {

    public GetterConstructorAllTypesEntityDbTest() {
        super(new GetterConstructorAllTypesEntityDb());
    }

    @Override
    protected Long getId(GetterConstructorAllTypesEntity entity) {
        return entity.getLongId();
    }

    @Override
    protected GetterConstructorAllTypesEntity setId(Long id, GetterConstructorAllTypesEntity entity) {
        return new GetterConstructorAllTypesEntity(
                id,
                entity.getLongPrim(),
                entity.getIntObj(),
                entity.getIntPrim(),
                entity.getShortObj(),
                entity.getShortPrim(),
                entity.getDoubleObj(),
                entity.getDoublePrim(),
                entity.getBoolObj(),
                entity.isBoolPrim(),
                entity.getString(),
                entity.getInstant(),
                entity.getLocalDateTime(),
                entity.getLocalDate(),
                entity.getOffsetDateTime(),
                entity.getDate(),
                entity.getTimestamp(),
                entity.getByteArray(),
                entity.getUuid());
    }

    @Override
    protected GetterConstructorAllTypesEntity newEntity() {
        return new GetterConstructorAllTypesEntity(
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
                UUID.randomUUID());
    }

    @Override
    protected GetterConstructorAllTypesEntity newEntityWithId() {
        return setId(nextId(), newEntity());
    }

    @Override
    protected GetterConstructorAllTypesEntity updateEntity(GetterConstructorAllTypesEntity entity) {
        return new GetterConstructorAllTypesEntity(
                entity.getLongId(),
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
                UUID.randomUUID());
    }

    @Override
    protected GetterConstructorAllTypesEntityDb.Patch patchAll(GetterConstructorAllTypesEntity entity) {
        return new GetterConstructorAllTypesEntityDb.Patch()
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
    protected GetterConstructorAllTypesEntity nullEntity(GetterConstructorAllTypesEntity entity) {
        return new GetterConstructorAllTypesEntity(
                entity.getLongId(),
                entity.getLongPrim(),
                null,
                entity.getIntPrim(),
                null,
                entity.getShortPrim(),
                null,
                entity.getDoublePrim(),
                null,
                entity.isBoolPrim(),
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
    protected GetterConstructorAllTypesEntityDb.Patch nullPatchAll() {
        return new GetterConstructorAllTypesEntityDb.Patch()
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
    protected Pair<GetterConstructorAllTypesEntity, GetterConstructorAllTypesEntityDb.Patch> patchPartial(
            GetterConstructorAllTypesEntity entity) {
        var updated = new GetterConstructorAllTypesEntity(
                entity.getLongId(),
                entity.getLongPrim(),
                entity.getIntObj(),
                entity.getIntPrim(),
                entity.getShortObj(),
                entity.getShortPrim(),
                entity.getDoubleObj(),
                entity.getDoublePrim(),
                entity.getBoolObj(),
                entity.isBoolPrim(),
                RandomStringUtils.randomAlphanumeric(15),
                TestUtil.now(),
                entity.getLocalDateTime(),
                entity.getLocalDate(),
                entity.getOffsetDateTime(),
                null,
                entity.getTimestamp(),
                entity.getByteArray(),
                UUID.randomUUID());

        return ImmutablePair.of(
                updated,
                new GetterConstructorAllTypesEntityDb.Patch()
                        .setString(updated.getString())
                        .setUuid(updated.getUuid())
                        .setInstant(updated.getInstant())
                        .setDate(updated.getDate()));
    }

    @Override
    protected GetterConstructorAllTypesEntityDb.Patch addRequiredFields(
            GetterConstructorAllTypesEntity entity, GetterConstructorAllTypesEntityDb.Patch patch) {
        return patch.setBoolPrim(entity.isBoolPrim())
                .setDoublePrim(entity.getDoublePrim())
                .setIntPrim(entity.getIntPrim())
                .setShortPrim(entity.getShortPrim())
                .setLongPrim(entity.getLongPrim());
    }
}
