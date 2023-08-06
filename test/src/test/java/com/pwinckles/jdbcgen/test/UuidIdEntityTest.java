package com.pwinckles.jdbcgen.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.UUID;
import java.util.stream.Stream;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class UuidIdEntityTest
        extends TestBase<
                UuidIdEntity, UUID, UuidIdEntityDb.Patch, UuidIdEntityDb.FilterBuilder, UuidIdEntityDb.SortBuilder> {

    // Need to do this so the uuids are ordered
    private final Iterator<UUID> uuids = Stream.generate(UUID::randomUUID)
            .limit(50)
            .sorted(Comparator.comparing(UUID::toString))
            .iterator();

    protected UuidIdEntityTest() {
        super(new UuidIdEntityDb());
    }

    @Override
    protected UUID getId(UuidIdEntity entity) {
        return entity.getId();
    }

    @Override
    protected UuidIdEntity setId(UUID id, UuidIdEntity entity) {
        return entity.clone().setId(id);
    }

    @Override
    protected UuidIdEntity newEntity() {
        return new UuidIdEntity().setId(uuids.next()).setValue(RandomStringUtils.randomAlphanumeric(10));
    }

    @Override
    protected UuidIdEntity newEntityWithId() {
        return newEntity();
    }

    @Override
    protected UuidIdEntity updateEntity(UuidIdEntity entity) {
        return entity.clone().setValue(RandomStringUtils.randomAlphanumeric(10));
    }

    @Override
    protected UuidIdEntityDb.Patch patchAll(UuidIdEntity entity) {
        return new UuidIdEntityDb.Patch().setValue(entity.getValue());
    }

    @Override
    protected UuidIdEntity nullEntity(UuidIdEntity entity) {
        return entity.clone().setValue(null);
    }

    @Override
    protected UuidIdEntityDb.Patch nullPatchAll() {
        return new UuidIdEntityDb.Patch().setValue(null);
    }

    @Override
    protected Pair<UuidIdEntity, UuidIdEntityDb.Patch> patchPartial(UuidIdEntity entity) {
        var updated = entity.clone().setValue(RandomStringUtils.randomAlphanumeric(15));

        return ImmutablePair.of(updated, new UuidIdEntityDb.Patch().setValue(updated.getValue()));
    }

    @Override
    protected UuidIdEntityDb.Patch addRequiredFields(UuidIdEntity entity, UuidIdEntityDb.Patch patch) {
        return patch.setId(entity.getId());
    }

    @Override
    protected void createTable(Connection conn) throws SQLException {
        try (var stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE uuid_id (" + "id UUID PRIMARY KEY," + " val VARCHAR(255)" + ")");
        }
    }
}
