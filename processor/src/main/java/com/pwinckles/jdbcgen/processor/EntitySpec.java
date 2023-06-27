package com.pwinckles.jdbcgen.processor;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Objects;

public class EntitySpec {

    private final String packageName;
    private final String dbClassName;
    private final String tableName;
    private final TypeElement typeElement;
    private final ExecutableElement constructorElement;
    private final ColumnSpec identityColumn;
    private final List<ColumnSpec> columns;
    private final boolean canonicalConstructor;

    public static Builder builder() {
        return new Builder();
    }

    private EntitySpec(String packageName,
                       String dbClassName,
                      String tableName,
                      TypeElement typeElement,
                      ExecutableElement constructorElement,
                      ColumnSpec identityColumn,
                      List<ColumnSpec> columns,
                      boolean canonicalConstructor) {
        this.packageName = Objects.requireNonNull(packageName, "packageName cannot be null");
        this.dbClassName = Objects.requireNonNull(dbClassName, "dbClassName cannot be null");
        this.tableName = Objects.requireNonNull(tableName, "tableName cannot be null");
        this.typeElement = Objects.requireNonNull(typeElement, "typeElement cannot be null");
        this.constructorElement = Objects.requireNonNull(constructorElement, "constructorElement cannot be null");
        this.identityColumn = Objects.requireNonNull(identityColumn, "identityColumn cannot be null");
        this.columns = Objects.requireNonNull(columns, "columns cannot be null");
        this.canonicalConstructor = canonicalConstructor;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getDbClassName() {
        return dbClassName;
    }

    public String getTableName() {
        return tableName;
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }

    public ExecutableElement getConstructorElement() {
        return constructorElement;
    }

    public ColumnSpec getIdentityColumn() {
        return identityColumn;
    }

    public List<ColumnSpec> getColumns() {
        return columns;
    }

    public boolean isCanonicalConstructor() {
        return canonicalConstructor;
    }

    public static class Builder {
        private String packageName;
        private String dbClassName;
        private String tableName;
        private TypeElement typeElement;
        private ExecutableElement constructorElement;
        private ColumnSpec identityColumn;
        private List<ColumnSpec> columns;
        private boolean canonicalConstructor;

        public Builder withPackageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        public Builder withDbClassName(String dbClassName) {
            this.dbClassName = dbClassName;
            return this;
        }

        public Builder withTableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public Builder withTypeElement(TypeElement typeElement) {
            this.typeElement = typeElement;
            return this;
        }

        public Builder withConstructorElement(ExecutableElement constructorElement) {
            this.constructorElement = constructorElement;
            return this;
        }

        public Builder withIdentityColumn(ColumnSpec identityColumn) {
            this.identityColumn = identityColumn;
            return this;
        }

        public Builder withColumns(List<ColumnSpec> columns) {
            this.columns = columns;
            return this;
        }

        public Builder withCanonicalConstructor(boolean canonicalConstructor) {
            this.canonicalConstructor = canonicalConstructor;
            return this;
        }

        public EntitySpec build() {
            return new EntitySpec(packageName,
                    dbClassName,
                    tableName,
                    typeElement,
                    constructorElement,
                    identityColumn,
                    columns,
                    canonicalConstructor);
        }
    }
}
