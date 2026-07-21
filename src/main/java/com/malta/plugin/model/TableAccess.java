package com.malta.plugin.model;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.malta.plugin.util.OperationType;

public class TableAccess {
    private final String tableName;
    private final OperationType operation;
    private final PsiMethod callerMethod;
    private final PsiMethodCallExpression callExpression;

    public TableAccess(
            String tableName,
            OperationType operation,
            PsiMethod callerMethod,
            PsiMethodCallExpression callExpression) {

        this.tableName = tableName;
        this.operation = operation;
        this.callerMethod = callerMethod;
        this.callExpression = callExpression;
    }

    public String getTableName() {
        return tableName;
    }

    public OperationType getOperation() {
        return operation;
    }

    public PsiMethod getCallerMethod() {
        return callerMethod;
    }

    public PsiMethodCallExpression getCallExpression() {
        return callExpression;
    }

}
