package com.pwinckles.jdbcgen.processor;

import com.pwinckles.jdbcgen.JdbcGenDb;
import java.util.List;
import java.util.Objects;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

/**
 * Contains metadata about the mapping of an entity to a DB table.
 */
public class EntitySpec {

    private final String packageName;
    private final String dbClassName;
    private final String tableName;
    private final TypeElement typeElement;
    private final ExecutableElement constructorElement;
    private final FieldSpec identityField;
    private final List<FieldSpec> fields;
    private final boolean canonicalConstructor;

    public static Builder builder() {
        return new Builder();
    }

    private EntitySpec(
            String packageName,
            String dbClassName,
            String tableName,
            TypeElement typeElement,
            ExecutableElement constructorElement,
            FieldSpec identityField,
            List<FieldSpec> fields,
            boolean canonicalConstructor) {
        this.packageName = Objects.requireNonNull(packageName, "packageName cannot be null");
        this.dbClassName = Objects.requireNonNull(dbClassName, "dbClassName cannot be null");
        this.tableName = Objects.requireNonNull(tableName, "tableName cannot be null");
        this.typeElement = Objects.requireNonNull(typeElement, "typeElement cannot be null");
        this.constructorElement = Objects.requireNonNull(constructorElement, "constructorElement cannot be null");
        this.identityField = Objects.requireNonNull(identityField, "identityColumn cannot be null");
        this.fields = Objects.requireNonNull(fields, "columns cannot be null");
        this.canonicalConstructor = canonicalConstructor;
    }

    /**
     * @return the package that contains the entity
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * @return the name of the {@link JdbcGenDb} to generate
     */
    public String getDbClassName() {
        return dbClassName;
    }

    /**
     * @return the name of the DB table
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @return the entity's type element
     */
    public TypeElement getTypeElement() {
        return typeElement;
    }

    /**
     * @return the entity's constructor element
     */
    public ExecutableElement getConstructorElement() {
        return constructorElement;
    }

    /**
     * @return the entity's identity field
     */
    public FieldSpec getIdentityField() {
        return identityField;
    }

    /**
     * @return the entity's fields
     */
    public List<FieldSpec> getFields() {
        return fields;
    }

    /**
     * @return true if the constructor is a canonical constructor
     */
    public boolean isCanonicalConstructor() {
        return canonicalConstructor;
    }

    public static class Builder {
        private String packageName;
        private String dbClassName;
        private String tableName;
        private TypeElement typeElement;
        private ExecutableElement constructorElement;
        private FieldSpec identityColumn;
        private List<FieldSpec> columns;
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

        public Builder withIdentityColumn(FieldSpec identityColumn) {
            this.identityColumn = identityColumn;
            return this;
        }

        public Builder withColumns(List<FieldSpec> columns) {
            this.columns = columns;
            return this;
        }

        public Builder withCanonicalConstructor(boolean canonicalConstructor) {
            this.canonicalConstructor = canonicalConstructor;
            return this;
        }

        public EntitySpec build() {
            return new EntitySpec(
                    packageName,
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
