package com.pwinckles.jdbcgen.processor;

import io.toolisticon.cute.CompileTestBuilder;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class JdbcGenProcessorTest {

    private static Stream<Arguments> invalidTests() {
        return Stream.of(
                Arguments.of(
                        "MissingTableAnnotation",
                        "com.pwinckles.jdbcgen.processor.invalid.MissingTableAnnotation must have exactly one @JdbcGenTable annotation."),
                Arguments.of(
                        "MissingColumnAnnotation",
                        "com.pwinckles.jdbcgen.processor.invalid.MissingColumnAnnotation must have at least one field annotated with @JdbcGenColumn."),
                Arguments.of(
                        "MissingIdentityAnnotation",
                        "com.pwinckles.jdbcgen.processor.invalid.MissingIdentityAnnotation must have exactly one field annotated with @JdbcGenColumn(identity = true)."),
                Arguments.of(
                        "NonStaticInnerClass",
                        "com.pwinckles.jdbcgen.processor.invalid.NonStaticInnerClass.Inner must be static."),
                Arguments.of(
                        "PrivateClass",
                        "com.pwinckles.jdbcgen.processor.invalid.PrivateClass.Inner must not be private."),
                Arguments.of(
                        "AbstractClass", "com.pwinckles.jdbcgen.processor.invalid.AbstractClass must not be abstract."),
                Arguments.of(
                        "GenericClass",
                        "com.pwinckles.jdbcgen.processor.invalid.GenericClass must not have type parameters."),
                Arguments.of(
                        "MissingGetter",
                        "com.pwinckles.jdbcgen.processor.invalid.MissingGetter.id must either be non-private or have a non-private getter."),
                Arguments.of(
                        "MissingSetter",
                        "com.pwinckles.jdbcgen.processor.invalid.MissingSetter.id must be writable by one of the following, non-private mechanisms: canonical constructor, setter, or non-final field."),
                Arguments.of(
                        "GetterWrongType",
                        "com.pwinckles.jdbcgen.processor.invalid.GetterWrongType.id must either be non-private or have a non-private getter."),
                Arguments.of(
                        "SetterWrongType",
                        "com.pwinckles.jdbcgen.processor.invalid.SetterWrongType.id must be writable by one of the following, non-private mechanisms: canonical constructor, setter, or non-final field."),
                Arguments.of(
                        "PrivateGetter",
                        "com.pwinckles.jdbcgen.processor.invalid.PrivateGetter.id must either be non-private or have a non-private getter."),
                Arguments.of(
                        "PrivateSetter",
                        "com.pwinckles.jdbcgen.processor.invalid.PrivateSetter.id must be writable by one of the following, non-private mechanisms: canonical constructor, setter, or non-final field."),
                Arguments.of(
                        "PrivateConstructor",
                        "com.pwinckles.jdbcgen.processor.invalid.PrivateConstructor must hava non-private default constructor or canonical constructor."),
                Arguments.of(
                        "Collision",
                        "com.pwinckles.jdbcgen.processor.invalid.Collision.First and com.pwinckles.jdbcgen.processor.invalid.Collision.Second both generate com.pwinckles.jdbcgen.processor.invalid.FirstDb. Either rename one of the classes or set the @JdbcGen(name) attribute to change the name of the generated class."));
    }

    @ParameterizedTest
    @MethodSource("invalidTests")
    public void executeInvalidTest(String className, String errorMessage) {
        CompileTestBuilder.compilationTest()
                .addProcessors(JdbcGenProcessor.class)
                .addSources("invalid/" + className + ".java")
                .compilationShouldFail()
                .expectErrorMessageThatContains(errorMessage)
                .executeTest();
    }
}
