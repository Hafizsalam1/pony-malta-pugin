package com.malta.plugin.resolver;

import com.intellij.psi.*;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class TableResolver {

    @Nullable
    public String resolve(PsiClass repositoryClass) {
        return resolve(repositoryClass, new HashSet<>());
    }

    @Nullable
    private String resolve(PsiClass repositoryClass, Set<String> visited) {

        if (repositoryClass == null) {
            return null;
        }

        String qualifiedName = repositoryClass.getQualifiedName();

        if (qualifiedName != null && !visited.add(qualifiedName)) {
            return null;
        }

        for (PsiClassType type : repositoryClass.getExtendsListTypes()) {
            PsiType[] parameters = type.getParameters();

            if (parameters.length > 0) {
                PsiClass entity = resolveEntity(parameters[0]);

                if (entity != null) {
                    String table = resolveTableName(entity);

                    if (table != null) {
                        return table;
                    }
                }
            }

            PsiClass resolved = type.resolve();

            if (resolved != null) {
                String table = resolve(resolved, visited);

                if (table != null) {
                    return table;
                }
            }
        }

        return null;
    }

    private PsiClass resolveEntity(PsiType type) {
        if (type instanceof PsiClassType classType) {
            return classType.resolve();
        }

        return null;
    }

    private String resolveTableName(PsiClass entity) {

        PsiAnnotation table =
                entity.getAnnotation("jakarta.persistence.Table");

        if (table == null) {
            table =
                    entity.getAnnotation("javax.persistence.Table");
        }

        if (table == null) {
            return entity.getName();
        }

        PsiAnnotationMemberValue value =
                table.findAttributeValue("name");

        if (value instanceof PsiLiteralExpression literal) {

            Object obj = literal.getValue();

            if (obj != null) {
                return obj.toString();
            }
        }

        return entity.getName();
    }

}
