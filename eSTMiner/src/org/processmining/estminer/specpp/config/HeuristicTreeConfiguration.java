package org.processmining.estminer.specpp.config;

import org.processmining.estminer.specpp.componenting.system.ComponentSystemAdapter;
import org.processmining.estminer.specpp.datastructures.tree.base.*;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.EnumeratingTree;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.GeneratingLocalNode;
import org.processmining.estminer.specpp.datastructures.tree.heuristic.HeuristicTreeExpansion;
import org.processmining.estminer.specpp.datastructures.tree.heuristic.NodeHeuristic;

public class HeuristicTreeConfiguration<N extends GeneratingLocalNode<?, ?, N>, G extends LocalNodeGenerator<?, ?, N>, H extends NodeHeuristic<H>> extends GeneratingTreeConfiguration<N, G> {

    private final SimpleBuilder<HeuristicStrategy<N, H>> heuristicStrategySupplier;
    private final InitializingBuilder<HeuristicTreeExpansion<N, H>, HeuristicStrategy<N, H>> treeExpansionFunction;
    private final InitializingBuilder<EnumeratingTree<N>, HeuristicTreeExpansion<N, H>> enumeratingTreeFunction;

    public HeuristicTreeConfiguration(ComponentSystemAdapter cs, SimpleBuilder<HeuristicStrategy<N, H>> heuristicStrategySupplier, InitializingBuilder<HeuristicTreeExpansion<N, H>, HeuristicStrategy<N, H>> treeExpansionFunction, InitializingBuilder<EnumeratingTree<N>, HeuristicTreeExpansion<N, H>> enumeratingTreeFunction, SimpleBuilder<? extends G> generatorSupplier) {
        super(cs, null, null, generatorSupplier);
        this.heuristicStrategySupplier = heuristicStrategySupplier;
        this.treeExpansionFunction = treeExpansionFunction;
        this.enumeratingTreeFunction = enumeratingTreeFunction;
    }

    public HeuristicStrategy<N, H> createHeuristicStrategy() {
        return createFrom(heuristicStrategySupplier);
    }

    public HeuristicTreeExpansion<N, H> createHeuristicTreeExpansion() {
        return createFrom(treeExpansionFunction, createHeuristicStrategy());
    }

    @Override
    public ExpansionStrategy<N> createExpansionStrategy() {
        return createHeuristicTreeExpansion();
    }

    @Override
    public EnumeratingTree<N> createTree() {
        return createFrom(enumeratingTreeFunction, createHeuristicTreeExpansion());
    }

    public static class Configurator<N extends GeneratingLocalNode<?, ?, N>, G extends LocalNodeGenerator<?, ?, N>, H extends NodeHeuristic<H>> extends GeneratingTreeConfiguration.Configurator<N, G> {

        protected SimpleBuilder<HeuristicStrategy<N, H>> heuristicStrategyBuilder;
        protected InitializingBuilder<HeuristicTreeExpansion<N, H>, HeuristicStrategy<N, H>> treeExpansionBuilder;
        protected InitializingBuilder<EnumeratingTree<N>, HeuristicTreeExpansion<N, H>> enumeratingTreeBuilder;

        public Configurator() {
        }

        public Configurator(SimpleBuilder<HeuristicStrategy<N, H>> heuristicStrategyBuilder, InitializingBuilder<HeuristicTreeExpansion<N, H>, HeuristicStrategy<N, H>> treeExpansionBuilder, InitializingBuilder<EnumeratingTree<N>, HeuristicTreeExpansion<N, H>> enumeratingTreeFunction, SimpleBuilder<? extends G> generatorSupplier) {
            super(null, null, generatorSupplier);
            this.heuristicStrategyBuilder = heuristicStrategyBuilder;
            this.treeExpansionBuilder = treeExpansionBuilder;
            this.enumeratingTreeBuilder = enumeratingTreeFunction;
        }

        public Configurator<N, G, H> heuristic(SimpleBuilder<HeuristicStrategy<N, H>> heuristicStrategySupplier) {
            this.heuristicStrategyBuilder = heuristicStrategySupplier;
            return this;
        }

        public Configurator<N, G, H> heuristicExpansion(InitializingBuilder<HeuristicTreeExpansion<N, H>, HeuristicStrategy<N, H>> treeExpansionFunction) {
            this.treeExpansionBuilder = treeExpansionFunction;
            return this;
        }

        public Configurator<N, G, H> enumeratingTree(InitializingBuilder<EnumeratingTree<N>, HeuristicTreeExpansion<N, H>> enumeratingTreeFunction) {
            this.enumeratingTreeBuilder = enumeratingTreeFunction;
            return this;
        }

        @Override
        public GeneratingTreeConfiguration.Configurator<N, G> generator(SimpleBuilder<? extends G> generatorBuilder) {
            return super.generator(generatorBuilder);
        }

        @Override
        public <GP extends ConstrainableLocalNodeGenerator<?, ?, N, GenerationConstraint>> Configurator<N, GP, H> constrainableGenerator(SimpleBuilder<? extends GP> generatorBuilder) {
            super.constrainableGenerator(generatorBuilder);
            return new Configurator<>(heuristicStrategyBuilder, treeExpansionBuilder, enumeratingTreeBuilder, generatorBuilder);
        }

        @Override
        public HeuristicTreeConfiguration<N, G, H> build(ComponentSystemAdapter cs) {
            return new HeuristicTreeConfiguration<>(cs, heuristicStrategyBuilder, treeExpansionBuilder, enumeratingTreeBuilder, generatorBuilder);
        }
    }
}
