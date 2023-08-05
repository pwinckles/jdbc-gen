package com.pwinckles.jdbcgen.filter;

/**
 * A conjunction that joins two filter predicates.
 */
enum Conjunction implements FilterPart {
    AND,
    OR;

    /**
     * {@inheritDoc}
     */
    @Override
    public void buildQuery(StringBuilder queryBuilder) {
        queryBuilder.append(" ").append(this.name()).append(" ");
    }
}
