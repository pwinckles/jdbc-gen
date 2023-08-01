package com.pwinckles.jdbcgen.filter;

// TODO javadoc
public class LongPredicateBuilder<B> extends CollectionPredicateBuilder<B, Long> {

    private final String field;
    private final Filter filter;
    private final FilterBuilderHelper<B> helper;

    // TODO javadoc
    public LongPredicateBuilder(String field, Filter filter, FilterBuilderHelper<B> helper) {
        super(field, filter, helper);
        this.field = field;
        this.filter = filter;
        this.helper = helper;
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isEqualTo(long value) {
        filter.add(new LongPredicate(field, Operation.EQUAL, value));
        return helper.conjunctionBuilder();
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isNotEqualTo(long value) {
        filter.add(new LongPredicate(field, Operation.NOT_EQUAL, value));
        return helper.conjunctionBuilder();
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isGreaterThan(long value) {
        filter.add(new LongPredicate(field, Operation.GREATER_THAN, value));
        return helper.conjunctionBuilder();
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isGreaterThanOrEqualTo(long value) {
        filter.add(new LongPredicate(field, Operation.GREATER_THAN_OR_EQUAL, value));
        return helper.conjunctionBuilder();
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isLessThan(long value) {
        filter.add(new LongPredicate(field, Operation.LESS_THAN, value));
        return helper.conjunctionBuilder();
    }

    // TODO javadoc
    public ConjunctionBuilder<B> isLessThanOrEqualTo(long value) {
        filter.add(new LongPredicate(field, Operation.LESS_THAN_OR_EQUAL, value));
        return helper.conjunctionBuilder();
    }
}
