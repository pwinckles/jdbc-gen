package com.pwinckles.jdbcgen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Maps an entity to a table in the database.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface JdbcGenTable {

    /**
     * The name of the table in the database that the entity maps to.
     *
     * @return the table name
     */
    String name();
}
