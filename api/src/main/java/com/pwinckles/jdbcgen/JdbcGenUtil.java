package com.pwinckles.jdbcgen;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Utility methods used by JdbcGen
 */
public final class JdbcGenUtil {

    private JdbcGenUtil() {}

    public static <T> T getNullableValue(ResultSet rs, int index, Class<T> clazz) throws SQLException {
        var value = rs.getObject(index, clazz);
        if (rs.wasNull()) {
            return null;
        }
        return value;
    }

    public static String enumToString(Enum<?> e) {
        if (e == null) {
            return null;
        }
        return e.name();
    }

    public static <T extends Enum<T>> T enumFromResultSet(ResultSet rs, int index, Class<T> clazz) throws SQLException {
        var value = rs.getObject(index, String.class);
        if (rs.wasNull()) {
            return null;
        }
        return Enum.valueOf(clazz, value);
    }
}
