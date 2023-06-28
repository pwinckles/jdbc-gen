package com.pwinckles.jdbcgen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an entity to generate a {@link JdbcGenDb} implementation for. By default, the generated class will be named
 * EntityClassNameDb. For example, suppose there's an entity class named Example, then the generated class will be
 * ExampleDb.
 * <p>
 * Annotated entity classes may not be private or non-static inner classes.
 * <p>
 * In addition to this annotation, an entity must also be annotated with {@link JdbcGenTable} and {@link JdbcGenColumn}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface JdbcGen {

    /**
     * By default, the generated class will be named EntityClassNameDb. For example, suppose there's an entity class
     * named Example, then the generated class will be ExampleDb. Specifying a "name" overrides this behavior, naming
     * the generated class as specified.
     *
     * @return the class name to use for the generated class
     */
    String name() default "";
}
