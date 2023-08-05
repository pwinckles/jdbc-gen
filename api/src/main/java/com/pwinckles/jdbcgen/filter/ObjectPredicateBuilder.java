package com.pwinckles.jdbcgen.filter;

import java.util.Objects;

// TODO javadoc
public class ObjectPredicateBuilder<B, T> extends CollectionPredicateBuilder<B, T> {

    private final String field;
    private final Filter filter;
    private final FilterBuilderHelper<B> helper;

    /**
     * @param field the name of the column to filter on in the db
     * @param filter the filter to add the predicate to
     * @param helper the entity's filter builder helper
     */
    public ObjectPredicateBuilder(String field, Filter filter, FilterBuilderHelper<B> helper) {
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
    public ConjunctionBuilder<B> isEqualTo(T value) {
        filter.add(new Predicate(field, Operation.EQUAL, value));
        return helper.conjunctionBuilder();
    }

    /**
     * Adds a predicate that matches values that are not equal to the specified value
     *
     * @param value the value to compare
     * @return conjunction builder
     */
    public ConjunctionBuilder<B> isNotEqualTo(T value) {
        filter.add(new Predicate(field, Operation.NOT_EQUAL, value));
        return helper.conjunctionBuilder();
    }

    /**
     * Adds a predicate that matches values that are greater than the specified value
     *
     * @param value the value to compare
     * @return conjunction builder
     */
    public ConjunctionBuilder<B> isGreaterThan(T value) {
        Objects.requireNonNull(value, "value cannot be null");
        filter.add(new Predicate(field, Operation.GREATER_THAN, value));
        return helper.conjunctionBuilder();
    }

    /**
     * Adds a predicate that matches values that are greater than or equal to the specified value
     *
     * @param value the value to compare
     * @return conjunction builder
     */
    public ConjunctionBuilder<B> isGreaterThanOrEqualTo(T value) {
        Objects.requireNonNull(value, "value cannot be null");
        filter.add(new Predicate(field, Operation.GREATER_THAN_OR_EQUAL, value));
        return helper.conjunctionBuilder();
    }

    /**
     * Adds a predicate that matches values that are less than the specified value
     *
     * @param value the value to compare
     * @return conjunction builder
     */
    public ConjunctionBuilder<B> isLessThan(T value) {
        Objects.requireNonNull(value, "value cannot be null");
        filter.add(new Predicate(field, Operation.LESS_THAN, value));
        return helper.conjunctionBuilder();
    }

    /**
     * Adds a predicate that matches values that are less than or equal to the specified value
     *
     * @param value the value to compare
     * @return conjunction builder
     */
    public ConjunctionBuilder<B> isLessThanOrEqualTo(T value) {
        Objects.requireNonNull(value, "value cannot be null");
        filter.add(new Predicate(field, Operation.LESS_THAN_OR_EQUAL, value));
        return helper.conjunctionBuilder();
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isNull() {
        return isEqualTo(null);
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isNotNull() {
        return isNotEqualTo(null);
    }
}
