package com.pwinckles.jdbcgen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Maps an entity field to a column in the database.
 * <p>
 * Annotated fields must have a non-private way for getting and setting their values. This can either be direct field
 * access, getter/setter, or a canonical constructor (for setting). If multiple of these methods are possible, then
 * the constructor takes precedence, followed by the getter/setter, and finally direct access.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface JdbcGenColumn {

    /**
     *The name of the column in the database to map the entity field to.
     *
     * @return the column name
     */
    String name();

    /**
     * Indicates if the field is an ID field. Each entity must have exactly one ID field.
     *
     * @return true if the field is the entity's ID
     */
    boolean identity() default false;
}
