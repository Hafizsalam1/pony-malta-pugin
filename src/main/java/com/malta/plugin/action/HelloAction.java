package com.malta.plugin.action;

import com.malta.plugin.analyzer.MethodAnalyzer;
import com.malta.plugin.builder.FlowTreeBuilder;
import com.malta.plugin.builder.LayerTreeBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import com.intellij.psi.*;
import com.malta.plugin.controller.CallTreeController;
import com.malta.plugin.model.CallNode;
import com.malta.plugin.resolver.MethodResolver;
import com.malta.plugin.service.CallGraphService;
import org.jetbrains.annotations.NotNull;
import com.malta.plugin.filter.CallTreeFilter;
import com.malta.plugin.renderer.CallTreeRenderer;

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

        PsiMethod method = MethodResolver.findMethod(anActionEvent);

        if (method == null) {
            return;
        }
        // Build Call Graph
        CallNode root =  new CallGraphService().analyze(method);

        // Build Flow Tree
        FlowTreeBuilder flowBuilder = new FlowTreeBuilder();

        DefaultMutableTreeNode flowRoot = flowBuilder.build(root);

        // Build Layer Tree
        LayerTreeBuilder layerBuilder = new LayerTreeBuilder();

        DefaultMutableTreeNode layerRoot = layerBuilder.build(root);

        final DefaultMutableTreeNode[] currentRoot = {
                flowRoot
        };


        final DefaultTreeModel[] currentModel = {
                new DefaultTreeModel(flowRoot)
        };

        JTree tree = new JTree(currentModel[0]);
        tree.setToggleClickCount(0);

        tree.setCellRenderer(new CallTreeRenderer());

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


        CallTreeController controller =
                new CallTreeController(
                        tree,
                        searchField,
                        switchButton,
                        flowRoot,
                        layerRoot
                );

        controller.init();

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

        e.getPresentation().setEnabledAndVisible(
                MethodResolver.findMethod(e) != null
        );
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }


}
