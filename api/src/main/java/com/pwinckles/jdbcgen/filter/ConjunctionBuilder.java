package com.pwinckles.jdbcgen.filter;

/**
 * Adds an AND or an OR to the filter so that an additional predicate may be added.
 *
 * @param <B> the entity's filter builder type
 */
public class ConjunctionBuilder<B> {

    private final Filter filter;
    private final FilterBuilderHelper<B> helper;

    /**
     * @param filter the filter to add the predicate to
     * @param helper the entity's filter builder helper
     */
    public ConjunctionBuilder(Filter filter, FilterBuilderHelper<B> helper) {
        this.filter = filter;
        this.helper = helper;
    }

    /**
     * Joins the previous predicate to the subsequent predicate with an AND
     *
     * @return entity filter builder
     */
    public B and() {
        filter.add(Conjunction.AND);
        return helper.filterBuilder();
    }

    /**
     * Joins the previous predicate to the subsequent predicate with an OR
     *
     * @return entity filter builder
     */
    public B or() {
        filter.add(Conjunction.OR);
        return helper.filterBuilder();
    }
}
