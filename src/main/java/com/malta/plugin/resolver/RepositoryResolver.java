package com.malta.plugin.resolver;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;

import java.util.HashSet;
import java.util.Set;

public class RepositoryResolver {

    public boolean isRepository(PsiClass psiClass) {
        return isRepository(psiClass, new HashSet<>());
    }

    private boolean isRepository(PsiClass psiClass, Set<String> visited) {

        if (psiClass == null) {
            return false;
        }

        String qualifiedName = psiClass.getQualifiedName();

        if (qualifiedName != null && !visited.add(qualifiedName)) {
            return false;
        }

        if (psiClass.hasAnnotation("org.springframework.stereotype.Repository")
                || isRepositoryName(psiClass.getName())) {
            return true;
        }

        for (PsiClassType type : psiClass.getExtendsListTypes()) {
            String name = type.getClassName();

            if (isRepositoryName(name)) {
                return true;
            }

            PsiClass resolved = type.resolve();

            if (resolved != null && isRepository(resolved, visited)) {
                return true;
            }
        }

        return false;
    }

    private boolean isRepositoryName(String name) {
        return "JpaRepository".equals(name)
                || "CrudRepository".equals(name)
                || "PagingAndSortingRepository".equals(name)
                || "Repository".equals(name)
                || (name != null && name.endsWith("Repository"));
    }
}
