package com.pwinckles.jdbcgen.filter;

// TODO javadoc
public class PrimitiveBooleanPredicateBuilder<B> {

    private final String field;
    private final Filter filter;
    private final FilterBuilderHelper<B> helper;

    /**
     * @param field the name of the column to filter on in the db
     * @param filter the filter to add the predicate to
     * @param helper the entity's filter builder helper
     */
    public PrimitiveBooleanPredicateBuilder(String field, Filter filter, FilterBuilderHelper<B> helper) {
        this.field = field;
        this.filter = filter;
        this.helper = helper;
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isTrue() {
        filter.add(new Predicate(field, Operation.EQUAL, true));
        return helper.conjunctionBuilder();
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isFalse() {
        filter.add(new Predicate(field, Operation.EQUAL, false));
        return helper.conjunctionBuilder();
    }
}
