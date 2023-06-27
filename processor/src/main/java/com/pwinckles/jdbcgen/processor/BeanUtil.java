package com.pwinckles.jdbcgen.processor;

public final class BeanUtil {

    private BeanUtil() {

    }

    public static String getterName(String fieldName, boolean isBoolean) {
        var prefix = isBoolean ? "is" : "get";
        return beanMethodName(prefix, fieldName);
    }

    public static String setterName(String fieldName) {
        return beanMethodName("set", fieldName);
    }

    private static String beanMethodName(String prefix, String fieldName) {
        return prefix + String.valueOf(fieldName.charAt(0)).toUpperCase() + fieldName.substring(1);
    }

}
