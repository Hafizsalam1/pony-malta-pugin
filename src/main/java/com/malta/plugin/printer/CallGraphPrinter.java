package com.malta.plugin.printer;

import com.malta.plugin.model.CallNode;

public class CallGraphPrinter {


    public String print(CallNode root) {

        StringBuilder sb = new StringBuilder();

        print(root, sb, 0);

        return sb.toString();
    }

    private void print(CallNode node,
                       StringBuilder sb,
                       int depth) {

        if (node == null) {
            return;
        }

        String indent = "    ".repeat(depth);

        sb.append(indent)
                .append("📦 ")
                .append(node.getMethod().getContainingClass().getName())
                .append(".")
                .append(node.getMethod().getName())
                .append("()")
                .append("\n");

        System.out.println(
                node.getMethod().getName() +
                        " children = " +
                        node.getChildren().size()
        );

        for (CallNode child : node.getChildren()) {
            print(child, sb, depth + 1);
        }
    }


}
