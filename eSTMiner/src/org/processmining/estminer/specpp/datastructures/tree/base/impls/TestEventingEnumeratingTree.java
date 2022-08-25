package org.processmining.estminer.specpp.datastructures.tree.base.impls;

import org.processmining.estminer.specpp.componenting.data.DataRequirements;
import org.processmining.estminer.specpp.componenting.system.link.ExpansionStrategyComponent;
import org.processmining.estminer.specpp.datastructures.tree.base.TreeNode;
import org.processmining.estminer.specpp.datastructures.tree.base.traits.LocallyExpandable;

public class TestEventingEnumeratingTree<N extends TreeNode & LocallyExpandable<N>> extends EventingEnumeratingTree<N> {
    public TestEventingEnumeratingTree(ExpansionStrategyComponent<N> expansionStrategy) {
        super(expansionStrategy);
        localComponentSystem().provide(DataRequirements.dataSource("test", TestEventingEnumeratingTree.class, () -> this));
    }
}
