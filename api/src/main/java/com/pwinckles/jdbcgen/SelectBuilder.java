package com.pwinckles.jdbcgen;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Constructs a select query. By default, selects will select all entities with no defined order.
 *
 * @param <F> the filter builder type
 * @param <S> the sort builder type
 */
public final class SelectBuilder<F, S> {

    private final F filterBuilder;
    private final S sortBuilder;
    private final Paginate paginate;

    public SelectBuilder(F filterBuilder, S sortBuilder, Paginate paginate) {
        this.filterBuilder = Objects.requireNonNull(filterBuilder, "filterBuilder cannot be null");
        this.sortBuilder = Objects.requireNonNull(sortBuilder, "sortBuilder cannot be null");
        this.paginate = Objects.requireNonNull(paginate, "paginate cannot be null");
    }

    /**
     * Adds a filter to the select.
     *
     * @param filterBuilder the filter builder
     * @return select builder
     */
    public SelectBuilder<F, S> filter(Consumer<F> filterBuilder) {
        filterBuilder.accept(this.filterBuilder);
        return this;
    }

    /**
     * Adds a sort order to the select.
     *
     * @param sortBuilder the sort builder
     * @return select builder
     */
    public SelectBuilder<F, S> sort(Consumer<S> sortBuilder) {
        sortBuilder.accept(this.sortBuilder);
        return this;
    }

    /**
     * Adds a limit and offset to the select
     *
     * @param pageNum the page number to select, >= 0
     * @param pageSize the size of the page, > 0
     * @return select builder
     */
    public SelectBuilder<F, S> paginate(long pageNum, long pageSize) {
        paginate.set(pageNum, pageSize);
        return this;
    }

    /**
     * Constructs a SQL SELECT LIMIT and OFFSET.
     */
    public static final class Paginate {
        private Long pageNum;
        private Long pageSize;

        /**
         * Sets the page number and size
         *
         * @param pageNum the page number to select, >= 0
         * @param pageSize the size of the page, > 0
         */
        public void set(long pageNum, long pageSize) {
            if (pageNum < 0) {
                throw new IllegalArgumentException("pageNum must be greater than or equal to 0");
            }
            if (pageSize < 1) {
                throw new IllegalArgumentException("pageSize must be greater than 1");
            }
            this.pageNum = pageNum;
            this.pageSize = pageSize;
        }

        /**
         * Appends the limit and offset to the query. If they are not set, nothing is appended.
         *
         * @param queryBuilder the query builder to append to
         */
        public void buildQuery(StringBuilder queryBuilder) {
            if (pageNum == null) {
                return;
            }
            queryBuilder.append(" LIMIT ").append(pageSize);
            if (pageNum > 0) {
                queryBuilder.append(" OFFSET ").append(pageNum * pageSize);
            }
        }
    }
}
