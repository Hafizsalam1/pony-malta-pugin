package com.malta.plugin.resolver;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;

public class RepositoryResolver {

    public boolean isRepository(PsiClass psiClass) {

        if (psiClass == null) {
            return false;
        }

        for (PsiClassType type : psiClass.getExtendsListTypes()) {

            String name = type.getClassName();

            if ("JpaRepository".equals(name)
                    || "CrudRepository".equals(name)
                    || "PagingAndSortingRepository".equals(name)) {

                return true;
            }
        }

        return false;
    }


}
