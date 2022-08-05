package org.processmining.estminer.specpp.config;

import org.processmining.estminer.specpp.base.Candidate;
import org.processmining.estminer.specpp.base.Composition;
import org.processmining.estminer.specpp.base.Result;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.estminer.specpp.datastructures.tree.base.LocalNodeGenerator;
import org.processmining.estminer.specpp.datastructures.tree.base.TreeNode;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.GeneratingLocalNode;
import org.processmining.estminer.specpp.datastructures.tree.base.traits.LocallyExpandable;
import org.processmining.estminer.specpp.datastructures.tree.heuristic.NodeHeuristic;

public class Configurators {

    public static <C extends Candidate, I extends Composition<C>, R extends Result> ProposerComposerConfiguration.Configurator<C, I, R> proposerComposer() {
        return new ProposerComposerConfiguration.Configurator<>();
    }

    public static <R extends Result> PostProcessingConfiguration.Configurator<R, R> postProcessing() {
        return new PostProcessingConfiguration.Configurator<>(() -> r -> r);
    }

    public static <N extends TreeNode & LocallyExpandable<N>> TreeConfiguration.Configurator<N> tree() {
        return new TreeConfiguration.Configurator<>();
    }

    public static <N extends GeneratingLocalNode<?, ?, N>, G extends LocalNodeGenerator<?, ?, N>> GeneratingTreeConfiguration.Configurator<N, G> generatingTree() {
        return new GeneratingTreeConfiguration.Configurator<>();
    }

    public static <N extends GeneratingLocalNode<?, ?, N>, G extends LocalNodeGenerator<?, ?, N>, H extends NodeHeuristic<H>> HeuristicTreeConfiguration.Configurator<N, G, H> heuristicTree() {
        return new HeuristicTreeConfiguration.Configurator<>();
    }

    public static EvaluatorConfiguration.Configurator evaluators() {
        return new EvaluatorConfiguration.Configurator();
    }

    public static SupervisionConfiguration.Configurator supervisors() {
        return new SupervisionConfiguration.Configurator();
    }

}
