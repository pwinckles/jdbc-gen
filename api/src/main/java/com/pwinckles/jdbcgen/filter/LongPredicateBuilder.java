package com.pwinckles.jdbcgen.filter;

// TODO javadoc
public class LongPredicateBuilder<B> extends CollectionPredicateBuilder<B, Long> {

    private final String field;
    private final Filter filter;
    private final FilterBuilderHelper<B> helper;

    /**
     * @param field the name of the column to filter on in the db
     * @param filter the filter to add the predicate to
     * @param helper the entity's filter builder helper
     */
    public LongPredicateBuilder(String field, Filter filter, FilterBuilderHelper<B> helper) {
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
    public ConjunctionBuilder<B> isEqualTo(long value) {
        filter.add(new LongPredicate(field, Operation.EQUAL, value));
        return helper.conjunctionBuilder();
    }

    /**
     * Adds a predicate that matches values that are not equal to the specified value
     *
     * @param value the value to compare
     * @return conjunction builder
     */
    public ConjunctionBuilder<B> isNotEqualTo(long value) {
        filter.add(new LongPredicate(field, Operation.NOT_EQUAL, value));
        return helper.conjunctionBuilder();
    }

    /**
     * Adds a predicate that matches values that are greater than the specified value
     *
     * @param value the value to compare
     * @return conjunction builder
     */
    public ConjunctionBuilder<B> isGreaterThan(long value) {
        filter.add(new LongPredicate(field, Operation.GREATER_THAN, value));
        return helper.conjunctionBuilder();
    }

    /**
     * Adds a predicate that matches values that are greater than or equal to the specified value
     *
     * @param value the value to compare
     * @return conjunction builder
     */
    public ConjunctionBuilder<B> isGreaterThanOrEqualTo(long value) {
        filter.add(new LongPredicate(field, Operation.GREATER_THAN_OR_EQUAL, value));
        return helper.conjunctionBuilder();
    }

    /**
     * Adds a predicate that matches values that are less than the specified value
     *
     * @param value the value to compare
     * @return conjunction builder
     */
    public ConjunctionBuilder<B> isLessThan(long value) {
        filter.add(new LongPredicate(field, Operation.LESS_THAN, value));
        return helper.conjunctionBuilder();
    }

    /**
     * Adds a predicate that matches values that are less than or equal to the specified value
     *
     * @param value the value to compare
     * @return conjunction builder
     */
    public ConjunctionBuilder<B> isLessThanOrEqualTo(long value) {
        filter.add(new LongPredicate(field, Operation.LESS_THAN_OR_EQUAL, value));
        return helper.conjunctionBuilder();
    }
}
