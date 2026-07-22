package com.malta.plugin.resolver;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;

public class MethodResolver {


    public static PsiMethod findMethod(AnActionEvent e){

        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);

        if(editor == null || file == null){
            return null;
        }

        PsiElement element =
                file.findElementAt(editor.getCaretModel().getOffset());

        return PsiTreeUtil.getParentOfType(
                element,
                PsiMethod.class
        );
    }



}
