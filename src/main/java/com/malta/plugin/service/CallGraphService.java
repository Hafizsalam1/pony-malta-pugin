package com.malta.plugin.service;

import com.intellij.psi.PsiMethod;
import com.malta.plugin.analyzer.MethodAnalyzer;
import com.malta.plugin.model.CallNode;

public class CallGraphService {
    public CallNode analyze(PsiMethod method) {
        return new MethodAnalyzer().analyze(method);
    }
}
