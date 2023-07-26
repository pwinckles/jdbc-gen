package com.pwinckles.jdbcgen.processor;

import com.pwinckles.jdbcgen.JdbcGen;
import com.pwinckles.jdbcgen.JdbcGenColumn;
import com.pwinckles.jdbcgen.JdbcGenDb;
import com.pwinckles.jdbcgen.JdbcGenTable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

/**
 * Analyzes an entity class to extract the metadata necessary to generate a {@link JdbcGenDb} instance.
 */
public class EntityAnalyzer {

    private static final Pattern UNESCAPED_QUOTE_PATTERN = Pattern.compile("(?<!\\\\)\"");
    private static final String ESCAPED_QUOTE = "\\\\\"";

    private final ProcessingEnvironment processingEnv;

    public EntityAnalyzer(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }

    /**
     * Analyzes a class that was annotated with {@link JdbcGen}. The metadata necessary to generate an instance of
     * {@link JdbcGenDb} is extracted, validated, and returned.
     *
     * @param entity the entity class
     * @return the entity metadata
     */
    public EntitySpec analyze(TypeElement entity) {
        validateEntityType(entity);

        var entitySpecBuilder = EntitySpec.builder().withTypeElement(entity);

        entitySpecBuilder.withPackageName(processingEnv
                .getElementUtils()
                .getPackageOf(entity)
                .getQualifiedName()
                .toString());

        var genAnnotation = getAndValidateGenAnnotation(entity);

        if (genAnnotation.name() == null || genAnnotation.name().isBlank()) {
            entitySpecBuilder.withDbClassName(entity.getSimpleName().toString() + "Db");
        } else {
            entitySpecBuilder.withDbClassName(genAnnotation.name());
        }

        var tableAnnotation = getAndValidateTableAnnotation(entity);
        entitySpecBuilder.withTableName(escapeQuotes(tableAnnotation.name()));

        var fields = getAndValidateFields(entity);

        var constructor = resolveConstructor(entity, fields);
        var hasCanonicalConstructor = !constructor.getParameters().isEmpty();
        entitySpecBuilder.withConstructorElement(constructor).withCanonicalConstructor(hasCanonicalConstructor);

        var fieldSpecs = resolveFieldSpecs(entity, fields, hasCanonicalConstructor);
        entitySpecBuilder.withColumns(fieldSpecs);

        fieldSpecs.stream().filter(FieldSpec::isIdentity).findFirst().ifPresent(entitySpecBuilder::withIdentityColumn);

        return entitySpecBuilder.build();
    }

    private void validateEntityType(TypeElement entity) {
        if (entity.getModifiers().contains(Modifier.PRIVATE)) {
            throw new RuntimeException(entity.getQualifiedName() + " must not be private.");
        }
        if (entity.getNestingKind() == NestingKind.MEMBER
                && !entity.getModifiers().contains(Modifier.STATIC)) {
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
            throw new IllegalArgumentException(
                    "@JdbcGenTable(name) on " + entity.getQualifiedName() + " cannot be blank.");
        }

        return tableAnnotation;
    }

    private List<VariableElement> getAndValidateFields(TypeElement entity) {
        var fields = entity.getEnclosedElements().stream()
                .filter(element -> {
                    var annotations = element.getAnnotationsByType(JdbcGenColumn.class);

                    if (annotations.length > 1) {
                        throw new RuntimeException(
                                entity.getQualifiedName() + " must not have more than one @JdbcGenColumn annotation.");
                    } else if (annotations.length == 1) {
                        var name = annotations[0].name();
                        if (name == null || name.isBlank()) {
                            throw new IllegalArgumentException(
                                    "@JdbcGenColumn(name) on " + entity.getQualifiedName() + " cannot be blank.");
                        }
                    }

                    return annotations.length == 1;
                })
                .map(VariableElement.class::cast)
                .collect(Collectors.toList());

        if (fields.isEmpty()) {
            throw new RuntimeException(
                    entity.getQualifiedName() + " must have at least one field annotated with @JdbcGenColumn.");
        }

        if (fields.stream()
                        .filter(field -> field.getAnnotationsByType(JdbcGenColumn.class)[0].identity())
                        .count()
                != 1) {
            throw new RuntimeException(entity.getQualifiedName()
                    + " must have exactly one field annotated with @JdbcGenColumn(identity = true).");
        }

        return fields;
    }

    private ExecutableElement resolveConstructor(TypeElement entity, List<VariableElement> fields) {
        var fieldType = fields.stream().map(VariableElement::asType).collect(Collectors.toList());

        var publicConstructors = entity.getEnclosedElements().stream()
                .filter(element -> element.getKind() == ElementKind.CONSTRUCTOR)
                .map(ExecutableElement.class::cast)
                .filter(element -> !element.getModifiers().contains(Modifier.PRIVATE))
                .collect(Collectors.toList());

        ExecutableElement defaultConstructor = null;

        for (var constructor : publicConstructors) {
            var paramTypes = constructor.getParameters().stream()
                    .map(VariableElement::asType)
                    .collect(Collectors.toList());

            if (Objects.equals(fieldType, paramTypes)) {
                return constructor;
            } else if (paramTypes.isEmpty()) {
                defaultConstructor = constructor;
            }
        }

        if (defaultConstructor == null) {
            throw new RuntimeException(
                    entity.getQualifiedName() + " must hava non-private default constructor or canonical constructor.");
        }

        return defaultConstructor;
    }

    private List<FieldSpec> resolveFieldSpecs(
            TypeElement entity, List<VariableElement> fields, boolean hasCanonicalConstructor) {
        var fieldSpecs = new ArrayList<FieldSpec>();

        var candidateMethodMap = entity.getEnclosedElements().stream()
                .filter(element -> element.getKind() == ElementKind.METHOD)
                .map(ExecutableElement.class::cast)
                .filter(element -> !element.getModifiers().contains(Modifier.PRIVATE))
                .collect(Collectors.toMap(element -> element.getSimpleName().toString(), Function.identity()));

        for (var field : fields) {
            var name = field.getSimpleName().toString();
            var fieldIsPrivate = field.getModifiers().contains(Modifier.PRIVATE);
            var columnAnnotation = field.getAnnotationsByType(JdbcGenColumn.class)[0];
            var builder = FieldSpec.builder()
                    .withColumnName(escapeQuotes(columnAnnotation.name()))
                    .withIdentity(columnAnnotation.identity())
                    .withFieldElement(field);

            var getter = candidateMethodMap.get(getterName(field));
            if (getter != null
                    && getter.getParameters().isEmpty()
                    && field.asType().equals(getter.getReturnType())) {
                builder.withGetterElement(getter).withGetMethod(FieldGetMethod.GETTER);
            } else if (fieldIsPrivate) {
                throw new RuntimeException(entity.getQualifiedName() + "." + name
                        + " must either be non-private or have a non-private getter.");
            } else {
                builder.withGetMethod(FieldGetMethod.DIRECT);
            }

            var setter = candidateMethodMap.get(BeanUtil.setterName(name));
            if (setter != null
                    && setter.getParameters().size() == 1
                    && field.asType().equals(setter.getParameters().get(0).asType())) {
                builder.withSetterElement(setter);

                if (!hasCanonicalConstructor) {
                    builder.withSetMethod(FieldSetMethod.SETTER);
                } else {
                    builder.withSetMethod(FieldSetMethod.CONSTRUCTOR);
                }
            } else if (hasCanonicalConstructor) {
                builder.withSetMethod(FieldSetMethod.CONSTRUCTOR);
            } else if (!fieldIsPrivate && !field.getModifiers().contains(Modifier.FINAL)) {
                builder.withSetMethod(FieldSetMethod.DIRECT);
            } else {
                throw new RuntimeException(entity.getQualifiedName() + "." + name
                        + " must be writable by one of the following, non-private mechanisms: "
                        + "canonical constructor, setter, or non-final field.");
            }

            fieldSpecs.add(builder.build());
        }

        return fieldSpecs;
    }

    private String getterName(VariableElement field) {
        return BeanUtil.getterName(
                field.getSimpleName().toString(),
                processingEnv.getTypeUtils().getPrimitiveType(TypeKind.BOOLEAN).equals(field.asType()));
    }

    private String escapeQuotes(String value) {
        return UNESCAPED_QUOTE_PATTERN.matcher(value).replaceAll(ESCAPED_QUOTE);
    }
}
