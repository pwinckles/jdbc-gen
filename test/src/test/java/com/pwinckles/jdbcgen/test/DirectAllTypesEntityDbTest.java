package com.pwinckles.jdbcgen.test;

import com.pwinckles.jdbcgen.test.util.TestUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;

public class DirectAllTypesEntityDbTest extends BaseAllTypesTest<DirectAllTypesEntity, Long, DirectAllTypesEntityDb.Patch, DirectAllTypesEntityDb.Column> {

    public DirectAllTypesEntityDbTest() {
        super(new DirectAllTypesEntityDb());
    }

    @Override
    protected Long getId(DirectAllTypesEntity entity) {
        return entity.longId;
    }

    @Override
    protected DirectAllTypesEntity setId(Long id, DirectAllTypesEntity entity) {
        var updated = entity.clone();
        updated.longId = id;
        return updated;
    }

    @Override
    protected DirectAllTypesEntity newEntity() {
        var entity = new DirectAllTypesEntity();
        entity.intObj = RandomUtils.nextInt();
        entity.intPrim = RandomUtils.nextInt();
        entity.shortObj = (short) 1;
        entity.shortPrim = (short) 2;
        entity.doubleObj = RandomUtils.nextDouble();
        entity.doublePrim = RandomUtils.nextDouble();
        entity.boolObj = true;
        entity.boolPrim = false;
        entity.string = RandomStringUtils.randomAlphanumeric(10);
        entity.instant = TestUtil.now();
        entity.localDateTime = TestUtil.nowLocalDateTime();
        entity.localDate = LocalDate.now();
        entity.offsetDateTime = TestUtil.nowOffsetDateTime();
        entity.date = new Date(2023, 6, 25);
        entity.timestamp = new Timestamp(System.currentTimeMillis());
        entity.byteArray = RandomUtils.nextBytes(10);
        entity.uuid = UUID.randomUUID();
        return entity;
    }

    @Override
    protected DirectAllTypesEntity newEntityWithId() {
        var entity = newEntity();
        entity.longId = nextId();
        return entity;
    }

    @Override
    protected DirectAllTypesEntity updateEntity(DirectAllTypesEntity entity) {
        var updated = entity.clone();
        updated.intObj = RandomUtils.nextInt();
        updated.intPrim = RandomUtils.nextInt();
        updated.shortObj = (short) 3;
        updated.shortPrim = (short) 4;
        updated.doubleObj = RandomUtils.nextDouble();
        updated.doublePrim = RandomUtils.nextDouble();
        updated.boolObj = false;
        updated.boolPrim = true;
        updated.string = RandomStringUtils.randomAlphanumeric(10);
        updated.instant = TestUtil.now();
        updated.localDateTime = TestUtil.nowLocalDateTime();
        updated.localDate = LocalDate.now();
        updated.offsetDateTime = TestUtil.nowOffsetDateTime();
        updated.date = new Date(2023, 6, 26);
        updated.timestamp = new Timestamp(System.currentTimeMillis());
        updated.byteArray = RandomUtils.nextBytes(10);
        updated.uuid = UUID.randomUUID();
        return updated;
    }

    @Override
    protected DirectAllTypesEntityDb.Patch patchAll(DirectAllTypesEntity entity) {
        return new DirectAllTypesEntityDb.Patch()
                .setLongPrim(entity.longPrim)
                .setIntObj(entity.intObj)
                .setIntPrim(entity.intPrim)
                .setShortObj(entity.shortObj)
                .setShortPrim(entity.shortPrim)
                .setDoubleObj(entity.doubleObj)
                .setDoublePrim(entity.doublePrim)
                .setBoolObj(entity.boolObj)
                .setBoolPrim(entity.boolPrim)
                .setString(entity.string)
                .setInstant(entity.instant)
                .setLocalDateTime(entity.localDateTime)
                .setLocalDate(entity.localDate)
                .setOffsetDateTime(entity.offsetDateTime)
                .setDate(entity.date)
                .setTimestamp(entity.timestamp)
                .setByteArray(entity.byteArray)
                .setUuid(entity.uuid);
    }

    @Override
    protected DirectAllTypesEntity nullEntity(DirectAllTypesEntity entity) {
        var updated = entity.clone();
        updated.intObj = null;
        updated.shortObj = null;
        updated.doubleObj = null;
        updated.boolObj = null;
        updated.string = null;
        updated.instant = null;
        updated.localDateTime = null;
        updated.localDate = null;
        updated.offsetDateTime = null;
        updated.date = null;
        updated.timestamp = null;
        updated.byteArray = null;
        updated.uuid = null;
        return updated;
    }

    @Override
    protected DirectAllTypesEntityDb.Patch nullPatchAll() {
        return new DirectAllTypesEntityDb.Patch()
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
    protected Pair<DirectAllTypesEntity, DirectAllTypesEntityDb.Patch> patchPartial(DirectAllTypesEntity entity) {
        var updated = entity.clone();
        updated.string = RandomStringUtils.randomAlphanumeric(15);
        updated.uuid = UUID.randomUUID();
        updated.instant = TestUtil.now();
        updated.date = null;

        return ImmutablePair.of(updated, new DirectAllTypesEntityDb.Patch()
                .setString(updated.string)
                .setUuid(updated.uuid)
                .setInstant(updated.instant)
                .setDate(updated.date));
    }

    @Override
    protected DirectAllTypesEntityDb.Patch addRequiredFields(DirectAllTypesEntity entity, DirectAllTypesEntityDb.Patch patch) {
        return patch.setBoolPrim(entity.boolPrim)
                .setDoublePrim(entity.doublePrim)
                .setIntPrim(entity.intPrim)
                .setShortPrim(entity.shortPrim)
                .setLongPrim(entity.longPrim);
    }

}
