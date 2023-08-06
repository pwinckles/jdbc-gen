package com.pwinckles.jdbcgen.test;

import com.pwinckles.jdbcgen.JdbcGen;
import com.pwinckles.jdbcgen.JdbcGenColumn;
import com.pwinckles.jdbcgen.JdbcGenTable;
import io.soabase.recordbuilder.core.RecordBuilderFull;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@JdbcGen
@JdbcGenTable(name = "all_types")
@RecordBuilderFull
public record RecordAllTypesEntity(
        @JdbcGenColumn(name = "at_id", identity = true) Long longId,
        @JdbcGenColumn(name = "at_long_prim") long longPrim,
        @JdbcGenColumn(name = "at_int_obj") Integer intObj,
        @JdbcGenColumn(name = "at_int_prim") int intPrim,
        @JdbcGenColumn(name = "at_short_obj") Short shortObj,
        @JdbcGenColumn(name = "at_short_prim") short shortPrim,
        @JdbcGenColumn(name = "at_double_obj") Double doubleObj,
        @JdbcGenColumn(name = "at_double_prim") double doublePrim,
        @JdbcGenColumn(name = "at_bool_obj") Boolean boolObj,
        @JdbcGenColumn(name = "at_bool_prim") boolean boolPrim,
        @JdbcGenColumn(name = "at_string") String string,
        @JdbcGenColumn(name = "at_instant") Instant instant,
        @JdbcGenColumn(name = "at_local_date_time") LocalDateTime localDateTime,
        @JdbcGenColumn(name = "at_local_date") LocalDate localDate,
        @JdbcGenColumn(name = "at_offset_date_time") OffsetDateTime offsetDateTime,
        @JdbcGenColumn(name = "at_date") Date date,
        @JdbcGenColumn(name = "at_timestamp") Timestamp timestamp,
        @JdbcGenColumn(name = "at_byte_array") byte[] byteArray,
        @JdbcGenColumn(name = "at_uuid") UUID uuid) {}
