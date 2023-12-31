package com.pwinckles.jdbcgen.filter;

import com.pwinckles.jdbcgen.JdbcGenUtil;
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
        filter.add(new Predicate(field, Operation.EQUAL, JdbcGenUtil.enumToString(value)));
        return helper.conjunctionBuilder();
    }

    /**
     * Adds a predicate that matches values that are not equal to the specified value
     *
     * @param value the value to compare
     * @return conjunction builder
     */
    public ConjunctionBuilder<B> isNotEqualTo(T value) {
        filter.add(new Predicate(field, Operation.NOT_EQUAL, JdbcGenUtil.enumToString(value)));
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
        filter.add(new Predicate(field, Operation.GREATER_THAN, value.name()));
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
        filter.add(new Predicate(field, Operation.GREATER_THAN_OR_EQUAL, value.name()));
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
        filter.add(new Predicate(field, Operation.LESS_THAN, value.name()));
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
        filter.add(new Predicate(field, Operation.LESS_THAN_OR_EQUAL, value.name()));
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
                field, values.stream().map(JdbcGenUtil::enumToString).collect(Collectors.toList())));
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
                field, values.stream().map(JdbcGenUtil::enumToString).collect(Collectors.toList())));
        return helper.conjunctionBuilder();
    }
}
