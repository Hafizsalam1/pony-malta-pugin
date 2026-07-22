package com.malta.plugin.analyzer;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.util.Query;
import com.malta.plugin.model.CallNode;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class MethodAnalyzer {

    private Set<PsiMethod> visited = new HashSet<>();

    public CallNode analyze(PsiMethod method) {

        if (visited.contains(method)) {
            return null;
        }

        visited.add(method);

        CallNode currentNode = new CallNode(method);

        method.accept(new JavaRecursiveElementVisitor() {

            @Override
            public void visitMethodCallExpression(
                    @NotNull PsiMethodCallExpression expression) {

                super.visitMethodCallExpression(expression);

                PsiMethod calledMethod = expression.resolveMethod();

                if (calledMethod == null) {
                    return;
                }

                calledMethod = resolveImplementation(calledMethod);

                CallNode child = analyze(calledMethod);

                if (child != null) {
                    currentNode.addChild(child);
                }
            }
        });

        return currentNode;
    }

    private PsiMethod resolveImplementation(PsiMethod method) {

        PsiClass psiClass = method.getContainingClass();

        if (psiClass == null || !psiClass.isInterface()) {
            return method;
        }

        Query<PsiClass> implementations =
                ClassInheritorsSearch.search(psiClass);

        for (PsiClass impl : implementations) {

            PsiMethod[] methods =
                    impl.findMethodsByName(method.getName(), true);

            if (methods.length > 0) {
                return methods[0];
            }
        }

        return method;
    }

}
