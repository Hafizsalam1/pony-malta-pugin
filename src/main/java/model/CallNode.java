package model;

import com.intellij.psi.PsiMethod;
import resolver.LayerResolver;
import util.LayerType;

import java.util.ArrayList;
import java.util.List;

public class CallNode {
    private PsiMethod method;

    private LayerType layer;

    private List<CallNode> children = new ArrayList<>();

    public CallNode(PsiMethod method) {
        this.method = method;
    }

    public PsiMethod getMethod() {
        return method;
    }

    public List<CallNode> getChildren() {
        return children;
    }

    public void addChild(CallNode child) {
        children.add(child);
    }

    public CallNode(PsiMethod method,
                    LayerResolver resolver){

        this.method = method;

        this.layer = resolver.resolveMethodLayer(method);
    }

    @Override
    public String toString() {

        return method.getContainingClass().getName()
                + "."
                + method.getName()
                + "()";
    }
}
