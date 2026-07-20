package builder;

import com.intellij.psi.PsiMethod;
import model.CallNode;
import resolver.LayerResolver;
import util.LayerType;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.EnumMap;
import java.util.Map;

public class LayerTreeBuilder {
    private LayerResolver resolver = new LayerResolver();

    public DefaultMutableTreeNode build(CallNode root) {

        DefaultMutableTreeNode rootNode =
                new DefaultMutableTreeNode("Architecture");

        Map<LayerType, DefaultMutableTreeNode> layerNodes =
                new EnumMap<>(LayerType.class);

        layerNodes.put(LayerType.CONTROLLER,
                new DefaultMutableTreeNode("📂 Controller"));

        layerNodes.put(LayerType.SERVICE,
                new DefaultMutableTreeNode("📂 Service"));

        layerNodes.put(LayerType.REPOSITORY,
                new DefaultMutableTreeNode("📂 Repository"));

        layerNodes.put(LayerType.DTO,
                new DefaultMutableTreeNode("📂 DTO"));

        layerNodes.put(LayerType.EXTERNAL,
                new DefaultMutableTreeNode("📂 External"));

        collect(root, layerNodes);

        for (DefaultMutableTreeNode node : layerNodes.values()) {

            if (node.getChildCount() > 0) {
                rootNode.add(node);
            }
        }

        return rootNode;
    }

    private void collect(
            CallNode node,
            Map<LayerType, DefaultMutableTreeNode> layerNodes) {

        if (node == null) {
            return;
        }

        PsiMethod method = node.getMethod();

        LayerType layer = resolver.resolveMethodLayer(method);

        DefaultMutableTreeNode layerNode =
                layerNodes.get(layer);

        if (layerNode != null) {
            layerNode.add(new DefaultMutableTreeNode(node));
        }

        for (CallNode child : node.getChildren()) {
            collect(child, layerNodes);
        }
    }

}
