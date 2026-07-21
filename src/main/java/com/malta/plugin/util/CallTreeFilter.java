package com.malta.plugin.util;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Enumeration;

public class CallTreeFilter {

    public DefaultMutableTreeNode filter(
            DefaultMutableTreeNode node,
            String keyword) {

        if (node == null) {
            return null;
        }

        keyword = keyword.toLowerCase();

        Object userObject = node.getUserObject();

        String text = userObject.toString().toLowerCase();

        boolean match = text.contains(keyword);

        DefaultMutableTreeNode result =
                new DefaultMutableTreeNode(userObject);

        Enumeration<?> children = node.children();

        while (children.hasMoreElements()) {

            DefaultMutableTreeNode child =
                    (DefaultMutableTreeNode) children.nextElement();

            DefaultMutableTreeNode filtered =
                    filter(child, keyword);

            if (filtered != null) {
                result.add(filtered);
            }
        }

        return (match || result.getChildCount() > 0)
                ? result
                : null;
    }

}
