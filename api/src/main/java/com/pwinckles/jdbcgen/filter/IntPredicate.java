package com.pwinckles.jdbcgen.filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

class IntPredicate implements FilterPart {

    private final String field;
    private final Operation operation;
    private final int value;

    public IntPredicate(String field, Operation operation, int value) {
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
        statement.setInt(currentPosition, value);
        return currentPosition + 1;
    }
}
