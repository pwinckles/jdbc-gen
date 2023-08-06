package com.pwinckles.jdbcgen.filter;

/**
 * Constructs a predicate for an enum value.
 *
 * @param <B> the entity's filter builder type
 * @param <T> the enum value's type
 */
public class EnumPredicateBuilder<B, T extends Enum<T>> extends CollectionPredicateBuilder<B, T> {

    private final String field;
    private final Filter filter;
    private final FilterBuilderHelper<B> helper;

    /**
     * @param field the name of the column to filter on in the db
     * @param filter the filter to add the predicate to
     * @param helper the entity's filter builder helper
     */
    public EnumPredicateBuilder(String field, Filter filter, FilterBuilderHelper<B> helper) {
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
        filter.add(new Predicate(field, Operation.EQUAL, value == null ? null : value.name()));
        return helper.conjunctionBuilder();
    }

    /**
     * Adds a predicate that matches values that are not equal to the specified value
     *
     * @param value the value to compare
     * @return conjunction builder
     */
    public ConjunctionBuilder<B> isNotEqualTo(T value) {
        filter.add(new Predicate(field, Operation.NOT_EQUAL, value == null ? null : value.name()));
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
}
