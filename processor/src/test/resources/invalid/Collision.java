package com.pwinckles.jdbcgen.processor.invalid;

import com.pwinckles.jdbcgen.JdbcGen;
import com.pwinckles.jdbcgen.JdbcGenColumn;
import com.pwinckles.jdbcgen.JdbcGenTable;

public class Collision {

    @JdbcGen
    @JdbcGenTable(name = " test")
    public static class First {
        @JdbcGenColumn(name = "id", identity = true)
        Long id;

        @JdbcGenColumn(name = "val")
        String value;
    }

    @JdbcGen(name = "FirstDb")
    @JdbcGenTable(name = " test")
    public static class Second {
        @JdbcGenColumn(name = "id", identity = true)
        Long id;

        @JdbcGenColumn(name = "val")
        String value;
    }

}
