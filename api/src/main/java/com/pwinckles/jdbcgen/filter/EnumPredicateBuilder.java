package com.pwinckles.jdbcgen.filter;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Constructs a predicate for an enum value.
 *
 * @param <B> the entity's filter builder type
 * @param <T> the enum value's type
 */
public class EnumPredicateBuilder<B, T extends Enum<T>> {

    private final String field;
    private final Filter filter;
    private final FilterBuilderHelper<B> helper;

    /**
     * @param field the name of the column to filter on in the db
     * @param filter the filter to add the predicate to
     * @param helper the entity's filter builder helper
     */
    public EnumPredicateBuilder(String field, Filter filter, FilterBuilderHelper<B> helper) {
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
        filter.add(new Predicate(field, Operation.EQUAL, toStringValue(value)));
        return helper.conjunctionBuilder();
    }

    /**
     * Adds a predicate that matches values that are not equal to the specified value
     *
     * @param value the value to compare
     * @return conjunction builder
     */
    public ConjunctionBuilder<B> isNotEqualTo(T value) {
        filter.add(new Predicate(field, Operation.NOT_EQUAL, toStringValue(value)));
        return helper.conjunctionBuilder();
    }

    /**
     * Adds a predicate that matches values that are null.
     *
     * @return conjunction builder
     */
    public ConjunctionBuilder<B> isNull() {
        return isEqualTo(null);
    }

    /**
     * Adds a predicate that matches values that are not null.
     *
     * @return conjunction builder
     */
    public ConjunctionBuilder<B> isNotNull() {
        return isNotEqualTo(null);
    }

    /**
     * Adds a predicate that filters for values that match the values specified in the collection.
     *
     * @param values the values to match
     * @return conjunction builder
     */
    public ConjunctionBuilder<B> isIn(Collection<T> values) {
        Objects.requireNonNull(values, "values cannot be null");
        filter.add(InListPredicate.inList(
                field, values.stream().map(this::toStringValue).collect(Collectors.toList())));
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
        filter.add(InListPredicate.notInList(
                field, values.stream().map(this::toStringValue).collect(Collectors.toList())));
        return helper.conjunctionBuilder();
    }

    private String toStringValue(T value) {
        return value == null ? null : value.name();
    }
}
