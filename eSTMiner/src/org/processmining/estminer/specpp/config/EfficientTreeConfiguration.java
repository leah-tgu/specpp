package org.processmining.estminer.specpp.config;

import org.processmining.estminer.specpp.componenting.system.ComponentCollection;
import org.processmining.estminer.specpp.datastructures.tree.base.*;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.EnumeratingTree;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.LocalNodeWithExternalizedLogic;

public class EfficientTreeConfiguration<N extends LocalNodeWithExternalizedLogic<?, ?, N>, G extends ChildGenerationLogic<?, ?, N>> extends TreeConfiguration<N> {

    protected final SimpleBuilder<? extends G> generatorBuilder;

    public EfficientTreeConfiguration(ComponentCollection csa, InitializingBuilder<EnumeratingTree<N>, ExpansionStrategy<N>> treeFunction, SimpleBuilder<ExpansionStrategy<N>> expansionStrategyBuilder, SimpleBuilder<? extends G> generatorBuilder) {
        super(csa, treeFunction, expansionStrategyBuilder);
        this.generatorBuilder = generatorBuilder;
    }

    public G createChildGenerationLogic() {
        return createFrom(generatorBuilder);
    }

    public static class Configurator<N extends LocalNodeWithExternalizedLogic<?, ?, N>, G extends ChildGenerationLogic<?, ?, N>> extends TreeConfiguration.Configurator<N> {

        protected SimpleBuilder<? extends G> generatorBuilder;

        public Configurator() {
        }

        public Configurator(InitializingBuilder<EnumeratingTree<N>, ExpansionStrategy<N>> treeFunction, SimpleBuilder<ExpansionStrategy<N>> expansionStrategyBuilder, SimpleBuilder<? extends G> generatorBuilder) {
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

        public Configurator<N, G> childGenerationLogic(SimpleBuilder<? extends G> generatorBuilder) {
            this.generatorBuilder = generatorBuilder;
            return this;
        }

        public <GP extends ConstrainableChildGenerationLogic<?, ?, N, GenerationConstraint>> Configurator<N, GP> constrainableGenerator(SimpleBuilder<? extends GP> generatorBuilder) {
            return new Configurator<>(treeFunction, expansionStrategyBuilder, generatorBuilder);
        }

        @Override
        public EfficientTreeConfiguration<N, G> build(ComponentCollection cs) {
            return new EfficientTreeConfiguration<>(cs, treeFunction, expansionStrategyBuilder, generatorBuilder);
        }

    }

}
