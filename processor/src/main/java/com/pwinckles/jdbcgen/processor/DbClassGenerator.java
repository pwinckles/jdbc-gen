package com.pwinckles.jdbcgen.processor;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

import com.google.common.base.CaseFormat;
import com.pwinckles.jdbcgen.BasePatch;
import com.pwinckles.jdbcgen.JdbcGenDb;
import com.pwinckles.jdbcgen.OrderDirection;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
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
        var columnType = ClassName.get(entitySpec.getPackageName(), entitySpec.getDbClassName(), "Column");

        var builder = TypeSpec.classBuilder(entitySpec.getDbClassName())
                .addSuperinterface(ParameterizedTypeName.get(
                        ClassName.get(JdbcGenDb.class), entityType, idType, patchType, columnType))
                .addJavadoc("Provides basic DB access for {@link $T} entities.", entityType);

        if (entitySpec.getTypeElement().getModifiers().contains(PUBLIC)) {
            builder.addModifiers(PUBLIC);
        } else if (entitySpec.getTypeElement().getModifiers().contains(PROTECTED)) {
            builder.addModifiers(PROTECTED);
        }

        builder.addType(genColumnsEnum(entityType, entitySpec))
                .addType(genPatchClass(entityType, patchType, idType, entitySpec))
                .addMethod(genSelect(idType, entitySpec))
                .addMethod(genSelectAll(entityType, entitySpec))
                .addMethod(genSelectAllOrdered(entityType, columnType, entitySpec))
                .addMethod(genCount(entitySpec))
                .addMethod(genInsert(entityType, idType, entitySpec))
                .addMethod(genInsertPatch(patchType, idType, entitySpec))
                .addMethod(genInsertList(entityType, idType, entitySpec))
                .addMethod(genUpdatePatch(entityType, entitySpec))
                .addMethod(genUpdatePatch(patchType, idType, entitySpec))
                .addMethod(genUpdateList(entityType, entitySpec))
                .addMethod(genDelete(idType, entitySpec))
                .addMethod(genDeleteList(idType, entitySpec))
                .addMethod(genDeleteAll(entitySpec))
                .addMethod(genInsertWithSpecifiedId(entityType, idType, entitySpec))
                .addMethod(genInsertListWithSpecifiedId(entityType, idType, entitySpec))
                .addMethod(genFromResultSet(entityType, entitySpec))
                .addMethod(genPrepareInsert(entityType, entitySpec))
                .addMethod(genPrepareUpdate(entityType, entitySpec))
                .addMethod(genGetNullableValue());

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

    private TypeSpec genColumnsEnum(TypeName entityType, EntitySpec entitySpec) {
        var builder = TypeSpec.enumBuilder("Column")
                .addModifiers(PUBLIC)
                .addJavadoc("The database columns associated to {@link $T}", entityType);

        entitySpec
                .getFields()
                .forEach(column -> builder.addEnumConstant(
                        toEnumCase(column.getFieldName()),
                        TypeSpec.anonymousClassBuilder("$S", column.getColumnName())
                                .build()));

        builder.addField(String.class, "value", PRIVATE, FINAL)
                .addMethod(MethodSpec.constructorBuilder()
                        .addParameter(String.class, "value")
                        .addStatement("this.value = value")
                        .build());

        return builder.build();
    }

    private TypeSpec genPatchClass(TypeName entityType, TypeName patchType, TypeName idType, EntitySpec entitySpec) {
        var builder = TypeSpec.classBuilder("Patch")
                .addModifiers(PUBLIC, STATIC)
                .superclass(BasePatch.class)
                .addJavadoc(
                        "Patch implementation that enables partial updates for {@link $T} entities."
                                + " Only the fields that are set this object will be modified in the DB.",
                        entityType);

        var idFieldName = entitySpec.getIdentityField().getFieldName();

        builder.addMethod(MethodSpec.methodBuilder(BeanUtil.getterName(idFieldName, false))
                .addModifiers(PUBLIC)
                .returns(idType)
                .addStatement(
                        "return ($T) getData().get($S)",
                        idType,
                        entitySpec.getIdentityField().getColumnName())
                .build());

        entitySpec.getFields().forEach(column -> {
            var field = column.getFieldElement();
            var fieldName = field.getSimpleName().toString();
            builder.addMethod(MethodSpec.methodBuilder(BeanUtil.setterName(fieldName))
                    .addModifiers(PUBLIC)
                    .returns(patchType)
                    .addParameter(TypeName.get(field.asType()), fieldName)
                    .addStatement("put($S, $N)", column.getColumnName(), fieldName)
                    .addStatement("return this")
                    .build());
        });

        return builder.build();
    }

    private MethodSpec genSelect(TypeName idType, EntitySpec entitySpec) {
        var query = "SELECT " + columnNames(entitySpec)
                + " FROM " + entitySpec.getTableName()
                + " WHERE " + entitySpec.getIdentityField().getColumnName() + " = ? LIMIT 1";
        return MethodSpec.methodBuilder("select")
                .addJavadoc("{@inheritDoc}")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(TypeName.get(entitySpec.getTypeElement().asType()))
                .addParameter(idType, "id")
                .addParameter(Connection.class, "conn")
                .addException(SQLException.class)
                .beginControlFlow("try (var stmt = conn.prepareStatement($S))", query)
                .addStatement("stmt.setObject(1, id)")
                .addStatement("var rs = stmt.executeQuery()")
                .beginControlFlow("if (rs.next())")
                .addStatement("return fromResultSet(rs)")
                .endControlFlow()
                .endControlFlow()
                .addStatement("return null")
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
                .addStatement("var rs = stmt.executeQuery($S)", query)
                .beginControlFlow("while (rs.next())")
                .addStatement("results.add(fromResultSet(rs))")
                .endControlFlow()
                .endControlFlow()
                .addStatement("return results")
                .build();
    }

    private MethodSpec genSelectAllOrdered(TypeName entityType, TypeName columnType, EntitySpec entitySpec) {
        var query = "SELECT " + columnNames(entitySpec) + " FROM " + entitySpec.getTableName() + " ORDER BY ";
        return MethodSpec.methodBuilder("selectAll")
                .addJavadoc("{@inheritDoc}")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(listType(entityType))
                .addParameter(columnType, "orderBy")
                .addParameter(OrderDirection.class, "direction")
                .addParameter(Connection.class, "conn")
                .addException(SQLException.class)
                .addStatement("var results = new $T<$T>()", ArrayList.class, entityType)
                .beginControlFlow("try (var stmt = conn.createStatement())")
                .addStatement("var rs = stmt.executeQuery($S + orderBy.value + \" \" + direction.getValue())", query)
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
                .addStatement("var rs = stmt.executeQuery($S)", query)
                .beginControlFlow("if (rs.next())")
                .addStatement("return rs.getLong(1)")
                .endControlFlow()
                .endControlFlow()
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
                .addStatement(
                        "var queryBuilder = new StringBuilder($S)", "INSERT INTO " + entitySpec.getTableName() + "(")
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
                .addStatement("return getNullableValue(rs, 1, $T.class)", idType)
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
                .beginControlFlow("try (var stmt = conn.prepareStatement($S))", buildUpdateQuery(entitySpec))
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
                .beginControlFlow("try (var stmt = conn.prepareStatement($S))", buildUpdateQuery(entitySpec))
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
                .beginControlFlow("try (var stmt = conn.prepareStatement($S))", buildDeleteQuery(entitySpec))
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
                .beginControlFlow("try (var stmt = conn.prepareStatement($S))", buildDeleteQuery(entitySpec))
                .beginControlFlow("for (var id : ids)")
                .addStatement("stmt.setObject(1, id)")
                .addStatement("stmt.addBatch()")
                .endControlFlow()
                .addStatement("return stmt.executeBatch()")
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
                        "try (var stmt = conn.prepareStatement($S, $T.RETURN_GENERATED_KEYS))",
                        buildInsertQueryWithoutId(entitySpec),
                        Statement.class)
                .addStatement("prepareInsert(entity, stmt)")
                .addStatement("stmt.executeUpdate()")
                .addStatement("var rs = stmt.getGeneratedKeys()")
                .beginControlFlow("if (rs.next())")
                .addStatement("return getNullableValue(rs, 1, $T.class)", idType)
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
                .beginControlFlow("try (var stmt = conn.prepareStatement($S))", buildInsertQueryWithId(entitySpec))
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
                        "try (var stmt = conn.prepareStatement($S, $T.RETURN_GENERATED_KEYS))",
                        buildInsertQueryWithoutId(entitySpec),
                        Statement.class)
                .beginControlFlow("for (var entity : entities)")
                .addStatement("prepareInsert(entity, stmt)")
                .addStatement("stmt.addBatch()")
                .endControlFlow()
                .addStatement("stmt.executeBatch()")
                .addStatement("var rs = stmt.getGeneratedKeys()")
                .beginControlFlow("while (rs.next())")
                .addStatement("ids.add(getNullableValue(rs, 1, $T.class))", idType)
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
                .beginControlFlow("try (var stmt = conn.prepareStatement($S))", buildInsertQueryWithId(entitySpec))
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
                var column = it.next();
                if (column.isPrimitive()) {
                    stmtBuilder.append(
                            resultSetPrimitiveGet(column.getFieldElement().asType()));
                } else {
                    stmtBuilder.append("getNullableValue(rs, i++, $T.class)");
                    args.add(TypeName.get(column.getFieldElement().asType()));
                }

                if (it.hasNext()) {
                    stmtBuilder.append(", ");
                }
            }

            stmtBuilder.append(")");
            builder.addStatement(stmtBuilder.toString(), args.toArray(new Object[] {}));
        } else {
            builder.addStatement("var entity = new $T()", entityType);

            entitySpec.getFields().forEach(column -> {
                var primitive = column.isPrimitive();
                var fieldType = TypeName.get(column.getFieldElement().asType());

                String getMethod;
                if (primitive) {
                    getMethod = resultSetPrimitiveGet(column.getFieldElement().asType());
                } else {
                    getMethod = "getNullableValue(rs, i++, $T.class)";
                }

                String statement;
                if (column.getSetMethod() == FieldSetMethod.DIRECT) {
                    statement = "entity." + column.getFieldName() + " = " + getMethod;

                } else {
                    statement = "entity." + column.getSetterName() + "(" + getMethod + ")";
                }

                if (primitive) {
                    builder.addStatement(statement);
                } else {
                    builder.addStatement(statement, fieldType);
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

        entitySpec.getFields().stream().filter(column -> !column.isIdentity()).forEach(column -> {
            builder.addStatement("stmt.setObject(i++, entity.$L)", fieldAccess(column));
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

        entitySpec.getFields().stream().filter(column -> !column.isIdentity()).forEach(column -> {
            builder.addStatement("stmt.setObject(i++, entity.$L)", fieldAccess(column));
        });

        builder.addStatement("stmt.setObject(i++, entity.$L)", fieldAccess(entitySpec.getIdentityField()));

        return builder.build();
    }

    private MethodSpec genGetNullableValue() {
        var typeVar = TypeVariableName.get("T");
        return MethodSpec.methodBuilder("getNullableValue")
                .addModifiers(PRIVATE)
                .addTypeVariable(typeVar)
                .returns(typeVar)
                .addParameter(ResultSet.class, "rs")
                .addParameter(int.class, "index")
                .addParameter(ParameterizedTypeName.get(ClassName.get(Class.class), typeVar), "clazz")
                .addException(SQLException.class)
                .addStatement("var value = rs.getObject(index, clazz)")
                .beginControlFlow("if (rs.wasNull())")
                .addStatement("return null")
                .endControlFlow()
                .addStatement("return value")
                .build();
    }

    private String columnNames(EntitySpec entitySpec) {
        return entitySpec.getFields().stream().map(FieldSpec::getColumnName).collect(Collectors.joining(", "));
    }

    private String columnNamesWithoutId(EntitySpec entitySpec) {
        return entitySpec.getFields().stream()
                .filter(column -> !column.isIdentity())
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
                .filter(column -> !column.isIdentity())
                .forEach(column -> queryBuilder.append(column.getColumnName()).append(" = ?, "));

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
}
