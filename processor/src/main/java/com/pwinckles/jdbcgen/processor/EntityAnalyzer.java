package com.pwinckles.jdbcgen.processor;

import com.pwinckles.jdbcgen.JdbcGenColumn;
import com.pwinckles.jdbcgen.JdbcGen;
import com.pwinckles.jdbcgen.JdbcGenTable;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EntityAnalyzer {

    private final ProcessingEnvironment processingEnv;

    public EntityAnalyzer(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }

    public EntitySpec analyze(TypeElement entity) {
        validateEntityType(entity);

        var entitySpecBuilder = EntitySpec.builder()
                .withTypeElement(entity);

        entitySpecBuilder.withPackageName(processingEnv.getElementUtils()
                .getPackageOf(entity).getQualifiedName().toString());

        var genAnnotation = getAndValidateGenAnnotation(entity);

        if (genAnnotation.name() == null || genAnnotation.name().isBlank()) {
            entitySpecBuilder.withDbClassName(entity.getSimpleName().toString() + "Db");
        } else {
            entitySpecBuilder.withDbClassName(genAnnotation.name());
        }

        var tableAnnotation = getAndValidateTableAnnotation(entity);
        entitySpecBuilder.withTableName(tableAnnotation.name());

        var columnFields = getAndValidateColumnFields(entity);

        var constructor = resolveConstructor(entity, columnFields);
        var hasCanonicalConstructor = !constructor.getParameters().isEmpty();
        entitySpecBuilder.withConstructorElement(constructor)
                .withCanonicalConstructor(hasCanonicalConstructor);

        var columnSpecs = resolveColumnSpecs(entity, columnFields, hasCanonicalConstructor);
        entitySpecBuilder.withColumns(columnSpecs);

        columnSpecs.stream()
                .filter(ColumnSpec::isIdentity).findFirst()
                .ifPresent(entitySpecBuilder::withIdentityColumn);

        return entitySpecBuilder.build();
    }

    private void validateEntityType(TypeElement entity) {
        if (entity.getModifiers().contains(Modifier.PRIVATE)) {
            throw new RuntimeException(entity.getQualifiedName() + " must not be private.");
        }
        if (entity.getNestingKind() == NestingKind.MEMBER && !entity.getModifiers().contains(Modifier.STATIC)) {
            throw new RuntimeException(entity.getQualifiedName() + " must be static.");
        }
        if (entity.getModifiers().contains(Modifier.ABSTRACT)) {
            throw new RuntimeException(entity.getQualifiedName() + " must not be abstract.");
        }
        if (!entity.getTypeParameters().isEmpty()) {
            throw new RuntimeException(entity.getQualifiedName() + " must not have type parameters.");
        }
    }

    private JdbcGen getAndValidateGenAnnotation(TypeElement entity) {
        var genAnnotations = entity.getAnnotationsByType(JdbcGen.class);
        if (genAnnotations.length != 1) {
            throw new RuntimeException(entity.getQualifiedName() + " must have exactly one @JdbcGen annotation.");
        }
        return genAnnotations[0];
    }

    private JdbcGenTable getAndValidateTableAnnotation(TypeElement entity) {
        var tableAnnotations = entity.getAnnotationsByType(JdbcGenTable.class);

        // TODO this is not true once joins are supported
        if (tableAnnotations.length != 1) {
            throw new RuntimeException(entity.getQualifiedName() + " must have exactly one @JdbcGenTable annotation.");
        }

        var tableAnnotation = tableAnnotations[0];
        var tableName = tableAnnotation.name();

        if (tableName == null || tableName.isBlank()) {
            throw new IllegalArgumentException("@JdbcGenTable(name) on "
                    + entity.getQualifiedName()
                    + " cannot be blank.");
        }

        return tableAnnotation;
    }

    private List<VariableElement> getAndValidateColumnFields(TypeElement entity) {
        var columnFields = entity.getEnclosedElements().stream()
                .filter(element -> {
                    var annotations = element.getAnnotationsByType(JdbcGenColumn.class);

                    if (annotations.length > 1) {
                        throw new RuntimeException(entity.getQualifiedName() + " must not have more than one @JdbcGenColumn annotation.");
                    } else if (annotations.length == 1) {
                        var name = annotations[0].name();
                        if (name == null || name.isBlank()) {
                            throw new IllegalArgumentException("@JdbcGenColumn(name) on "
                                    + entity.getQualifiedName() + " cannot be blank.");
                        }
                    }

                    return annotations.length == 1;
                })
                .map(VariableElement.class::cast)
                .collect(Collectors.toList());

        if (columnFields.isEmpty()) {
            throw new RuntimeException(entity.getQualifiedName() + " must have at least one field annotated with @JdbcGenColumn.");
        }

        if (columnFields.stream().filter(field -> field.getAnnotationsByType(JdbcGenColumn.class)[0].identity()).count() != 1) {
            throw new RuntimeException(entity.getQualifiedName() + " must have exactly one field annotated with @JdbcGenColumn(identity = true).");
        }

        return columnFields;
    }

    private ExecutableElement resolveConstructor(TypeElement entity, List<VariableElement> columnFields) {
        var columnTypes = columnFields.stream().map(VariableElement::asType).collect(Collectors.toList());

        var publicConstructors = entity.getEnclosedElements().stream()
                .filter(element -> element.getKind() == ElementKind.CONSTRUCTOR)
                .map(ExecutableElement.class::cast)
                .filter(element -> !element.getModifiers().contains(Modifier.PRIVATE))
                .collect(Collectors.toList());

        ExecutableElement defaultConstructor = null;

        for (var constructor : publicConstructors) {
            var paramTypes = constructor.getParameters().stream().map(VariableElement::asType).collect(Collectors.toList());

            if (Objects.equals(columnTypes, paramTypes)) {
                return constructor;
            } else if (paramTypes.isEmpty()) {
                defaultConstructor = constructor;
            }
        }

        if (defaultConstructor == null) {
            throw new RuntimeException(entity.getQualifiedName() + " must hava non-private default constructor or canonical constructor.");
        }

        return defaultConstructor;
    }

    private List<ColumnSpec> resolveColumnSpecs(TypeElement entity,
                                                List<VariableElement> columnFields,
                                                boolean hasCanonicalConstructor) {
        var columnSpecs = new ArrayList<ColumnSpec>();

        var candidateMethodMap = entity.getEnclosedElements().stream()
                .filter(element -> element.getKind() == ElementKind.METHOD)
                .map(ExecutableElement.class::cast)
                .filter(element -> !element.getModifiers().contains(Modifier.PRIVATE))
                .collect(Collectors.toMap(element -> element.getSimpleName().toString(), Function.identity()));

        for (var columnField : columnFields) {
            var name = columnField.getSimpleName().toString();
            var fieldIsPrivate = columnField.getModifiers().contains(Modifier.PRIVATE);
            var columnAnnotation = columnField.getAnnotationsByType(JdbcGenColumn.class)[0];
            var columnSpecBuilder = ColumnSpec.builder()
                    .withColumnName(columnAnnotation.name())
                    .withIdentity(columnAnnotation.identity())
                    .withFieldElement(columnField);

            var getter = candidateMethodMap.get(getterName(columnField));
            if (getter != null
                    && getter.getParameters().isEmpty()
                    && columnField.asType().equals(getter.getReturnType())) {
                columnSpecBuilder.withGetterElement(getter)
                        .withGetMethod(FieldGetMethod.GETTER);
            } else if (fieldIsPrivate) {
                throw new RuntimeException(entity.getQualifiedName() + "." + name + " must either be non-private or have a non-private getter.");
            } else {
                columnSpecBuilder.withGetMethod(FieldGetMethod.DIRECT);
            }

            var setter = candidateMethodMap.get(BeanUtil.setterName(name));
            if (setter != null
                    && setter.getParameters().size() == 1
                    && columnField.asType().equals(setter.getParameters().get(0).asType())) {
                columnSpecBuilder.withSetterElement(setter);

                if (!hasCanonicalConstructor) {
                    columnSpecBuilder.withSetMethod(FieldSetMethod.SETTER);
                } else {
                    columnSpecBuilder.withSetMethod(FieldSetMethod.CONSTRUCTOR);
                }
            } else if (hasCanonicalConstructor) {
                columnSpecBuilder.withSetMethod(FieldSetMethod.CONSTRUCTOR);
            } else if (!fieldIsPrivate && !columnField.getModifiers().contains(Modifier.FINAL)) {
                columnSpecBuilder.withSetMethod(FieldSetMethod.DIRECT);
            } else {
                throw new RuntimeException(entity.getQualifiedName() + "." + name + " must be writable by one of the following, non-private mechanisms: " +
                        "canonical constructor, setter, or non-final field.");
            }

            columnSpecs.add(columnSpecBuilder.build());
        }

        return columnSpecs;
    }

    private String getterName(VariableElement field) {
        return BeanUtil.getterName(field.getSimpleName().toString(),
                processingEnv.getTypeUtils().getPrimitiveType(TypeKind.BOOLEAN).equals(field.asType()));
    }

}
