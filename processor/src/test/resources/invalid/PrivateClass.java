package com.pwinckles.jdbcgen.processor.invalid;

import com.pwinckles.jdbcgen.JdbcGen;
import com.pwinckles.jdbcgen.JdbcGenColumn;
import com.pwinckles.jdbcgen.JdbcGenTable;

public class PrivateClass {

    @JdbcGen
    @JdbcGenTable(name = " test")
    private static class Inner {
        @JdbcGenColumn(name = "id", identity = true)
        Long id;

        @JdbcGenColumn(name = "val")
        String value;
    }

}
