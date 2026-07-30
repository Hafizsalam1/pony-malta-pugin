package com.malta.plugin.controller;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.TextRange;
import com.malta.plugin.filter.CallTreeFilter;
import com.malta.plugin.model.CallNode;
import com.malta.plugin.model.TableAccess;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class CallTreeController {

    private final JTree tree;
    private final JTextField searchField;
    private final JButton flowButton;
    private final JButton layerButton;
    private final JButton tableButton;


    private final DefaultMutableTreeNode flowRoot;
    private final DefaultMutableTreeNode layerRoot;
    private final DefaultMutableTreeNode tableRoot;

    private DefaultMutableTreeNode currentRoot;
    private DefaultTreeModel currentModel;

    private final CallTreeFilter filter;
    private static final Logger LOG = Logger.getInstance(CallTreeController.class);

    public CallTreeController(
            JTree tree,
            JTextField searchField,
            JButton flowButton,
            JButton layerButton,
            JButton tableButton,
            DefaultMutableTreeNode flowRoot,
            DefaultMutableTreeNode layerRoot,
            DefaultMutableTreeNode tableRoot
    ) {


        this.tree = tree;
        this.searchField = searchField;
        this.flowButton = flowButton;
        this.layerButton = layerButton;
        this.tableButton = tableButton;

        this.flowRoot = flowRoot;
        this.layerRoot = layerRoot;
        this.tableRoot = tableRoot;

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

                PsiMethod method = null;

                if (userObject instanceof CallNode callNode) {
                    method = callNode.getMethod();
                } else if (userObject instanceof TableAccess access) {
                    method = access.getCallerMethod();
                }

                if (userObject instanceof TableAccess access) {

                    PsiMethodCallExpression callExpr = access.getCallExpression();

                    if (callExpr == null) {
                        LOG.warn("TableAccess has no call expression: " + access.getTableName());
                    } else {
                        PsiFile file = callExpr.getContainingFile();
                        VirtualFile vFile = file != null ? file.getVirtualFile() : null;

                        if (vFile == null) {
                            LOG.warn("No virtual file for call expression in method: " + access.getCallerMethod().getName());
                        } else {
                            Project project = access.getCallerMethod().getProject();
                            int start = callExpr.getTextRange().getStartOffset();
                            int end = callExpr.getTextRange().getEndOffset();

                            OpenFileDescriptor descriptor = new OpenFileDescriptor(project, vFile, start);

                            Editor editor = FileEditorManager.getInstance(project).openTextEditor(descriptor, true);

                            if (editor != null) {
                                editor.getCaretModel().moveToOffset(start);
                                editor.getSelectionModel().setSelection(start, end);
                                return;
                            } else {
                                descriptor.navigate(true);
                                return;
                            }
                        }
                    }
                }

                if (method != null && method.canNavigate()) {
                    method.navigate(true);
                }
            }
        });
    }


    private void registerSwitchButton() {

        flowButton.addActionListener(e -> switchTo(flowRoot));

        layerButton.addActionListener(e -> switchTo(layerRoot));

        tableButton.addActionListener(e -> switchTo(tableRoot));
    }

    private void switchTo(DefaultMutableTreeNode root) {

        currentRoot = root;
        currentModel = new DefaultTreeModel(root);

        tree.setModel(currentModel);
        tree.expandRow(0);
    }


}
