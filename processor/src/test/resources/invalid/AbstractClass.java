package com.pwinckles.jdbcgen.processor.invalid;

import com.pwinckles.jdbcgen.JdbcGen;
import com.pwinckles.jdbcgen.JdbcGenColumn;
import com.pwinckles.jdbcgen.JdbcGenTable;

@JdbcGen
@JdbcGenTable(name = " test")
public abstract class AbstractClass {

    @JdbcGenColumn(name = "id", identity = true)
    Long id;

    @JdbcGenColumn(name = "val")
    String value;

}
