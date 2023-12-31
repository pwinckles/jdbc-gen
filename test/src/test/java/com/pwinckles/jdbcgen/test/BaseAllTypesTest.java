package com.pwinckles.jdbcgen.test;

import com.pwinckles.jdbcgen.BasePatch;
import com.pwinckles.jdbcgen.JdbcGenDb;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class BaseAllTypesTest<E, I, P extends BasePatch, C, F> extends TestBase<E, I, P, C, F> {

    protected BaseAllTypesTest(JdbcGenDb<E, I, P, C, F> db) {
        super(db);
    }

    @Override
    protected void createTable(Connection conn) throws SQLException {
        try (var stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE all_types (" + "at_id IDENTITY PRIMARY KEY,"
                    + " at_long_prim BIGINT NOT NULL,"
                    + " at_int_obj INT,"
                    + " at_int_prim INT NOT NULL,"
                    + " at_short_obj SMALLINT,"
                    + " at_short_prim SMALLINT NOT NULL,"
                    + " at_double_obj DOUBLE,"
                    + " at_double_prim DOUBLE NOT NULL,"
                    + " at_bool_obj BOOLEAN,"
                    + " at_bool_prim BOOLEAN NOT NULL,"
                    + " at_string VARCHAR(255),"
                    + " at_instant TIMESTAMP,"
                    + " at_local_date_time TIMESTAMP,"
                    + " at_local_date DATE,"
                    + " at_offset_date_time TIMESTAMP WITH TIME ZONE,"
                    + " at_date DATE,"
                    + " at_timestamp TIMESTAMP,"
                    + " at_byte_array BLOB,"
                    + " at_uuid UUID,"
                    + " at_enum VARCHAR(255)"
                    + ")");
        }
    }
}
