package org.processmining.estminer.specpp.config;

import org.processmining.estminer.specpp.componenting.system.ComponentSystemAdapter;
import org.processmining.estminer.specpp.representations.tree.base.ConstrainableLocalNodeGenerator;
import org.processmining.estminer.specpp.representations.tree.base.ExpansionStrategy;
import org.processmining.estminer.specpp.representations.tree.base.GenerationConstraint;
import org.processmining.estminer.specpp.representations.tree.base.LocalNodeGenerator;
import org.processmining.estminer.specpp.representations.tree.base.impls.EnumeratingTree;
import org.processmining.estminer.specpp.representations.tree.base.impls.GeneratingLocalNode;

public class GeneratingTreeConfiguration<N extends GeneratingLocalNode<?, ?, N>, G extends LocalNodeGenerator<?, ?, N>> extends TreeConfiguration<N> {

    protected final SimpleBuilder<G> generatorBuilder;

    public GeneratingTreeConfiguration(ComponentSystemAdapter csa, InitializingBuilder<EnumeratingTree<N>, ExpansionStrategy<N>> treeFunction, SimpleBuilder<ExpansionStrategy<N>> expansionStrategyBuilder, SimpleBuilder<G> generatorBuilder) {
        super(csa, treeFunction, expansionStrategyBuilder);
        this.generatorBuilder = generatorBuilder;
    }

    public G createGenerator() {
        return createFrom(generatorBuilder);
    }

    public static class Configurator<N extends GeneratingLocalNode<?, ?, N>, G extends LocalNodeGenerator<?, ?, N>> extends TreeConfiguration.Configurator<N> {

        protected SimpleBuilder<G> generatorBuilder;

        public Configurator() {
        }

        public Configurator(InitializingBuilder<EnumeratingTree<N>, ExpansionStrategy<N>> treeFunction, SimpleBuilder<ExpansionStrategy<N>> expansionStrategyBuilder, SimpleBuilder<G> generatorBuilder) {
            super(treeFunction, expansionStrategyBuilder);
            this.generatorBuilder = generatorBuilder;
        }

        @Override
        public Configurator<N, G> tree(InitializingBuilder<EnumeratingTree<N>, ExpansionStrategy<N>> treeFunction) {
            super.tree(treeFunction);
            return this;
        }

        @Override
        public Configurator<N, G> expansionStrategy(SimpleBuilder<ExpansionStrategy<N>> expansionStrategyBuilder) {
            super.expansionStrategy(expansionStrategyBuilder);
            return this;
        }

        public Configurator<N, G> generator(SimpleBuilder<G> generatorBuilder) {
            this.generatorBuilder = generatorBuilder;
            return this;
        }

        public <GP extends ConstrainableLocalNodeGenerator<?, ?, N, GenerationConstraint>> Configurator<N, GP> constrainableGenerator(SimpleBuilder<GP> generatorBuilder) {
            return new Configurator<>(treeFunction, expansionStrategyBuilder, generatorBuilder);
        }

        @Override
        public GeneratingTreeConfiguration<N, G> build(ComponentSystemAdapter cs) {
            return new GeneratingTreeConfiguration<>(cs, treeFunction, expansionStrategyBuilder, generatorBuilder);
        }

    }

}
