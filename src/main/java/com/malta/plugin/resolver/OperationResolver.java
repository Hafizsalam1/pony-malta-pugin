package com.malta.plugin.resolver;

import com.intellij.psi.PsiMethod;
import com.malta.plugin.util.OperationType;

public class OperationResolver {

    public OperationType resolve(PsiMethod method) {

        if (method == null) {
            return null;
        }

        String name = method.getName().toLowerCase();

        // READ
        if (name.startsWith("find")
                || name.startsWith("get")
                || name.startsWith("load")
                || name.startsWith("search")
                || name.startsWith("exists")
                || name.startsWith("count")) {

            return OperationType.READ;
        }

        // INSERT
        if (name.startsWith("insert")
                || name.startsWith("persist")
                || name.startsWith("create")
                || name.startsWith("add")
                || name.startsWith("save")
        ) {

            return OperationType.INSERT;
        }

        // UPDATE
        if (name.startsWith("update")
                || name.startsWith("merge")
                || name.startsWith("edit")
                || name.startsWith("modify")) {

            return OperationType.UPDATE;
        }

        // DELETE
        if (name.startsWith("delete")
                || name.startsWith("remove")) {

            return OperationType.DELETE;
        }

        return null;
    }


}
