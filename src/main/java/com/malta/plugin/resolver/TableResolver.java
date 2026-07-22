package com.malta.plugin.resolver;

import com.intellij.psi.*;
import org.jetbrains.annotations.Nullable;

public class TableResolver {

    @Nullable
    public String resolve(PsiClass repositoryClass) {

        if (repositoryClass == null) {
            return null;
        }

        for (PsiClassType type : repositoryClass.getExtendsListTypes()) {

            PsiType[] parameters = type.getParameters();

            if (parameters.length == 0) {
                continue;
            }

            PsiClass entity =
                    ((PsiClassType) parameters[0]).resolve();

            if (entity == null) {
                continue;
            }

            String table = resolveTableName(entity);

            if (table != null) {
                return table;
            }
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
