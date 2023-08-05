package com.pwinckles.jdbcgen.filter;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Constructs a collection filter predicate.
 *
 * @param <B> the entity's filter builder type
 * @param <T> the type of the elements in the collection
 */
public class CollectionPredicateBuilder<B, T> {

    private final String field;
    private final Filter filter;
    private final FilterBuilderHelper<B> helper;

    /**
     * @param field the name of the column to filter on in the db
     * @param filter the filter to add the predicate to
     * @param helper the entity's filter builder helper
     */
    public CollectionPredicateBuilder(String field, Filter filter, FilterBuilderHelper<B> helper) {
        this.field = field;
        this.filter = filter;
        this.helper = helper;
    }

    /**
     * Adds a predicate that filters for values that match the values specified in the collection.
     *
     * @param values the values to match
     * @return conjunction builder
     */
    public ConjunctionBuilder<B> isIn(Collection<T> values) {
        Objects.requireNonNull(values, "values cannot be null");
        filter.add(InListPredicate.inList(field, List.copyOf(values)));
        return helper.conjunctionBuilder();
    }

    /**
     * Adds a predicate that filters for values that do not match the values specified in the collection.
     *
     * @param values the values to not match
     * @return conjunction builder
     */
    public ConjunctionBuilder<B> isNotIn(Collection<T> values) {
        Objects.requireNonNull(values, "values cannot be null");
        filter.add(InListPredicate.notInList(field, List.copyOf(values)));
        return helper.conjunctionBuilder();
    }
}
