package com.pwinckles.jdbcgen.filter;

import java.util.Objects;

/**
 * Constructs a predicate for a String value.
 *
 * @param <B> the entity's filter builder type
 */
public class StringPredicateBuilder<B> extends ObjectPredicateBuilder<B, String> {

    private final String field;
    private final Filter filter;
    private final FilterBuilderHelper<B> helper;

    /**
     * @param field the name of the column to filter on in the db
     * @param filter the filter to add the predicate to
     * @param helper the entity's filter builder helper
     */
    public StringPredicateBuilder(String field, Filter filter, FilterBuilderHelper<B> helper) {
        super(field, filter, helper);
        this.field = field;
        this.filter = filter;
        this.helper = helper;
    }

    /**
     * Adds a predicate that matches values using a SQL 'LIKE' comparison. Use '%' as a wildcard.
     *
     * @param value the value to compare
     * @return conjunction builder
     */
    public ConjunctionBuilder<B> isLike(String value) {
        Objects.requireNonNull(value, "value cannot be null");
        filter.add(new Predicate(field, Operation.LIKE, value));
        return helper.conjunctionBuilder();
    }

    /**
     * Adds a predicate that matches values using a SQL 'NOT LIKE' comparison. Use '%' as a wildcard.
     *
     * @param value the value to compare
     * @return conjunction builder
     */
    public ConjunctionBuilder<B> isNotLike(String value) {
        Objects.requireNonNull(value, "value cannot be null");
        filter.add(new Predicate(field, Operation.NOT_LIKE, value));
        return helper.conjunctionBuilder();
    }

    /**
     * Adds a predicate that matches values using a SQL 'LIKE' comparison, ignoring case. Use a '%' as a wildcard.
     *
     * @param value the value to compare
     * @return conjunction builder
     */
    public ConjunctionBuilder<B> isLikeInsensitive(String value) {
        Objects.requireNonNull(value, "value cannot be null");
        filter.add(new Predicate(field, Operation.LIKE_INSENSITIVE, value));
        return helper.conjunctionBuilder();
    }

    /**
     * Adds a predicate that matches values using a SQL 'NOT LIKE' comparison, ignoring case. Use a '%' as a wildcard.
     *
     * @param value the value to compare
     * @return conjunction builder
     */
    public ConjunctionBuilder<B> isNotLikeInsensitive(String value) {
        Objects.requireNonNull(value, "value cannot be null");
        filter.add(new Predicate(field, Operation.NOT_LIKE_INSENSITIVE, value));
        return helper.conjunctionBuilder();
    }

    /**
     * Adds a predicate that does a case-insensitive equality comparison.
     *
     * @param value the value to compare
     * @return conjunction builder
     */
    public ConjunctionBuilder<B> isEqualToInsensitive(String value) {
        Objects.requireNonNull(value, "value cannot be null");
        filter.add(new Predicate(field, Operation.EQUAL_INSENSITIVE, value));
        return helper.conjunctionBuilder();
    }

    /**
     * Adds a predicate that does a case-insensitive, not equals comparison.
     *
     * @param value the value to compare
     * @return conjunction builder
     */
    public ConjunctionBuilder<B> isNotEqualToInsensitive(String value) {
        Objects.requireNonNull(value, "value cannot be null");
        filter.add(new Predicate(field, Operation.NOT_EQUAL_INSENSITIVE, value));
        return helper.conjunctionBuilder();
    }
}
