package com.pwinckles.jdbcgen.filter;

/**
 * Constructs a predicate for a primitive boolean value.
 *
 * @param <B> the entity's filter builder type
 */
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

    /**
     * Adds a predicate that filters for true values
     *
     * @return conjunction builder
     */
    public ConjunctionBuilder<B> isTrue() {
        filter.add(new Predicate(field, Operation.EQUAL, true));
        return helper.conjunctionBuilder();
    }

    /**
     * Adds a predicate that filters for false values
     *
     * @return conjunction builder
     */
    public ConjunctionBuilder<B> isFalse() {
        filter.add(new Predicate(field, Operation.EQUAL, false));
        return helper.conjunctionBuilder();
    }
}
