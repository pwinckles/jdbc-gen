package com.pwinckles.jdbcgen.filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

// TODO javadoc
class LongPredicate implements FilterPart {

    private final String field;
    private final Operation operation;
    private final long value;

    // TODO javadoc
    public LongPredicate(String field, Operation operation, long value) {
        this.field = Objects.requireNonNull(field, "field cannot be null");
        this.operation = Objects.requireNonNull(operation, "operation cannot be null");
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void buildQuery(StringBuilder queryBuilder) {
        queryBuilder.append(field).append(" ").append(operation.getSymbol()).append(" ?");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int addArguments(int currentPosition, PreparedStatement statement) throws SQLException {
        statement.setLong(currentPosition, value);
        return currentPosition + 1;
    }
}
