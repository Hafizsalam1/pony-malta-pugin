package com.malta.plugin.builder;

import com.malta.plugin.model.CallNode;

import javax.swing.tree.DefaultMutableTreeNode;

public class FlowTreeBuilder {

    public DefaultMutableTreeNode build(CallNode node){

        if(node == null){
            return null;
        }

        DefaultMutableTreeNode treeNode =
                new DefaultMutableTreeNode(node);

        for(CallNode child : node.getChildren()){

            treeNode.add(build(child));

        }

        return treeNode;
    }
}
