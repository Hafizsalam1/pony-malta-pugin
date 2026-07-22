package com.malta.plugin.controller;

import com.intellij.psi.PsiMethod;
import com.malta.plugin.filter.CallTreeFilter;
import com.malta.plugin.model.CallNode;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CallTreeController {

    private final JTree tree;
    private final JTextField searchField;
    private final JButton switchButton;

    private final DefaultMutableTreeNode flowRoot;
    private final DefaultMutableTreeNode layerRoot;

    private DefaultMutableTreeNode currentRoot;
    private DefaultTreeModel currentModel;

    private final CallTreeFilter filter;

    public CallTreeController(
            JTree tree,
            JTextField searchField,
            JButton switchButton,
            DefaultMutableTreeNode flowRoot,
            DefaultMutableTreeNode layerRoot
    ) {

        this.tree = tree;
        this.searchField = searchField;
        this.switchButton = switchButton;

        this.flowRoot = flowRoot;
        this.layerRoot = layerRoot;

        this.currentRoot = flowRoot;
        this.currentModel = new DefaultTreeModel(flowRoot);

        this.filter = new CallTreeFilter();
    }

    public void init() {

        registerSearch();

        registerNavigation();

        registerSwitchButton();
    }

    private void registerSearch() {

        searchField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTree();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTree();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTree();
            }

            private void filterTree() {

                String keyword = searchField.getText();

                if (keyword.isBlank()) {

                    tree.setModel(currentModel);

                } else {

                    DefaultMutableTreeNode filtered =
                            filter.filter(currentRoot, keyword);

                    tree.setModel(
                            new DefaultTreeModel(filtered)
                    );
                }
            }
        });
    }


    private void registerNavigation() {

        tree.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() != 2) {
                    return;
                }

                TreePath path =
                        tree.getPathForLocation(e.getX(), e.getY());

                if (path == null) {
                    return;
                }

                DefaultMutableTreeNode treeNode =
                        (DefaultMutableTreeNode) path.getLastPathComponent();

                Object userObject = treeNode.getUserObject();

                if (!(userObject instanceof CallNode callNode)) {
                    return;
                }

                PsiMethod method = callNode.getMethod();

                System.out.println(method.getName()+ "  INi Methodnya");


                if (method != null && method.canNavigate()) {
                    method.navigate(true);
                }
            }
        });
    }


    private void registerSwitchButton() {

        switchButton.addActionListener(e -> {

            if (switchButton.getText().equals("Layer View")) {

                currentRoot = layerRoot;
                currentModel = new DefaultTreeModel(layerRoot);

                tree.setModel(currentModel);

                switchButton.setText("Flow View");

            } else {

                currentRoot = flowRoot;
                currentModel = new DefaultTreeModel(flowRoot);

                tree.setModel(currentModel);

                switchButton.setText("Layer View");
            }

            tree.expandRow(0);
        });
    }


}
