package com.pwinckles.jdbcgen.filter;

/**
 * Constructs a predicate for a primitive double value.
 *
 * @param <B> the entity's filter builder type
 */
public class DoublePredicateBuilder<B> extends CollectionPredicateBuilder<B, Double> {

    private final String field;
    private final Filter filter;
    private final FilterBuilderHelper<B> helper;

    /**
     * @param field the name of the column to filter on in the db
     * @param filter the filter to add the predicate to
     * @param helper the entity's filter builder helper
     */
    public DoublePredicateBuilder(String field, Filter filter, FilterBuilderHelper<B> helper) {
        super(field, filter, helper);
        this.field = field;
        this.filter = filter;
        this.helper = helper;
    }

    /**
     * Adds a predicate that matches values that are equal to the specified value
     *
     * @param value the value to compare
     * @return conjunction builder
     */
    public ConjunctionBuilder<B> isEqualTo(double value) {
        filter.add(new DoublePredicate(field, Operation.EQUAL, value));
        return helper.conjunctionBuilder();
    }

    /**
     * Adds a predicate that matches values that are not equal to the specified value
     *
     * @param value the value to compare
     * @return conjunction builder
     */
    public ConjunctionBuilder<B> isNotEqualTo(double value) {
        filter.add(new DoublePredicate(field, Operation.NOT_EQUAL, value));
        return helper.conjunctionBuilder();
    }

    /**
     * Adds a predicate that matches values that are greater than the specified value
     *
     * @param value the value to compare
     * @return conjunction builder
     */
    public ConjunctionBuilder<B> isGreaterThan(double value) {
        filter.add(new DoublePredicate(field, Operation.GREATER_THAN, value));
        return helper.conjunctionBuilder();
    }

    /**
     * Adds a predicate that matches values that are greater than or equal to the specified value
     *
     * @param value the value to compare
     * @return conjunction builder
     */
    public ConjunctionBuilder<B> isGreaterThanOrEqualTo(double value) {
        filter.add(new DoublePredicate(field, Operation.GREATER_THAN_OR_EQUAL, value));
        return helper.conjunctionBuilder();
    }

    /**
     * Adds a predicate that matches values that are less than the specified value
     *
     * @param value the value to compare
     * @return conjunction builder
     */
    public ConjunctionBuilder<B> isLessThan(double value) {
        filter.add(new DoublePredicate(field, Operation.LESS_THAN, value));
        return helper.conjunctionBuilder();
    }

    /**
     * Adds a predicate that matches values that are less than or equal to the specified value
     *
     * @param value the value to compare
     * @return conjunction builder
     */
    public ConjunctionBuilder<B> isLessThanOrEqualTo(double value) {
        filter.add(new DoublePredicate(field, Operation.LESS_THAN_OR_EQUAL, value));
        return helper.conjunctionBuilder();
    }
}
