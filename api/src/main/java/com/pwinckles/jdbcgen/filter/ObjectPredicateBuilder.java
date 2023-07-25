package com.pwinckles.jdbcgen.filter;

import java.util.Objects;

// TODO javadoc
public class ObjectPredicateBuilder<B, T> extends CollectionPredicateBuilder<B, T> {

    private final String field;
    private final Filter filter;
    private final FilterBuilderHelper<B> helper;

    // TODO javadoc
    public ObjectPredicateBuilder(String field, Filter filter, FilterBuilderHelper<B> helper) {
        super(field, filter, helper);
        this.field = field;
        this.filter = filter;
        this.helper = helper;
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isEqualTo(T value) {
        filter.add(new Predicate(field, Operation.EQUAL, value));
        return helper.conjunctionBuilder();
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isNotEqualTo(T value) {
        filter.add(new Predicate(field, Operation.NOT_EQUAL, value));
        return helper.conjunctionBuilder();
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isGreaterThan(T value) {
        Objects.requireNonNull(value, "value cannot be null");
        filter.add(new Predicate(field, Operation.GREATER_THAN, value));
        return helper.conjunctionBuilder();
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isGreaterThanOrEqualTo(T value) {
        Objects.requireNonNull(value, "value cannot be null");
        filter.add(new Predicate(field, Operation.GREATER_THAN_OR_EQUAL, value));
        return helper.conjunctionBuilder();
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isLessThan(T value) {
        Objects.requireNonNull(value, "value cannot be null");
        filter.add(new Predicate(field, Operation.LESS_THAN, value));
        return helper.conjunctionBuilder();
    }

    // TODO javadoc
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
