package com.pwinckles.jdbcgen.filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

// TODO javadoc
class ShortPredicate implements FilterPart {

    private final String field;
    private final Operation operation;
    private final short value;

    // TODO javadoc
    public ShortPredicate(String field, Operation operation, short value) {
        this.field = field;
        this.operation = operation;
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
        statement.setShort(currentPosition, value);
        return currentPosition + 1;
    }
}
