package org.processmining.estminer.specpp.config;

import org.processmining.estminer.specpp.componenting.system.ComponentSystemAdapter;
import org.processmining.estminer.specpp.representations.tree.base.ExpansionStrategy;
import org.processmining.estminer.specpp.representations.tree.base.TreeNode;
import org.processmining.estminer.specpp.representations.tree.base.impls.EnumeratingTree;
import org.processmining.estminer.specpp.representations.tree.base.traits.LocallyExpandable;

public class TreeConfiguration<N extends TreeNode & LocallyExpandable<N>> extends Configuration {

    protected final InitializingBuilder<EnumeratingTree<N>, ExpansionStrategy<N>> treeFunction;
    protected final SimpleBuilder<ExpansionStrategy<N>> expansionStrategyBuilder;

    public TreeConfiguration(ComponentSystemAdapter csa, InitializingBuilder<EnumeratingTree<N>, ExpansionStrategy<N>> treeFunction, SimpleBuilder<ExpansionStrategy<N>> expansionStrategyBuilder) {
        super(csa);
        this.treeFunction = treeFunction;
        this.expansionStrategyBuilder = expansionStrategyBuilder;
    }

    public static <N extends TreeNode & LocallyExpandable<N>> TreeConfiguration.Configurator<N> configure() {
        return new Configurator<>();
    }

    public ExpansionStrategy<N> createExpansionStrategy() {
        return createFrom(expansionStrategyBuilder);
    }

    public EnumeratingTree<N> createTree() {
        return createFrom(treeFunction, createExpansionStrategy());
    }

    public static class Configurator<N extends TreeNode & LocallyExpandable<N>> implements ComponentInitializerBuilder<TreeConfiguration<N>> {
        protected InitializingBuilder<EnumeratingTree<N>, ExpansionStrategy<N>> treeFunction;
        protected SimpleBuilder<ExpansionStrategy<N>> expansionStrategyBuilder;

        public Configurator() {
        }

        public Configurator(InitializingBuilder<EnumeratingTree<N>, ExpansionStrategy<N>> treeFunction, SimpleBuilder<ExpansionStrategy<N>> expansionStrategyBuilder) {
            this.treeFunction = treeFunction;
            this.expansionStrategyBuilder = expansionStrategyBuilder;
        }

        public Configurator<N> expansionStrategy(SimpleBuilder<ExpansionStrategy<N>> expansionStrategyBuilder) {
            this.expansionStrategyBuilder = expansionStrategyBuilder;
            return this;
        }

        public Configurator<N> tree(InitializingBuilder<EnumeratingTree<N>, ExpansionStrategy<N>> treeFunction) {
            this.treeFunction = treeFunction;
            return this;
        }

        public TreeConfiguration<N> build(ComponentSystemAdapter cs) {
            return new TreeConfiguration<>(cs, treeFunction, expansionStrategyBuilder);
        }

    }

}
