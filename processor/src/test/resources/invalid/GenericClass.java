package com.pwinckles.jdbcgen.processor.invalid;

import com.pwinckles.jdbcgen.JdbcGen;
import com.pwinckles.jdbcgen.JdbcGenColumn;
import com.pwinckles.jdbcgen.JdbcGenTable;

@JdbcGen
@JdbcGenTable(name = " test")
public class GenericClass<I> {

    @JdbcGenColumn(name = "id", identity = true)
    I id;

    @JdbcGenColumn(name = "val")
    String value;

}
