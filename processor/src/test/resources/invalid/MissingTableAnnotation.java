package com.pwinckles.jdbcgen.processor.invalid;

import com.pwinckles.jdbcgen.JdbcGen;
import com.pwinckles.jdbcgen.JdbcGenColumn;

@JdbcGen
public class MissingTableAnnotation {

    @JdbcGenColumn(name = "id", identity = true)
    Long id;

    @JdbcGenColumn(name = "val")
    String value;

}
