package com.malta.plugin.action;

import com.malta.plugin.analyzer.analyzer.MethodAnalyzer;
import com.malta.plugin.builder.FlowTreeBuilder;
import com.malta.plugin.builder.LayerTreeBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.malta.plugin.model.CallNode;
import org.jetbrains.annotations.NotNull;
import com.malta.plugin.util.CallTreeFilter;
import com.malta.plugin.util.CallTreeRenderer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HelloAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);

        PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);

        if(psiFile==null){
            return;
        }

        if(editor==null){
            return;
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = psiFile.findElementAt(offset);
        PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);

        if(method == null){
            return;
        }

        // Build Call Graph
        MethodAnalyzer analyzer = new MethodAnalyzer();

        CallNode root = analyzer.analyze(method);


// Build Flow Tree
        FlowTreeBuilder flowBuilder = new FlowTreeBuilder();

        DefaultMutableTreeNode flowRoot =
                flowBuilder.build(root);


// Build Layer Tree
        LayerTreeBuilder layerBuilder = new LayerTreeBuilder();

        DefaultMutableTreeNode layerRoot =
                layerBuilder.build(root);


        final DefaultMutableTreeNode[] currentRoot = {
                flowRoot
        };


        final DefaultTreeModel[] currentModel = {
                new DefaultTreeModel(flowRoot)
        };

        JTree tree = new JTree(currentModel[0]);


        tree.setCellRenderer(
                new CallTreeRenderer()
        );


        CallTreeFilter filter = new CallTreeFilter();
        JTextField searchField = new JTextField();

        JLabel searchIcon = new JLabel(AllIcons.Actions.Search);

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton switchButton = new JButton("Layer View");

        searchPanel.add(searchIcon, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(switchButton, BorderLayout.EAST);


        searchField.getDocument().addDocumentListener(
                new DocumentListener() {

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

                        if(keyword.isBlank()) {

                            tree.setModel(currentModel[0]);

                        } else {

                            DefaultMutableTreeNode filtered =
                                    filter.filter(currentRoot[0], keyword);

                            tree.setModel(
                                    new DefaultTreeModel(filtered)
                            );
                        }
                    }

                });

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

                if (method != null && method.canNavigate()) {
                    method.navigate(true);
                }

            }

        });



        switchButton.addActionListener(e -> {

            if (switchButton.getText().equals("Layer View")) {


                currentRoot[0] = layerRoot;


                currentModel[0] =
                        new DefaultTreeModel(layerRoot);


                tree.setModel(
                        currentModel[0]
                );


                switchButton.setText("Flow View");


            } else {


                currentRoot[0] = flowRoot;


                currentModel[0] =
                        new DefaultTreeModel(flowRoot);


                tree.setModel(
                        currentModel[0]
                );


                switchButton.setText("Layer View");

            }


            tree.expandRow(0);

        });

        JScrollPane scrollPane = new JScrollPane(tree);

        JPanel panel = new JPanel(new BorderLayout());


        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(
                null,
                panel,
                "Method Analyzer",
                JOptionPane.PLAIN_MESSAGE
        );

    }


    @Override
    public void update(@NotNull AnActionEvent e) {

        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);

        boolean visible = false;

        if (editor != null && file != null) {

            PsiElement element =
                    file.findElementAt(editor.getCaretModel().getOffset());

            visible = PsiTreeUtil.getParentOfType(
                    element,
                    PsiMethod.class
            ) != null;
        }

        e.getPresentation().setEnabledAndVisible(visible);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }


}
