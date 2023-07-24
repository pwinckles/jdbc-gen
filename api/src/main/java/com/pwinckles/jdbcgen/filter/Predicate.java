package com.pwinckles.jdbcgen.filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

// TODO javadoc
class Predicate implements FilterPart {

    private final String field;
    private final Operation operation;
    private final Object value;

    // TODO javadoc
    public Predicate(String field, Operation operation, Object value) {
        this.field = Objects.requireNonNull(field, "field cannot be null");
        this.operation = Objects.requireNonNull(operation, "operation cannot be null");
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void buildQuery(StringBuilder queryBuilder) {
        if (operation == Operation.LIKE_INSENSITIVE || operation == Operation.NOT_LIKE_INSENSITIVE) {
            queryBuilder
                    .append("LOWER(")
                    .append(field)
                    .append(") ")
                    .append(operation.getSymbol())
                    .append(" LOWER(?)");
        } else {
            queryBuilder.append(field).append(" ");

            if (value == null) {
                if (operation == Operation.EQUAL) {
                    queryBuilder.append("IS NULL");
                } else if (operation == Operation.NOT_EQUAL) {
                    queryBuilder.append("IS NOT NULL");
                }
            } else if (value instanceof Boolean && operation == Operation.EQUAL) {
                if (Boolean.TRUE.equals(value)) {
                    queryBuilder.append("IS TRUE");
                } else {
                    queryBuilder.append("IS FALSE");
                }
            } else {
                queryBuilder.append(operation.getSymbol()).append(" ?");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int addArguments(int currentPosition, PreparedStatement statement) throws SQLException {
        if (value != null && !(value instanceof Boolean)) {
            statement.setObject(currentPosition, value);
            return currentPosition + 1;
        }
        return currentPosition;
    }
}
