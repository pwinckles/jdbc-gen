package com.pwinckles.jdbcgen.test.prototype;

import com.pwinckles.jdbcgen.BasePatch;
import com.pwinckles.jdbcgen.JdbcGenDb;
import com.pwinckles.jdbcgen.SelectBuilder;
import com.pwinckles.jdbcgen.filter.Filter;
import com.pwinckles.jdbcgen.sort.Sort;
import com.pwinckles.jdbcgen.test.ExampleEnum;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ExampleDb implements JdbcGenDb<Example, Long, ExampleDb.Patch, ExampleFilterBuilder, ExampleSortBuilder> {

    public static class Patch extends BasePatch {

        public Long getId() {
            return (Long) getData().get("id");
        }

        public Patch setId(Long id) {
            put("id", id);
            return this;
        }

        public Patch setName(String name) {
            put("name", name);
            return this;
        }

        public Patch setCount(long count) {
            put("count", count);
            return this;
        }

        public Patch setTimestamp(Instant timestamp) {
            put("timestamp", timestamp);
            return this;
        }

        public Patch setExampleEnum(ExampleEnum exampleEnum) {
            put("enum", exampleEnum);
            return this;
        }
    }

    /**
     * Creates a new patch object to use for partially updating an entity.
     *
     * @return new patch
     */
    public static Patch patch() {
        return new Patch();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Example select(Long id, Connection conn) throws SQLException {
        try (var stmt =
                conn.prepareStatement("SELECT id, name, count, timestamp, enum FROM example WHERE id = ? LIMIT 1")) {
            stmt.setObject(1, id);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return fromResultSet(rs);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Example> selectAll(Connection conn) throws SQLException {
        var results = new ArrayList<Example>();

        try (var stmt = conn.createStatement()) {
            var rs = stmt.executeQuery("SELECT id, name, count, timestamp, enum FROM example");
            while (rs.next()) {
                results.add(fromResultSet(rs));
            }
        }

        return results;
    }

    @Override
    public List<Example> select(
            Consumer<SelectBuilder<ExampleFilterBuilder, ExampleSortBuilder>> selectBuilder, Connection conn)
            throws SQLException {
        var results = new ArrayList<Example>();

        var filter = new Filter();
        var sort = new Sort();
        var paginate = new SelectBuilder.Paginate();

        selectBuilder.accept(
                new SelectBuilder<>(new ExampleFilterBuilder(filter), new ExampleSortBuilder(sort), paginate));

        var queryBuilder = new StringBuilder("SELECT id, name, count, timestamp, enum FROM example");
        filter.buildQuery(queryBuilder);
        sort.buildQuery(queryBuilder);
        paginate.buildQuery(queryBuilder);

        try (var stmt = conn.prepareStatement(queryBuilder.toString())) {
            filter.addArguments(1, stmt);
            var rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(fromResultSet(rs));
            }
        }

        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count(Connection conn) throws SQLException {
        try (var stmt = conn.createStatement()) {
            var rs = stmt.executeQuery("SELECT COUNT(id) FROM example");
            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        return 0;
    }

    @Override
    public long count(Consumer<ExampleFilterBuilder> filterBuilder, Connection conn) throws SQLException {
        var filter = new Filter();
        filterBuilder.accept(new ExampleFilterBuilder(filter));

        var queryBuilder = new StringBuilder("SELECT COUNT(id) FROM example");
        filter.buildQuery(queryBuilder);

        try (var stmt = conn.prepareStatement(queryBuilder.toString())) {
            filter.addArguments(1, stmt);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
        }

        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long insert(Example entity, Connection conn) throws SQLException {
        if (entity.getId() == null) {
            return insertWithGeneratedId(entity, conn);
        }
        return insertWithSpecifiedId(entity, conn);
    }

    private Long insertWithGeneratedId(Example entity, Connection conn) throws SQLException {
        try (var stmt = conn.prepareStatement(
                "INSERT INTO example (name, count, timestamp, enum) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            prepareInsert(entity, stmt);
            stmt.executeUpdate();
            var rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getLong(1);
            } else {
                throw new SQLException("Generated id was not returned.");
            }
        }
    }

    private Long insertWithSpecifiedId(Example entity, Connection conn) throws SQLException {
        try (var stmt = conn.prepareStatement(
                "INSERT INTO example (id, name, count, timestamp, enum) VALUES (?, ?, ?, ?, ?)")) {
            prepareInsert(entity, stmt);
            stmt.executeUpdate();
        }
        return entity.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long insert(Patch entity, Connection conn) throws SQLException {
        if (entity.getData().isEmpty()) {
            throw new SQLException("No data specified");
        }

        boolean generatedId = entity.getId() == null;
        var data = entity.getData();
        var keys = new ArrayList<>(data.keySet());

        var queryBuilder = new StringBuilder("INSERT INTO example (");

        for (var it = keys.iterator(); it.hasNext(); ) {
            queryBuilder.append(it.next());
            if (it.hasNext()) {
                queryBuilder.append(", ");
            }
        }

        queryBuilder.append(") VALUES (");

        if (keys.size() > 1) {
            queryBuilder.append("?, ".repeat(keys.size() - 1));
        }
        queryBuilder.append("?)");

        PreparedStatement stmt;
        if (generatedId) {
            stmt = conn.prepareStatement(queryBuilder.toString(), Statement.RETURN_GENERATED_KEYS);
        } else {
            stmt = conn.prepareStatement(queryBuilder.toString());
        }

        try {
            for (int i = 0; i < keys.size(); i++) {
                var value = data.get(keys.get(i));
                stmt.setObject(i + 1, value);
            }

            stmt.executeUpdate();

            if (generatedId) {
                var rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getLong(1);
                } else {
                    throw new SQLException("Generated id was not returned.");
                }
            } else {
                return entity.getId();
            }
        } finally {
            stmt.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> insert(List<Example> entities, Connection conn) throws SQLException {
        if (!entities.isEmpty() && entities.get(0).getId() == null) {
            return insertWithGeneratedId(entities, conn);
        }
        return insertWithSpecifiedId(entities, conn);
    }

    private List<Long> insertWithGeneratedId(List<Example> entities, Connection conn) throws SQLException {
        var ids = new ArrayList<Long>();

        try (var stmt = conn.prepareStatement(
                "INSERT INTO example (name, count, timestamp, enum) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            for (var entity : entities) {
                prepareInsert(entity, stmt);
                stmt.addBatch();
            }

            stmt.executeBatch();

            var rs = stmt.getGeneratedKeys();
            while (rs.next()) {
                ids.add(rs.getLong(1));
            }
        }

        return ids;
    }

    private List<Long> insertWithSpecifiedId(List<Example> entities, Connection conn) throws SQLException {
        try (var stmt = conn.prepareStatement(
                "INSERT INTO example (id, name, count, timestamp, enum) VALUES (?, ?, ?, ?, ?)")) {
            for (var entity : entities) {
                prepareInsert(entity, stmt);
                stmt.addBatch();
            }

            stmt.executeBatch();
        }

        return entities.stream().map(Example::getId).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int update(Example entity, Connection conn) throws SQLException {
        try (var stmt =
                conn.prepareStatement("UPDATE example SET name = ?, count = ?, timestamp = ?, enum = ? WHERE id = ?")) {
            prepareUpdate(entity, stmt);
            return stmt.executeUpdate();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int update(Long id, Patch entity, Connection conn) throws SQLException {
        if (entity.getData().isEmpty()) {
            throw new SQLException("No data specified");
        }

        var data = entity.getData();
        var keys = new ArrayList<>(data.keySet());

        var queryBuilder = new StringBuilder("UPDATE example SET ");

        for (var it = keys.iterator(); it.hasNext(); ) {
            queryBuilder.append(it.next()).append(" = ?");
            if (it.hasNext()) {
                queryBuilder.append(", ");
            }
        }

        queryBuilder.append(" WHERE id = ?");

        try (var stmt = conn.prepareStatement(queryBuilder.toString())) {
            for (int i = 0; i < keys.size(); i++) {
                var value = data.get(keys.get(i));
                stmt.setObject(i + 1, value);
            }
            stmt.setObject(keys.size() + 1, id);

            return stmt.executeUpdate();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] update(List<Example> entities, Connection conn) throws SQLException {
        try (var stmt =
                conn.prepareStatement("UPDATE example SET name = ?, count = ?, timestamp = ?, enum = ? WHERE id = ?")) {
            for (var entity : entities) {
                prepareUpdate(entity, stmt);
                stmt.addBatch();
            }

            return stmt.executeBatch();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int delete(Long id, Connection conn) throws SQLException {
        try (var stmt = conn.prepareStatement("DELETE FROM example WHERE id = ?")) {
            stmt.setObject(1, id);
            return stmt.executeUpdate();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] delete(List<Long> ids, Connection conn) throws SQLException {
        try (var stmt = conn.prepareStatement("DELETE FROM example WHERE id = ?")) {
            for (var id : ids) {
                stmt.setObject(1, id);
                stmt.addBatch();
            }
            return stmt.executeBatch();
        }
    }

    @Override
    public int delete(Consumer<ExampleFilterBuilder> filterBuilder, Connection conn) throws SQLException {
        var filter = new Filter();
        filterBuilder.accept(new ExampleFilterBuilder(filter));

        var queryBuilder = new StringBuilder("DELETE FROM example");
        filter.buildQuery(queryBuilder);

        try (var stmt = conn.prepareStatement(queryBuilder.toString())) {
            filter.addArguments(1, stmt);
            return stmt.executeUpdate();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int deleteAll(Connection conn) throws SQLException {
        try (var stmt = conn.createStatement()) {
            return stmt.executeUpdate("DELETE FROM example");
        }
    }

    private Example fromResultSet(ResultSet rs) throws SQLException {
        int i = 1;
        var entity = new Example();
        entity.setId(getNullableValue(rs, i++, Long.class));
        entity.setName(getNullableValue(rs, i++, String.class));
        entity.setCount(rs.getLong(i++));
        entity.setTimestamp(getNullableValue(rs, i++, Instant.class));
        entity.setExampleEnum(enumFromResultSet(rs, i++, ExampleEnum.class));
        return entity;
    }

    private <T> T getNullableValue(ResultSet rs, int index, Class<T> clazz) throws SQLException {
        var value = rs.getObject(index, clazz);
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

    private void prepareInsert(Example entity, PreparedStatement stmt) throws SQLException {
        int i = 1;
        if (entity.getId() != null) {
            stmt.setObject(i++, entity.getId());
        }
        stmt.setObject(i++, entity.getName());
        stmt.setObject(i++, entity.getCount());
        stmt.setObject(i++, entity.getTimestamp());
        stmt.setObject(i++, enumToString(entity.getExampleEnum()));
    }

    private void prepareUpdate(Example entity, PreparedStatement stmt) throws SQLException {
        int i = 1;
        stmt.setObject(i++, entity.getName());
        stmt.setObject(i++, entity.getCount());
        stmt.setObject(i++, entity.getTimestamp());
        stmt.setObject(i++, enumToString(entity.getExampleEnum()));
        stmt.setObject(i++, entity.getId());
    }

    private String enumToString(Enum<?> e) {
        if (e == null) {
            return null;
        }
        return e.name();
    }

    private <T extends Enum<T>> T enumFromResultSet(ResultSet rs, int index, Class<T> clazz) throws SQLException {
        var value = rs.getObject(index, String.class);
        if (rs.wasNull()) {
            return null;
        }
        return Enum.valueOf(clazz, value);
    }
}
