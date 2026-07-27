package com.malta.plugin.analyzer;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.util.Query;
import com.malta.plugin.model.CallNode;
import com.malta.plugin.model.TableAccess;
import com.malta.plugin.resolver.OperationResolver;
import com.malta.plugin.resolver.RepositoryResolver;
import com.malta.plugin.resolver.TableResolver;
import com.malta.plugin.util.OperationType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AffectedTableAnalyzer {

    private List<TableAccess> tableAccesses = new ArrayList<>();

    private final RepositoryResolver repositoryResolver =
            new RepositoryResolver();

    private final TableResolver tableResolver =
            new TableResolver();

    private final OperationResolver operationResolver =
            new OperationResolver();

    private final Set<PsiMethod> visited = new HashSet<>();


    public List<TableAccess> analyze(PsiMethod method) {

        if (!visited.add(method)) {
            return tableAccesses;
        }

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

                collectTableAccess(
                        method,
                        calledMethod,
                        expression
                );
                analyze(calledMethod);
            }
        });

        return tableAccesses;
    }

    private void collectTableAccess(
            PsiMethod callerMethod,
            PsiMethod calledMethod,
            PsiMethodCallExpression expression) {

        PsiClass repository = calledMethod.getContainingClass();

        if (repository == null || !repositoryResolver.isRepository(repository)) {
            return;
        }

        String tableName = tableResolver.resolve(repository);

        if (tableName == null || tableName.isBlank()) {
            return;
        }

        OperationType operation =
                operationResolver.resolve(calledMethod);

        if (operation == null) {
            return;
        }

        tableAccesses.add(
                new TableAccess(
                        tableName,
                        operation,
                        callerMethod,
                        expression
                )
        );
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
