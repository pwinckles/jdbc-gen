package com.pwinckles.jdbcgen.processor;

import com.pwinckles.jdbcgen.JdbcGen;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes({"com.pwinckles.jdbcgen.JdbcGen"})
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class JdbcGenProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            var entityAnalyzer = new EntityAnalyzer(processingEnv);
            var dbClassGenerator = new DbClassGenerator();

            var entitySpecs = roundEnv.getElementsAnnotatedWith(JdbcGen.class).stream()
                    .map(TypeElement.class::cast)
                    .map(entityAnalyzer::analyze)
                    .collect(Collectors.toList());

            validateGeneratedNames(entitySpecs);

            for (var entitySpec : entitySpecs) {
                var javaFile = dbClassGenerator.generate(entitySpec);
                javaFile.writeTo(processingEnv.getFiler());
            }
        } catch (Exception e) {
            var message = e.getMessage();
            if (message == null) {
                message = "JdbcGen failed with exception: " + e;
            }
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message);
        }
        return true;
    }

    private void validateGeneratedNames(List<EntitySpec> entitySpecs) {
        var seenFqns = new HashMap<String, EntitySpec>();

        for (var entitySpec : entitySpecs) {
            var fqn = entitySpec.getPackageName() + "." + entitySpec.getDbClassName();
            var existing = seenFqns.get(fqn);
            if (existing != null) {
                throw new RuntimeException(
                        existing.getTypeElement().getQualifiedName()
                                + " and " + entitySpec.getTypeElement().getQualifiedName() + " both generate "
                                + fqn
                                + ". Either rename one of the classes or set the @JdbcGen(name) attribute to change the name of the generated class.");
            } else {
                seenFqns.put(fqn, entitySpec);
            }
        }
    }
}
