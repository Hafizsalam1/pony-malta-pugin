package com.malta.plugin.renderer;

import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.malta.plugin.model.CallNode;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class CallTreeRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean selected,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {

        JLabel label = (JLabel) super.getTreeCellRendererComponent(
                tree,
                value,
                selected,
                expanded,
                leaf,
                row,
                hasFocus);

        DefaultMutableTreeNode treeNode =
                (DefaultMutableTreeNode) value;

        Object userObject = treeNode.getUserObject();

        if (!(userObject instanceof CallNode callNode)) {
            return label;
        }

        PsiMethod method = callNode.getMethod();

        if (method == null) {
            return label;
        }

        PsiClass psiClass = method.getContainingClass();

        if (psiClass == null) {
            return label;
        }


//        label.setText(
//                method.getContainingClass().getName()
//                        + "."
//                        + method.getName()
//                        + "()"
//        );


        applyColor(this, psiClass, selected);
        applyIcon(this, method);

        setText(
                method.getContainingClass().getName()
                        + "."
                        + method.getName()
                        + "()"
        );

        return this;
    }

    private void applyColor(JLabel label,
                            PsiClass psiClass,
                            boolean selected) {

        if (selected || psiClass == null) {
            return;
        }

        if (psiClass.hasAnnotation(
                "org.springframework.web.bind.annotation.RestController")
                || psiClass.hasAnnotation(
                "org.springframework.stereotype.Controller")) {

            // Merah
            label.setForeground(new Color(220, 120, 120));


        } else if (psiClass.hasAnnotation(
                "org.springframework.stereotype.Service")) {
            // Biru
            label.setForeground(new Color(86, 156, 214));

        } else if (psiClass.hasAnnotation(
                "org.springframework.stereotype.Repository")) {
            // Hijau
            label.setForeground(new Color(78, 201, 176));

        } else {

            // Abu-abu
            label.setForeground(new Color(180, 180, 180));
        }
    }

    private void applyIcon(JLabel label,
                           PsiMethod method) {

        String name = method.getName().toLowerCase();

         if (name.startsWith("save")
                || name.startsWith("insert")
                || name.startsWith("persist")) {

            label.setIcon(AllIcons.Actions.MenuSaveall);

        } else if (name.startsWith("update")
                || name.startsWith("edit")
                || name.startsWith("modify")
         || name.startsWith("create")
                 || name.startsWith("register")
                 || name.startsWith("add")) {

            label.setIcon(AllIcons.Actions.Edit);

        } else if (name.startsWith("delete")
                || name.startsWith("remove")) {

            label.setIcon(AllIcons.General.Remove);

        } else if (name.startsWith("send")) {

            label.setIcon(AllIcons.Actions.Execute);

        } else if (name.startsWith("validate")
                || name.startsWith("check")) {

            label.setIcon(AllIcons.General.InspectionsOK);

        } else if (name.startsWith("map")
                || name.startsWith("convert")
                || name.startsWith("transform")) {

            label.setIcon(AllIcons.Actions.Refresh);

        }else{
             label.setIcon(AllIcons.Nodes.Method);
         }

    }
}
