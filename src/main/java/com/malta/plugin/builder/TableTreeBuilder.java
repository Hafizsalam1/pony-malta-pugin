package com.malta.plugin.builder;

import com.malta.plugin.model.TableAccess;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TableTreeBuilder {

    public DefaultMutableTreeNode build(List<TableAccess> tableAccesses) {

        DefaultMutableTreeNode root =
                new DefaultMutableTreeNode("Affected Tables");

        Map<String, DefaultMutableTreeNode> tableNodes =
                new LinkedHashMap<>();

        for (TableAccess access : tableAccesses) {

            DefaultMutableTreeNode tableNode =
                    tableNodes.computeIfAbsent(
                            access.getTableName(),
                            tableName -> {
                                DefaultMutableTreeNode node =
                                        new DefaultMutableTreeNode(tableName);
                                root.add(node);
                                return node;
                            });

            tableNode.add(
                    new DefaultMutableTreeNode(access)
            );
        }

        return root;
    }
}
