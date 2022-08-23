package org.processmining.estminer.specpp.config;

import org.processmining.estminer.specpp.base.Candidate;
import org.processmining.estminer.specpp.base.Result;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.estminer.specpp.componenting.system.link.CompositionComponent;
import org.processmining.estminer.specpp.datastructures.tree.base.ChildGenerationLogic;
import org.processmining.estminer.specpp.datastructures.tree.base.LocalNodeGenerator;
import org.processmining.estminer.specpp.datastructures.tree.base.TreeNode;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.LocalNodeWithExternalizedLogic;
import org.processmining.estminer.specpp.datastructures.tree.base.traits.LocallyExpandable;
import org.processmining.estminer.specpp.datastructures.tree.heuristic.HeuristicValue;

public class Configurators {

    public static <C extends Candidate, I extends CompositionComponent<C>, R extends Result> ProposerComposerConfiguration.Configurator<C, I, R> proposerComposer() {
        return new ProposerComposerConfiguration.Configurator<>();
    }

    public static <R extends Result> PostProcessingConfiguration.Configurator<R, R> postProcessing() {
        return new PostProcessingConfiguration.Configurator<>(() -> r -> r);
    }

    public static <N extends TreeNode & LocallyExpandable<N>> TreeConfiguration.Configurator<N> tree() {
        return new TreeConfiguration.Configurator<>();
    }

    public static <N extends LocalNodeWithExternalizedLogic<?, ?, N>, G extends ChildGenerationLogic<?, ?, N>> EfficientTreeConfiguration.Configurator<N, G> generatingTree() {
        return new EfficientTreeConfiguration.Configurator<>();
    }

    public static <N extends LocalNodeWithExternalizedLogic<?, ?, N>, G extends ChildGenerationLogic<?, ?, N>, H extends HeuristicValue<H>> HeuristicTreeConfiguration.Configurator<N, G, H> heuristicTree() {
        return new HeuristicTreeConfiguration.Configurator<>();
    }

    public static EvaluatorConfiguration.Configurator evaluators() {
        return new EvaluatorConfiguration.Configurator();
    }

    public static SupervisionConfiguration.Configurator supervisors() {
        return new SupervisionConfiguration.Configurator();
    }

}
