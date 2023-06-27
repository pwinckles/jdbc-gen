package com.pwinckles.jdbcgen.processor;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.util.Objects;

public class ColumnSpec {

    private final String columnName;
    private final boolean identity;
    private final VariableElement fieldElement;
    private final ExecutableElement getterElement;
    private final ExecutableElement setterElement;
    private final FieldGetMethod getMethod;
    private final FieldSetMethod setMethod;

    public static Builder builder() {
        return new Builder();
    }

    private ColumnSpec(String columnName,
                      boolean identity,
                      VariableElement fieldElement,
                      ExecutableElement getterElement,
                      ExecutableElement setterElement,
                      FieldGetMethod getMethod,
                      FieldSetMethod setMethod) {
        this.columnName = Objects.requireNonNull(columnName, "columnName cannot be null");
        this.identity = identity;
        this.fieldElement = Objects.requireNonNull(fieldElement, "fieldElement cannot be null");
        this.getterElement = getterElement;
        this.setterElement = setterElement;
        this.getMethod = Objects.requireNonNull(getMethod, "getMethod cannot be null");
        this.setMethod = Objects.requireNonNull(setMethod, "setMethod cannot be null");

        if (getMethod == FieldGetMethod.GETTER) {
            Objects.requireNonNull(getterElement, "getterElement cannot be null");
        }
        if (setMethod == FieldSetMethod.SETTER) {
            Objects.requireNonNull(setterElement, "setterElement cannot be null");
        }
    }

    public String getColumnName() {
        return columnName;
    }

    public String getFieldName() {
        return fieldElement.getSimpleName().toString();
    }

    public boolean isPrimitive() {
        return fieldElement.asType().getKind().isPrimitive();
    }

    public boolean isIdentity() {
        return identity;
    }

    public VariableElement getFieldElement() {
        return fieldElement;
    }

    public ExecutableElement getGetterElement() {
        return getterElement;
    }

    public String getGetterName() {
        if (getterElement == null) {
            return null;
        }
        return getterElement.getSimpleName().toString();
    }

    public ExecutableElement getSetterElement() {
        return setterElement;
    }

    public String getSetterName() {
        if (setterElement == null) {
            return null;
        }
        return setterElement.getSimpleName().toString();
    }

    public FieldGetMethod getGetMethod() {
        return getMethod;
    }

    public FieldSetMethod getSetMethod() {
        return setMethod;
    }

    public static class Builder {
        private String columnName;
        private boolean identity;
        private VariableElement fieldElement;
        private ExecutableElement getterElement;
        private ExecutableElement setterElement;
        private FieldGetMethod getMethod;
        private FieldSetMethod setMethod;

        public Builder withColumnName(String columnName) {
            this.columnName = columnName;
            return this;
        }

        public Builder withIdentity(boolean identity) {
            this.identity = identity;
            return this;
        }

        public Builder withFieldElement(VariableElement fieldElement) {
            this.fieldElement = fieldElement;
            return this;
        }

        public Builder withGetterElement(ExecutableElement getterElement) {
            this.getterElement = getterElement;
            return this;
        }

        public Builder withSetterElement(ExecutableElement setterElement) {
            this.setterElement = setterElement;
            return this;
        }

        public Builder withGetMethod(FieldGetMethod getMethod) {
            this.getMethod = getMethod;
            return this;
        }

        public Builder withSetMethod(FieldSetMethod setMethod) {
            this.setMethod = setMethod;
            return this;
        }

        public ColumnSpec build() {
            return new ColumnSpec(columnName,
                    identity,
                    fieldElement,
                    getterElement,
                    setterElement,
                    getMethod,
                    setMethod);
        }
    }
}
