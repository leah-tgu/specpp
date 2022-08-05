package org.processmining.estminer.specpp.representations.tree.base.impls;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.processmining.estminer.specpp.representations.tree.base.NodeProperties;
import org.processmining.estminer.specpp.representations.tree.base.PropertyNode;
import org.processmining.estminer.specpp.representations.tree.base.TreeNode;
import org.processmining.estminer.specpp.representations.tree.events.*;
import org.processmining.estminer.specpp.supervision.piping.Observer;

import static org.processmining.estminer.specpp.util.JavaTypingUtils.castClass;

public class ChangeTree<P extends NodeProperties> extends BiDiTreeImpl<AnnotatableBiDiNodeImpl<String>> implements Observer<TreeNodeEvent<PropertyNode<P>>> {

    private final BidiMap<TreeNode, AnnotatableBiDiNodeImpl<String>> map;

    public ChangeTree() {
        super();
        map = new DualHashBidiMap<>();
    }

    @Override
    public void observe(TreeNodeEvent<PropertyNode<P>> event) {
        PropertyNode<P> source = event.getSource();
        synchronized (map) {
            if (event instanceof NodeExpansionEvent) {
                if (!map.containsKey(source)) {
                    AnnotatableBiDiNodeImpl<String> r = NodeFactory.annotatedRoot(castClass(AnnotatableBiDiNodeImpl.class), source.getProperties()
                                                                                                                                  .toString());
                    setRoot(r);
                    map.put(source, r);
                }
                AnnotatableBiDiNodeImpl<String> affectedNode = map.get(source);
                NodeExpansionEvent<PropertyNode<P>> expansionEvent = (NodeExpansionEvent<PropertyNode<P>>) event;
                PropertyNode<P> child = expansionEvent.getChild();
                AnnotatableBiDiNodeImpl<String> corrChild = NodeFactory.annotatedChildOf(affectedNode, child.getProperties()
                                                                                                            .toString());
                map.put(child, corrChild);
            } else if (event instanceof NodeRemovalEvent) {

            } else if (event instanceof NodeContractionEvent) {

            } else if (event instanceof NodeExhaustionEvent) {
                AnnotatableBiDiNodeImpl<String> affectedNode = map.get(source);
                NodeFactory.annotatedChildOf(affectedNode, "X");
            }
        }
    }

    @Override
    public String toString() {
        return "ChangeTree:" + super.toString();
    }
}
