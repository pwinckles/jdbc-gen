package com.pwinckles.jdbcgen.filter;

// TODO javadoc
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
