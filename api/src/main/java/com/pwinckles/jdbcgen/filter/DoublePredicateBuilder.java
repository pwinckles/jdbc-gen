package com.pwinckles.jdbcgen.filter;

// TODO javadoc
public class DoublePredicateBuilder<B> extends CollectionPredicateBuilder<B, Double> {

    private final String field;
    private final Filter filter;
    private final FilterBuilderHelper<B> helper;

    // TODO javadoc
    public DoublePredicateBuilder(String field, Filter filter, FilterBuilderHelper<B> helper) {
        super(field, filter, helper);
        this.field = field;
        this.filter = filter;
        this.helper = helper;
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isEqualTo(double value) {
        filter.add(new DoublePredicate(field, Operation.EQUAL, value));
        return helper.conjunctionBuilder();
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isNotEqualTo(double value) {
        filter.add(new DoublePredicate(field, Operation.NOT_EQUAL, value));
        return helper.conjunctionBuilder();
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isGreaterThan(double value) {
        filter.add(new DoublePredicate(field, Operation.GREATER_THAN, value));
        return helper.conjunctionBuilder();
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isGreaterThanOrEqualTo(double value) {
        filter.add(new DoublePredicate(field, Operation.GREATER_THAN_OR_EQUAL, value));
        return helper.conjunctionBuilder();
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isLessThan(double value) {
        filter.add(new DoublePredicate(field, Operation.LESS_THAN, value));
        return helper.conjunctionBuilder();
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isLessThanOrEqualTo(double value) {
        filter.add(new DoublePredicate(field, Operation.LESS_THAN_OR_EQUAL, value));
        return helper.conjunctionBuilder();
    }
}
