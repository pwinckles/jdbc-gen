package com.pwinckles.jdbcgen.test;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class PrimitiveIdEntityTest
        extends TestBase<
                PrimitiveIdEntity,
                Long,
                PrimitiveIdEntityDb.Patch,
                PrimitiveIdEntityDb.FilterBuilder,
                PrimitiveIdEntityDb.SortBuilder> {

    protected PrimitiveIdEntityTest() {
        super(new PrimitiveIdEntityDb());
    }

    @Override
    protected Long getId(PrimitiveIdEntity entity) {
        return entity.getId();
    }

    @Override
    protected PrimitiveIdEntity setId(Long id, PrimitiveIdEntity entity) {
        return entity.clone().setId(id);
    }

    @Override
    protected PrimitiveIdEntity newEntity() {
        return new PrimitiveIdEntity().setId(nextId()).setValue(RandomStringUtils.randomAlphanumeric(10));
    }

    @Override
    protected PrimitiveIdEntity newEntityWithId() {
        return newEntity();
    }

    @Override
    protected PrimitiveIdEntity updateEntity(PrimitiveIdEntity entity) {
        return entity.clone().setValue(RandomStringUtils.randomAlphanumeric(10));
    }

    @Override
    protected PrimitiveIdEntityDb.Patch patchAll(PrimitiveIdEntity entity) {
        return new PrimitiveIdEntityDb.Patch().setValue(entity.getValue());
    }

    @Override
    protected PrimitiveIdEntity nullEntity(PrimitiveIdEntity entity) {
        return entity.clone().setValue(null);
    }

    @Override
    protected PrimitiveIdEntityDb.Patch nullPatchAll() {
        return new PrimitiveIdEntityDb.Patch().setValue(null);
    }

    @Override
    protected Pair<PrimitiveIdEntity, PrimitiveIdEntityDb.Patch> patchPartial(PrimitiveIdEntity entity) {
        var updated = entity.clone().setValue(RandomStringUtils.randomAlphanumeric(15));

        return ImmutablePair.of(updated, new PrimitiveIdEntityDb.Patch().setValue(updated.getValue()));
    }

    @Override
    protected PrimitiveIdEntityDb.Patch addRequiredFields(PrimitiveIdEntity entity, PrimitiveIdEntityDb.Patch patch) {
        return patch.setId(entity.getId());
    }

    @Override
    protected void createTable(Connection conn) throws SQLException {
        try (var stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE primitive_id (" + "id IDENTITY PRIMARY KEY," + " val VARCHAR(255)" + ")");
        }
    }
}
