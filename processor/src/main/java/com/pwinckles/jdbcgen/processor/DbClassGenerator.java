package com.pwinckles.jdbcgen.processor;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

import com.google.common.base.CaseFormat;
import com.pwinckles.jdbcgen.BasePatch;
import com.pwinckles.jdbcgen.JdbcGenDb;
import com.pwinckles.jdbcgen.JdbcGenUtil;
import com.pwinckles.jdbcgen.SelectBuilder;
import com.pwinckles.jdbcgen.filter.BooleanPredicateBuilder;
import com.pwinckles.jdbcgen.filter.ConjunctionBuilder;
import com.pwinckles.jdbcgen.filter.DoublePredicateBuilder;
import com.pwinckles.jdbcgen.filter.EnumPredicateBuilder;
import com.pwinckles.jdbcgen.filter.Filter;
import com.pwinckles.jdbcgen.filter.FilterBuilderHelper;
import com.pwinckles.jdbcgen.filter.FloatPredicateBuilder;
import com.pwinckles.jdbcgen.filter.Group;
import com.pwinckles.jdbcgen.filter.IntPredicateBuilder;
import com.pwinckles.jdbcgen.filter.LongPredicateBuilder;
import com.pwinckles.jdbcgen.filter.ObjectPredicateBuilder;
import com.pwinckles.jdbcgen.filter.PrimitiveBooleanPredicateBuilder;
import com.pwinckles.jdbcgen.filter.ShortPredicateBuilder;
import com.pwinckles.jdbcgen.filter.StringPredicateBuilder;
import com.pwinckles.jdbcgen.sort.Sort;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.processing.Generated;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * Generates an instance of {@link JdbcGenDb} for an entity.
 */
public class DbClassGenerator {

    /**
     * Generates an instance of {@link JdbcGenDb}.
     *
     * @param entitySpec the entity's metadata
     * @return the generated class
     */
    public JavaFile generate(EntitySpec entitySpec) {
        var entityType = TypeName.get(entitySpec.getTypeElement().asType());
        var idType = TypeName.get(
                        entitySpec.getIdentityField().getFieldElement().asType())
                .box();
        var patchType = ClassName.get(entitySpec.getPackageName(), entitySpec.getDbClassName(), "Patch");
        var filterBuilderType =
                ClassName.get(entitySpec.getPackageName(), entitySpec.getDbClassName(), "FilterBuilder");
        var sortBuilderType = ClassName.get(entitySpec.getPackageName(), entitySpec.getDbClassName(), "SortBuilder");

        var builder = TypeSpec.classBuilder(entitySpec.getDbClassName())
                .addSuperinterface(ParameterizedTypeName.get(
                        ClassName.get(JdbcGenDb.class),
                        entityType,
                        idType,
                        patchType,
                        filterBuilderType,
                        sortBuilderType))
                .addAnnotation(generatedAnnotation())
                .addJavadoc("Provides basic DB access for {@link $T} entities.", entityType);

        if (entitySpec.getTypeElement().getModifiers().contains(PUBLIC)) {
            builder.addModifiers(PUBLIC);
        } else if (entitySpec.getTypeElement().getModifiers().contains(PROTECTED)) {
            builder.addModifiers(PROTECTED);
        }

        builder.addType(genPatchClass(entityType, patchType, idType, entitySpec))
                .addType(genFilterBuilderClass(entityType, filterBuilderType, entitySpec))
                .addType(genSortBuilderClass(entityType, sortBuilderType, entitySpec))
                .addMethod(genSelect(entityType, idType, entitySpec))
                .addMethod(genSelect(entityType, filterBuilderType, sortBuilderType, entitySpec))
                .addMethod(genSelectAll(entityType, entitySpec))
                .addMethod(genCount(entitySpec))
                .addMethod(genCountFiltered(filterBuilderType, entitySpec))
                .addMethod(genInsert(entityType, idType, entitySpec))
                .addMethod(genInsertPatch(patchType, idType, entitySpec))
                .addMethod(genInsertList(entityType, idType, entitySpec))
                .addMethod(genUpdatePatch(entityType, entitySpec))
                .addMethod(genUpdatePatch(patchType, idType, entitySpec))
                .addMethod(genUpdateList(entityType, entitySpec))
                .addMethod(genDelete(idType, entitySpec))
                .addMethod(genDeleteList(idType, entitySpec))
                .addMethod(genDeleteFiltered(filterBuilderType, entitySpec))
                .addMethod(genDeleteAll(entitySpec))
                .addMethod(genInsertWithSpecifiedId(entityType, idType, entitySpec))
                .addMethod(genInsertListWithSpecifiedId(entityType, idType, entitySpec))
                .addMethod(genFromResultSet(entityType, entitySpec))
                .addMethod(genPrepareInsert(entityType, entitySpec))
                .addMethod(genPrepareUpdate(entityType, entitySpec));

        if (!entitySpec.getIdentityField().isPrimitive()) {
            builder.addMethod(genInsertWithGeneratedId(entityType, idType, entitySpec))
                    .addMethod(genInsertListWithGeneratedId(entityType, idType, entitySpec));
        }

        return JavaFile.builder(entitySpec.getPackageName(), builder.build())
                .addStaticImport(Collectors.class, "toList")
                .addStaticImport(Collections.class, "unmodifiableMap")
                .addStaticImport(Statement.class, "RETURN_GENERATED_KEYS")
                .build();
    }

    private TypeSpec genPatchClass(TypeName entityType, TypeName patchType, TypeName idType, EntitySpec entitySpec) {
        var builder = TypeSpec.classBuilder("Patch")
                .addModifiers(PUBLIC, STATIC)
                .superclass(BasePatch.class)
                .addAnnotation(generatedAnnotation())
                .addJavadoc(
                        "Patch implementation that enables partial updates for {@link $T} entities."
                                + " Only the fields that are set this object will be modified in the DB.",
                        entityType);

        var idFieldName = entitySpec.getIdentityField().getFieldName();

        builder.addMethod(MethodSpec.methodBuilder(BeanUtil.getterName(idFieldName, false))
                .addModifiers(PUBLIC)
                .returns(idType)
                .addStatement(
                        "return ($T) getData().get(\"$L\")",
                        idType,
                        entitySpec.getIdentityField().getColumnName())
                .build());

        entitySpec.getFields().forEach(fieldSpec -> {
            var field = fieldSpec.getFieldElement();
            var fieldName = field.getSimpleName().toString();
            var methodBuilder = MethodSpec.methodBuilder(BeanUtil.setterName(fieldName))
                    .addModifiers(PUBLIC)
                    .returns(patchType)
                    .addParameter(TypeName.get(field.asType()), fieldName);

            if (fieldSpec.isEnum()) {
                methodBuilder.addStatement(
                        "put(\"$L\", $T.enumToString($N))", fieldSpec.getColumnName(), JdbcGenUtil.class, fieldName);
            } else {
                methodBuilder.addStatement("put(\"$L\", $N)", fieldSpec.getColumnName(), fieldName);
            }
            methodBuilder.addStatement("return this");

            builder.addMethod(methodBuilder.build());
        });

        return builder.build();
    }

    private TypeSpec genFilterBuilderClass(TypeName entityType, TypeName filterBuilderType, EntitySpec entitySpec) {
        var builder = TypeSpec.classBuilder("FilterBuilder")
                .addModifiers(PUBLIC, STATIC)
                .addAnnotation(generatedAnnotation())
                .addJavadoc("Constructs a SQL WHERE clause.", entityType)
                .addField(Filter.class, "filter", PRIVATE, FINAL)
                .addField(
                        ParameterizedTypeName.get(ClassName.get(FilterBuilderHelper.class), filterBuilderType),
                        "helper",
                        PRIVATE,
                        FINAL)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(PUBLIC)
                        .addParameter(Filter.class, "filter")
                        .addStatement(
                                "this.filter = $T.requireNonNull(filter, \"filter cannot be null\")", Objects.class)
                        .addStatement("this.helper = new $T<>(filter, this)", FilterBuilderHelper.class)
                        .build());

        entitySpec.getFields().forEach(fieldSpec -> {
            var predicateBuilderType = mapFieldTypeToPredicateBuilderType(fieldSpec, filterBuilderType);
            if (predicateBuilderType != null) {
                builder.addMethod(MethodSpec.methodBuilder(fieldSpec.getFieldName())
                        .addJavadoc("Add a predicate on $L.\n", fieldSpec.getFieldName())
                        .addJavadoc("\n")
                        .addJavadoc("@return the builder to use to specify the field predicate")
                        .addModifiers(PUBLIC)
                        .returns(predicateBuilderType)
                        .addStatement(
                                "return new $T<>(\"$L\", filter, helper)",
                                predicateBuilderType.rawType,
                                fieldSpec.getColumnName())
                        .build());
            }
        });

        builder.addMethod(MethodSpec.methodBuilder("group")
                .addJavadoc("Groups series of predicates inside parentheses.\n")
                .addJavadoc("\n")
                .addJavadoc("@param filterBuilder the builder to use to construct the grouped predicates\n")
                .addJavadoc("@return the builder continuation after the grouping")
                .addModifiers(PUBLIC)
                .returns(ParameterizedTypeName.get(ClassName.get(ConjunctionBuilder.class), filterBuilderType))
                .addParameter(
                        ParameterizedTypeName.get(ClassName.get(Consumer.class), filterBuilderType), "filterBuilder")
                .addStatement("var groupFilter = new $T()", Filter.class)
                .addStatement("filterBuilder.accept(new $T(groupFilter))", filterBuilderType)
                .addStatement("filter.add($T.group(groupFilter))", Group.class)
                .addStatement("return helper.conjunctionBuilder()")
                .build());

        builder.addMethod(MethodSpec.methodBuilder("notGroup")
                .addJavadoc("Groups series of predicates inside parentheses, and negates the group.\n")
                .addJavadoc("\n")
                .addJavadoc("@param filterBuilder the builder to use to construct the grouped predicates\n")
                .addJavadoc("@return the builder continuation after the grouping")
                .addModifiers(PUBLIC)
                .returns(ParameterizedTypeName.get(ClassName.get(ConjunctionBuilder.class), filterBuilderType))
                .addParameter(
                        ParameterizedTypeName.get(ClassName.get(Consumer.class), filterBuilderType), "filterBuilder")
                .addStatement("var groupFilter = new $T()", Filter.class)
                .addStatement("filterBuilder.accept(new $T(groupFilter))", filterBuilderType)
                .addStatement("filter.add($T.notGroup(groupFilter))", Group.class)
                .addStatement("return helper.conjunctionBuilder()")
                .build());

        return builder.build();
    }

    private TypeSpec genSortBuilderClass(TypeName entityType, TypeName sortBuilderType, EntitySpec entitySpec) {
        var builder = TypeSpec.classBuilder("SortBuilder")
                .addModifiers(PUBLIC, STATIC)
                .addAnnotation(generatedAnnotation())
                .addJavadoc("Constructs a SQL ORDER BY clause.", entityType)
                .addField(Sort.class, "sort", PRIVATE, FINAL)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(PUBLIC)
                        .addParameter(Sort.class, "sort")
                        .addStatement("this.sort = $T.requireNonNull(sort, \"sort cannot be null\")", Objects.class)
                        .build());

        entitySpec.getFields().forEach(fieldSpec -> {
            builder.addMethod(MethodSpec.methodBuilder(fieldSpec.getFieldName() + "Asc")
                    .addJavadoc("Orders results by $L in ascending order.\n", fieldSpec.getFieldName())
                    .addJavadoc("\n")
                    .addJavadoc("@return the builder to use to specify additional ordering")
                    .addModifiers(PUBLIC)
                    .returns(sortBuilderType)
                    .addStatement("sort.asc(\"$L\")", fieldSpec.getColumnName())
                    .addStatement("return this")
                    .build());
            builder.addMethod(MethodSpec.methodBuilder(fieldSpec.getFieldName() + "Desc")
                    .addJavadoc("Orders results by $L in descending order.\n", fieldSpec.getFieldName())
                    .addJavadoc("\n")
                    .addJavadoc("@return the builder to use to specify additional ordering")
                    .addModifiers(PUBLIC)
                    .returns(sortBuilderType)
                    .addStatement("sort.desc(\"$L\")", fieldSpec.getColumnName())
                    .addStatement("return this")
                    .build());
        });

        return builder.build();
    }

    private MethodSpec genSelect(TypeName entityType, TypeName idType, EntitySpec entitySpec) {
        var query = "SELECT " + columnNames(entitySpec)
                + " FROM " + entitySpec.getTableName()
                + " WHERE " + entitySpec.getIdentityField().getColumnName() + " = ? LIMIT 1";
        return MethodSpec.methodBuilder("select")
                .addJavadoc("{@inheritDoc}")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(entityType)
                .addParameter(idType, "id")
                .addParameter(Connection.class, "conn")
                .addException(SQLException.class)
                .beginControlFlow("try (var stmt = conn.prepareStatement(\"$L\"))", query)
                .addStatement("stmt.setObject(1, id)")
                .addStatement("var rs = stmt.executeQuery()")
                .beginControlFlow("if (rs.next())")
                .addStatement("return fromResultSet(rs)")
                .endControlFlow()
                .endControlFlow()
                .addStatement("return null")
                .build();
    }

    private MethodSpec genSelect(
            TypeName entityType, TypeName filterBuilderType, TypeName sortBuilderType, EntitySpec entitySpec) {
        var query = "SELECT " + columnNames(entitySpec) + " FROM " + entitySpec.getTableName();
        return MethodSpec.methodBuilder("select")
                .addJavadoc("{@inheritDoc}")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(listType(entityType))
                .addParameter(
                        ParameterizedTypeName.get(
                                ClassName.get(Consumer.class),
                                ParameterizedTypeName.get(
                                        ClassName.get(SelectBuilder.class), filterBuilderType, sortBuilderType)),
                        "selectBuilder")
                .addParameter(Connection.class, "conn")
                .addException(SQLException.class)
                .addStatement("var results = new $T<$T>()", ArrayList.class, entityType)
                .addCode("\n")
                .addStatement("var filter = new $T()", Filter.class)
                .addStatement("var sort = new $T()", Sort.class)
                .addStatement("var paginate = new $T()", SelectBuilder.Paginate.class)
                .addCode("\n")
                .addStatement(
                        "selectBuilder.accept(new $T<>(new $T(filter), new $T(sort), paginate))",
                        SelectBuilder.class,
                        filterBuilderType,
                        sortBuilderType)
                .addCode("\n")
                .addStatement("var queryBuilder = new $T(\"$L\")", StringBuilder.class, query)
                .addStatement("filter.buildQuery(queryBuilder)")
                .addStatement("sort.buildQuery(queryBuilder)")
                .addStatement("paginate.buildQuery(queryBuilder)")
                .addCode("\n")
                .beginControlFlow("try (var stmt = conn.prepareStatement(queryBuilder.toString()))")
                .addStatement("filter.addArguments(1, stmt)")
                .addStatement("var rs = stmt.executeQuery()")
                .beginControlFlow("while (rs.next())")
                .addStatement("results.add(fromResultSet(rs))")
                .endControlFlow()
                .endControlFlow()
                .addCode("\n")
                .addStatement("return results")
                .build();
    }

    private MethodSpec genSelectAll(TypeName entityType, EntitySpec entitySpec) {
        var query = "SELECT " + columnNames(entitySpec) + " FROM " + entitySpec.getTableName();
        return MethodSpec.methodBuilder("selectAll")
                .addJavadoc("{@inheritDoc}")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(listType(entityType))
                .addParameter(Connection.class, "conn")
                .addException(SQLException.class)
                .addStatement("var results = new $T<$T>()", ArrayList.class, entityType)
                .beginControlFlow("try (var stmt = conn.createStatement())")
                .addStatement("var rs = stmt.executeQuery(\"$L\")", query)
                .beginControlFlow("while (rs.next())")
                .addStatement("results.add(fromResultSet(rs))")
                .endControlFlow()
                .endControlFlow()
                .addStatement("return results")
                .build();
    }

    private MethodSpec genCount(EntitySpec entitySpec) {
        var query =
                "SELECT COUNT(" + entitySpec.getIdentityField().getColumnName() + ") FROM " + entitySpec.getTableName();
        return MethodSpec.methodBuilder("count")
                .addJavadoc("{@inheritDoc}")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(long.class)
                .addParameter(Connection.class, "conn")
                .addException(SQLException.class)
                .beginControlFlow("try (var stmt = conn.createStatement())")
                .addStatement("var rs = stmt.executeQuery(\"$L\")", query)
                .beginControlFlow("if (rs.next())")
                .addStatement("return rs.getLong(1)")
                .endControlFlow()
                .endControlFlow()
                .addStatement("return 0")
                .build();
    }

    private MethodSpec genCountFiltered(TypeName filterBuilderType, EntitySpec entitySpec) {
        var query =
                "SELECT COUNT(" + entitySpec.getIdentityField().getColumnName() + ") FROM " + entitySpec.getTableName();
        return MethodSpec.methodBuilder("count")
                .addJavadoc("{@inheritDoc}")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(long.class)
                .addParameter(
                        ParameterizedTypeName.get(ClassName.get(Consumer.class), filterBuilderType), "filterBuilder")
                .addParameter(Connection.class, "conn")
                .addException(SQLException.class)
                .addStatement("var filter = new $T()", Filter.class)
                .addStatement("filterBuilder.accept(new $T(filter))", filterBuilderType)
                .addCode("\n")
                .addStatement("var queryBuilder = new $T(\"$L\")", StringBuilder.class, query)
                .addStatement("filter.buildQuery(queryBuilder)")
                .addCode("\n")
                .beginControlFlow("try (var stmt = conn.prepareStatement(queryBuilder.toString()))")
                .addStatement("filter.addArguments(1, stmt)")
                .addStatement("var rs = stmt.executeQuery()")
                .beginControlFlow("if (rs.next())")
                .addStatement("return rs.getLong(1)")
                .endControlFlow()
                .endControlFlow()
                .addCode("\n")
                .addStatement("return 0")
                .build();
    }

    private MethodSpec genInsert(TypeName entityType, TypeName idType, EntitySpec entitySpec) {
        var builder = MethodSpec.methodBuilder("insert")
                .addJavadoc("{@inheritDoc}")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(idType)
                .addParameter(entityType, "entity")
                .addParameter(Connection.class, "conn")
                .addException(SQLException.class);

        if (entitySpec.getIdentityField().isPrimitive()) {
            builder.addStatement("return insertWithSpecifiedId(entity, conn)");
        } else {
            builder.beginControlFlow("if (entity.$L == null)", fieldAccess(entitySpec.getIdentityField()))
                    .addStatement("return insertWithGeneratedId(entity, conn)")
                    .endControlFlow()
                    .addStatement("return insertWithSpecifiedId(entity, conn)");
        }

        return builder.build();
    }

    private MethodSpec genInsertPatch(TypeName patchType, TypeName idType, EntitySpec entitySpec) {
        var idGetterName = BeanUtil.getterName(entitySpec.getIdentityField().getFieldName(), false);
        return MethodSpec.methodBuilder("insert")
                .addJavadoc("{@inheritDoc}")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(idType)
                .addParameter(patchType, "entity")
                .addParameter(Connection.class, "conn")
                .addException(SQLException.class)
                .beginControlFlow("if (entity.getData().isEmpty())")
                .addStatement("throw new $T($S)", SQLException.class, "No data specified")
                .endControlFlow()
                .addCode("\n")
                .addStatement("boolean generatedId = entity.$N() == null", idGetterName)
                .addStatement("var data = entity.getData()")
                .addStatement("var keys = new ArrayList<>(data.keySet())")
                .addCode("\n")
                .addStatement("var queryBuilder = new StringBuilder(\"INSERT INTO $L (\")", entitySpec.getTableName())
                .addCode("\n")
                .beginControlFlow("for (var it = keys.iterator(); it.hasNext();)")
                .addStatement("queryBuilder.append(it.next())")
                .beginControlFlow("if (it.hasNext())")
                .addStatement("queryBuilder.append(\", \")")
                .endControlFlow()
                .endControlFlow()
                .addCode("\n")
                .addStatement("queryBuilder.append(\") VALUES (\")")
                .addCode("\n")
                .beginControlFlow("if (keys.size() > 1)")
                .addStatement("queryBuilder.append(\"?, \".repeat(keys.size() - 1))")
                .endControlFlow()
                .addStatement("queryBuilder.append(\"?)\")")
                .addCode("\n")
                .addStatement("$T stmt", PreparedStatement.class)
                .beginControlFlow("if (generatedId)")
                .addStatement(
                        "stmt = conn.prepareStatement(queryBuilder.toString(), $T.RETURN_GENERATED_KEYS)",
                        Statement.class)
                .nextControlFlow("else")
                .addStatement("stmt = conn.prepareStatement(queryBuilder.toString())")
                .endControlFlow()
                .addCode("\n")
                .beginControlFlow("try")
                .beginControlFlow("for (int i = 0; i < keys.size(); i++)")
                .addStatement("var value = data.get(keys.get(i))")
                .addStatement("stmt.setObject(i + 1, value)")
                .endControlFlow()
                .addCode("\n")
                .addStatement("stmt.executeUpdate()")
                .addCode("\n")
                .beginControlFlow("if (generatedId)")
                .addStatement("var rs = stmt.getGeneratedKeys()")
                .beginControlFlow("if (rs.next())")
                .addStatement("return $T.getNullableValue(rs, 1, $T.class)", JdbcGenUtil.class, idType)
                .nextControlFlow("else")
                .addStatement("throw new $T($S)", SQLException.class, "Generated id was not returned.")
                .endControlFlow()
                .nextControlFlow("else")
                .addStatement("return entity.$N()", idGetterName)
                .endControlFlow()
                .nextControlFlow("finally")
                .addStatement("stmt.close()")
                .endControlFlow()
                .build();
    }

    private MethodSpec genInsertList(TypeName entityType, TypeName idType, EntitySpec entitySpec) {
        var builder = MethodSpec.methodBuilder("insert")
                .addJavadoc("{@inheritDoc}")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(listType(idType))
                .addParameter(listType(entityType), "entities")
                .addParameter(Connection.class, "conn")
                .addException(SQLException.class);

        if (entitySpec.getIdentityField().isPrimitive()) {
            builder.addStatement("return insertWithSpecifiedId(entities, conn)");
        } else {
            builder.beginControlFlow(
                            "if (!entities.isEmpty() && entities.get(0).$L == null)",
                            fieldAccess(entitySpec.getIdentityField()))
                    .addStatement("return insertWithGeneratedId(entities, conn)")
                    .endControlFlow()
                    .addStatement("return insertWithSpecifiedId(entities, conn)");
        }

        return builder.build();
    }

    private MethodSpec genUpdatePatch(TypeName entityType, EntitySpec entitySpec) {
        return MethodSpec.methodBuilder("update")
                .addJavadoc("{@inheritDoc}")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(int.class)
                .addParameter(entityType, "entity")
                .addParameter(Connection.class, "conn")
                .addException(SQLException.class)
                .beginControlFlow("try (var stmt = conn.prepareStatement(\"$L\"))", buildUpdateQuery(entitySpec))
                .addStatement("prepareUpdate(entity, stmt)")
                .addStatement("return stmt.executeUpdate()")
                .endControlFlow()
                .build();
    }

    private MethodSpec genUpdatePatch(TypeName patchType, TypeName idType, EntitySpec entitySpec) {
        return MethodSpec.methodBuilder("update")
                .addJavadoc("{@inheritDoc}")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(int.class)
                .addParameter(idType, "id")
                .addParameter(patchType, "entity")
                .addParameter(Connection.class, "conn")
                .addException(SQLException.class)
                .beginControlFlow("if (entity.getData().isEmpty())")
                .addStatement("throw new $T($S)", SQLException.class, "No data specified")
                .endControlFlow()
                .addCode("\n")
                .addStatement("var data = entity.getData()")
                .addStatement("var keys = new ArrayList<>(data.keySet())")
                .addCode("\n")
                .addStatement("var queryBuilder = new StringBuilder(\"UPDATE $L SET \")", entitySpec.getTableName())
                .addCode("\n")
                .beginControlFlow("for (var it = keys.iterator(); it.hasNext();)")
                .addStatement("queryBuilder.append(it.next()).append(\" = ?\")")
                .beginControlFlow("if (it.hasNext())")
                .addStatement("queryBuilder.append(\", \")")
                .endControlFlow()
                .endControlFlow()
                .addCode("\n")
                .addStatement(
                        "queryBuilder.append(\" WHERE $L = ?\")",
                        entitySpec.getIdentityField().getColumnName())
                .addCode("\n")
                .beginControlFlow("try (var stmt = conn.prepareStatement(queryBuilder.toString()))")
                .beginControlFlow("for (int i = 0; i < keys.size(); i++)")
                .addStatement("var value = data.get(keys.get(i))")
                .addStatement("stmt.setObject(i + 1, value)")
                .endControlFlow()
                .addStatement("stmt.setObject(keys.size() + 1, id)")
                .addStatement("return stmt.executeUpdate()")
                .endControlFlow()
                .build();
    }

    private MethodSpec genUpdateList(TypeName entityType, EntitySpec entitySpec) {
        return MethodSpec.methodBuilder("update")
                .addJavadoc("{@inheritDoc}")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(int[].class)
                .addParameter(listType(entityType), "entities")
                .addParameter(Connection.class, "conn")
                .addException(SQLException.class)
                .beginControlFlow("try (var stmt = conn.prepareStatement(\"$L\"))", buildUpdateQuery(entitySpec))
                .beginControlFlow("for (var entity : entities)")
                .addStatement("prepareUpdate(entity, stmt)")
                .addStatement("stmt.addBatch()")
                .endControlFlow()
                .addStatement("return stmt.executeBatch()")
                .endControlFlow()
                .build();
    }

    private MethodSpec genDelete(TypeName idType, EntitySpec entitySpec) {
        return MethodSpec.methodBuilder("delete")
                .addJavadoc("{@inheritDoc}")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(int.class)
                .addParameter(idType, "id")
                .addParameter(Connection.class, "conn")
                .addException(SQLException.class)
                .beginControlFlow("try (var stmt = conn.prepareStatement(\"$L\"))", buildDeleteQuery(entitySpec))
                .addStatement("stmt.setObject(1, id)")
                .addStatement("return stmt.executeUpdate()")
                .endControlFlow()
                .build();
    }

    private MethodSpec genDeleteList(TypeName idType, EntitySpec entitySpec) {
        return MethodSpec.methodBuilder("delete")
                .addJavadoc("{@inheritDoc}")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(int[].class)
                .addParameter(listType(idType), "ids")
                .addParameter(Connection.class, "conn")
                .addException(SQLException.class)
                .beginControlFlow("try (var stmt = conn.prepareStatement(\"$L\"))", buildDeleteQuery(entitySpec))
                .beginControlFlow("for (var id : ids)")
                .addStatement("stmt.setObject(1, id)")
                .addStatement("stmt.addBatch()")
                .endControlFlow()
                .addStatement("return stmt.executeBatch()")
                .endControlFlow()
                .build();
    }

    private MethodSpec genDeleteFiltered(TypeName filterBuilderType, EntitySpec entitySpec) {
        return MethodSpec.methodBuilder("delete")
                .addJavadoc("{@inheritDoc}")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(int.class)
                .addParameter(
                        ParameterizedTypeName.get(ClassName.get(Consumer.class), filterBuilderType), "filterBuilder")
                .addParameter(Connection.class, "conn")
                .addException(SQLException.class)
                .addStatement("var filter = new $T()", Filter.class)
                .addStatement("filterBuilder.accept(new $T(filter))", filterBuilderType)
                .addCode("\n")
                .addStatement(
                        "var queryBuilder = new $T(\"DELETE FROM $L\")", StringBuilder.class, entitySpec.getTableName())
                .addStatement("filter.buildQuery(queryBuilder)")
                .addCode("\n")
                .beginControlFlow("try (var stmt = conn.prepareStatement(queryBuilder.toString()))")
                .addStatement("filter.addArguments(1, stmt)")
                .addStatement("return stmt.executeUpdate()")
                .endControlFlow()
                .build();
    }

    private MethodSpec genDeleteAll(EntitySpec entitySpec) {
        return MethodSpec.methodBuilder("deleteAll")
                .addJavadoc("{@inheritDoc}")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(int.class)
                .addParameter(Connection.class, "conn")
                .addException(SQLException.class)
                .beginControlFlow("try (var stmt = conn.createStatement())")
                .addStatement("return stmt.executeUpdate(\"DELETE FROM $L\")", entitySpec.getTableName())
                .endControlFlow()
                .build();
    }

    private MethodSpec genInsertWithGeneratedId(TypeName entityType, TypeName idType, EntitySpec entitySpec) {
        return MethodSpec.methodBuilder("insertWithGeneratedId")
                .addModifiers(PRIVATE)
                .returns(idType)
                .addParameter(entityType, "entity")
                .addParameter(Connection.class, "conn")
                .addException(SQLException.class)
                .beginControlFlow(
                        "try (var stmt = conn.prepareStatement(\"$L\", $T.RETURN_GENERATED_KEYS))",
                        buildInsertQueryWithoutId(entitySpec),
                        Statement.class)
                .addStatement("prepareInsert(entity, stmt)")
                .addStatement("stmt.executeUpdate()")
                .addStatement("var rs = stmt.getGeneratedKeys()")
                .beginControlFlow("if (rs.next())")
                .addStatement("return $T.getNullableValue(rs, 1, $T.class)", JdbcGenUtil.class, idType)
                .nextControlFlow("else")
                .addStatement("throw new $T($S)", SQLException.class, "Generated id was not returned.")
                .endControlFlow()
                .endControlFlow()
                .build();
    }

    private MethodSpec genInsertWithSpecifiedId(TypeName entityType, TypeName idType, EntitySpec entitySpec) {
        return MethodSpec.methodBuilder("insertWithSpecifiedId")
                .addModifiers(PRIVATE)
                .returns(idType)
                .addParameter(entityType, "entity")
                .addParameter(Connection.class, "conn")
                .addException(SQLException.class)
                .beginControlFlow("try (var stmt = conn.prepareStatement(\"$L\"))", buildInsertQueryWithId(entitySpec))
                .addStatement("prepareInsert(entity, stmt)")
                .addStatement("stmt.executeUpdate()")
                .endControlFlow()
                .addStatement("return entity.$L", fieldAccess(entitySpec.getIdentityField()))
                .build();
    }

    private MethodSpec genInsertListWithGeneratedId(TypeName entityType, TypeName idType, EntitySpec entitySpec) {
        return MethodSpec.methodBuilder("insertWithGeneratedId")
                .addModifiers(PRIVATE)
                .returns(listType(idType))
                .addParameter(listType(entityType), "entities")
                .addParameter(Connection.class, "conn")
                .addException(SQLException.class)
                .addStatement("var ids = new ArrayList<$T>()", idType)
                .beginControlFlow(
                        "try (var stmt = conn.prepareStatement(\"$L\", $T.RETURN_GENERATED_KEYS))",
                        buildInsertQueryWithoutId(entitySpec),
                        Statement.class)
                .beginControlFlow("for (var entity : entities)")
                .addStatement("prepareInsert(entity, stmt)")
                .addStatement("stmt.addBatch()")
                .endControlFlow()
                .addStatement("stmt.executeBatch()")
                .addStatement("var rs = stmt.getGeneratedKeys()")
                .beginControlFlow("while (rs.next())")
                .addStatement("ids.add($T.getNullableValue(rs, 1, $T.class))", JdbcGenUtil.class, idType)
                .endControlFlow()
                .endControlFlow()
                .addStatement("return ids")
                .build();
    }

    private MethodSpec genInsertListWithSpecifiedId(TypeName entityType, TypeName idType, EntitySpec entitySpec) {
        return MethodSpec.methodBuilder("insertWithSpecifiedId")
                .addModifiers(PRIVATE)
                .returns(listType(idType))
                .addParameter(listType(entityType), "entities")
                .addParameter(Connection.class, "conn")
                .addException(SQLException.class)
                .beginControlFlow("try (var stmt = conn.prepareStatement(\"$L\"))", buildInsertQueryWithId(entitySpec))
                .beginControlFlow("for (var entity : entities)")
                .addStatement("prepareInsert(entity, stmt)")
                .addStatement("stmt.addBatch()")
                .endControlFlow()
                .addStatement("stmt.executeBatch()")
                .endControlFlow()
                .addStatement(
                        "return entities.stream().map(entity -> entity.$L).collect($T.toList())",
                        fieldAccess(entitySpec.getIdentityField()),
                        Collectors.class)
                .build();
    }

    private MethodSpec genFromResultSet(TypeName entityType, EntitySpec entitySpec) {
        var builder = MethodSpec.methodBuilder("fromResultSet")
                .addModifiers(PRIVATE)
                .returns(entityType)
                .addParameter(ResultSet.class, "rs")
                .addException(SQLException.class)
                .addStatement("int i = 1");

        if (entitySpec.isCanonicalConstructor()) {
            var args = new ArrayList<>();
            var stmtBuilder = new StringBuilder("return new $T(");
            args.add(entityType);

            for (var it = entitySpec.getFields().iterator(); it.hasNext(); ) {
                var fieldSpec = it.next();
                if (fieldSpec.isPrimitive()) {
                    stmtBuilder.append(
                            resultSetPrimitiveGet(fieldSpec.getFieldElement().asType()));
                } else if (fieldSpec.isEnum()) {
                    stmtBuilder.append("$T.enumFromResultSet(rs, i++, $T.class)");
                    args.add(JdbcGenUtil.class);
                    args.add(TypeName.get(fieldSpec.getFieldElement().asType()));
                } else {
                    stmtBuilder.append("$T.getNullableValue(rs, i++, $T.class)");
                    args.add(JdbcGenUtil.class);
                    args.add(TypeName.get(fieldSpec.getFieldElement().asType()));
                }

                if (it.hasNext()) {
                    stmtBuilder.append(", ");
                }
            }

            stmtBuilder.append(")");
            builder.addStatement(stmtBuilder.toString(), args.toArray(new Object[] {}));
        } else {
            builder.addStatement("var entity = new $T()", entityType);

            entitySpec.getFields().forEach(fieldSpec -> {
                var primitive = fieldSpec.isPrimitive();
                var fieldType = TypeName.get(fieldSpec.getFieldElement().asType());

                String getMethod;
                if (primitive) {
                    getMethod =
                            resultSetPrimitiveGet(fieldSpec.getFieldElement().asType());
                } else if (fieldSpec.isEnum()) {
                    getMethod = "$T.enumFromResultSet(rs, i++, $T.class)";
                } else {
                    getMethod = "$T.getNullableValue(rs, i++, $T.class)";
                }

                String statement;
                if (fieldSpec.getSetMethod() == FieldSetMethod.DIRECT) {
                    statement = "entity." + fieldSpec.getFieldName() + " = " + getMethod;

                } else {
                    statement = "entity." + fieldSpec.getSetterName() + "(" + getMethod + ")";
                }

                if (primitive) {
                    builder.addStatement(statement);
                } else {
                    builder.addStatement(statement, JdbcGenUtil.class, fieldType);
                }
            });

            builder.addStatement("return entity");
        }

        return builder.build();
    }

    private MethodSpec genPrepareInsert(TypeName entityType, EntitySpec entitySpec) {
        var builder = MethodSpec.methodBuilder("prepareInsert")
                .addModifiers(PRIVATE)
                .returns(void.class)
                .addParameter(entityType, "entity")
                .addParameter(PreparedStatement.class, "stmt")
                .addException(SQLException.class)
                .addStatement("int i = 1");

        if (entitySpec.getIdentityField().isPrimitive()) {
            builder.addStatement("stmt.setObject(i++, entity.$L)", fieldAccess(entitySpec.getIdentityField()));
        } else {
            builder.beginControlFlow("if (entity.$L != null)", fieldAccess(entitySpec.getIdentityField()))
                    .addStatement("stmt.setObject(i++, entity.$L)", fieldAccess(entitySpec.getIdentityField()))
                    .endControlFlow();
        }

        entitySpec.getFields().stream()
                .filter(fieldSpec -> !fieldSpec.isIdentity())
                .forEach(fieldSpec -> {
                    if (fieldSpec.isEnum()) {
                        builder.addStatement(
                                "stmt.setObject(i++, $T.enumToString(entity.$L))",
                                JdbcGenUtil.class,
                                fieldAccess(fieldSpec));
                    } else {
                        builder.addStatement("stmt.setObject(i++, entity.$L)", fieldAccess(fieldSpec));
                    }
                });

        return builder.build();
    }

    private MethodSpec genPrepareUpdate(TypeName entityType, EntitySpec entitySpec) {
        var builder = MethodSpec.methodBuilder("prepareUpdate")
                .addModifiers(PRIVATE)
                .returns(void.class)
                .addParameter(entityType, "entity")
                .addParameter(PreparedStatement.class, "stmt")
                .addException(SQLException.class)
                .addStatement("int i = 1");

        entitySpec.getFields().stream()
                .filter(fieldSpec -> !fieldSpec.isIdentity())
                .forEach(fieldSpec -> {
                    if (fieldSpec.isEnum()) {
                        builder.addStatement(
                                "stmt.setObject(i++, $T.enumToString(entity.$L))",
                                JdbcGenUtil.class,
                                fieldAccess(fieldSpec));
                    } else {
                        builder.addStatement("stmt.setObject(i++, entity.$L)", fieldAccess(fieldSpec));
                    }
                });

        builder.addStatement("stmt.setObject(i++, entity.$L)", fieldAccess(entitySpec.getIdentityField()));

        return builder.build();
    }

    private AnnotationSpec generatedAnnotation() {
        return AnnotationSpec.builder(Generated.class)
                .addMember("value", "$S", JdbcGenProcessor.class.getName())
                .addMember(
                        "date",
                        "$S",
                        OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS).toString())
                .build();
    }

    private String columnNames(EntitySpec entitySpec) {
        return entitySpec.getFields().stream().map(FieldSpec::getColumnName).collect(Collectors.joining(", "));
    }

    private String columnNamesWithoutId(EntitySpec entitySpec) {
        return entitySpec.getFields().stream()
                .filter(fieldSpec -> !fieldSpec.isIdentity())
                .map(FieldSpec::getColumnName)
                .collect(Collectors.joining(", "));
    }

    private String genPlaceholders(int count) {
        var builder = new StringBuilder();
        if (count > 1) {
            builder.append("?, ".repeat(count - 1));
        }
        builder.append("?");
        return builder.toString();
    }

    private String fieldAccess(FieldSpec column) {
        if (column.getGetMethod() == FieldGetMethod.DIRECT) {
            return column.getFieldName();
        }
        return column.getGetterName() + "()";
    }

    private String resultSetPrimitiveGet(TypeMirror type) {
        switch (type.getKind()) {
            case LONG:
                return "rs.getLong(i++)";
            case BOOLEAN:
                return "rs.getBoolean(i++)";
            case INT:
                return "rs.getInt(i++)";
            case DOUBLE:
                return "rs.getDouble(i++)";
            case FLOAT:
                return "rs.getFloat(i++)";
            case SHORT:
                return "rs.getShort(i++)";
            case BYTE:
                return "rs.getByte(i++)";
            case ARRAY:
                var component = ((ArrayType) type).getComponentType();
                if (component.getKind() == TypeKind.BYTE) {
                    return "rs.getBytes(i++)";
                }
                throw new RuntimeException(
                        "Byte arrays are the only array type that is currently supported. Found type: " + component);
            default:
                throw new RuntimeException("Unmapped type: " + type);
        }
    }

    private String toEnumCase(String name) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name);
    }

    private ParameterizedTypeName listType(TypeName typeName) {
        return ParameterizedTypeName.get(ClassName.get(List.class), typeName);
    }

    private String buildInsertQueryWithId(EntitySpec entitySpec) {
        var columnNames = columnNames(entitySpec);
        return "INSERT INTO " + entitySpec.getTableName() + " (" + columnNames + ") VALUES ("
                + genPlaceholders(entitySpec.getFields().size()) + ")";
    }

    private String buildInsertQueryWithoutId(EntitySpec entitySpec) {
        var columnNames = columnNamesWithoutId(entitySpec);
        return "INSERT INTO " + entitySpec.getTableName() + " (" + columnNames + ") VALUES ("
                + genPlaceholders(entitySpec.getFields().size() - 1) + ")";
    }

    private String buildUpdateQuery(EntitySpec entitySpec) {
        var queryBuilder =
                new StringBuilder("UPDATE ").append(entitySpec.getTableName()).append(" SET ");

        entitySpec.getFields().stream()
                .filter(fieldSpec -> !fieldSpec.isIdentity())
                .forEach(fieldSpec ->
                        queryBuilder.append(fieldSpec.getColumnName()).append(" = ?, "));

        queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length());

        queryBuilder
                .append(" WHERE ")
                .append(entitySpec.getIdentityField().getColumnName())
                .append(" = ?");

        return queryBuilder.toString();
    }

    private String buildDeleteQuery(EntitySpec entitySpec) {
        return "DELETE FROM " + entitySpec.getTableName() + " WHERE "
                + entitySpec.getIdentityField().getColumnName() + " = ?";
    }

    private ParameterizedTypeName mapFieldTypeToPredicateBuilderType(FieldSpec fieldSpec, TypeName filterBuilderType) {
        var fieldType = fieldSpec.getFieldElement().asType();

        if (fieldType.getKind().isPrimitive()) {
            switch (fieldType.getKind()) {
                case LONG:
                    return ParameterizedTypeName.get(ClassName.get(LongPredicateBuilder.class), filterBuilderType);
                case INT:
                    return ParameterizedTypeName.get(ClassName.get(IntPredicateBuilder.class), filterBuilderType);
                case SHORT:
                    return ParameterizedTypeName.get(ClassName.get(ShortPredicateBuilder.class), filterBuilderType);
                case BOOLEAN:
                    return ParameterizedTypeName.get(
                            ClassName.get(PrimitiveBooleanPredicateBuilder.class), filterBuilderType);
                case DOUBLE:
                    return ParameterizedTypeName.get(ClassName.get(DoublePredicateBuilder.class), filterBuilderType);
                case FLOAT:
                    return ParameterizedTypeName.get(ClassName.get(FloatPredicateBuilder.class), filterBuilderType);
                case BYTE:
                case ARRAY:
                default:
                    // We don't support filtering on these types
                    return null;
            }
        } else {
            var fieldTypeName = TypeName.get(fieldType);

            if (fieldSpec.isEnum()) {
                return ParameterizedTypeName.get(
                        ClassName.get(EnumPredicateBuilder.class), filterBuilderType, fieldTypeName);
            } else if (fieldTypeName.equals(TypeName.get(String.class))) {
                return ParameterizedTypeName.get(ClassName.get(StringPredicateBuilder.class), filterBuilderType);
            } else if (fieldTypeName.equals(TypeName.get(Boolean.class))) {
                return ParameterizedTypeName.get(ClassName.get(BooleanPredicateBuilder.class), filterBuilderType);
            } else {
                return ParameterizedTypeName.get(
                        ClassName.get(ObjectPredicateBuilder.class), filterBuilderType, fieldTypeName);
            }
        }
    }
}
