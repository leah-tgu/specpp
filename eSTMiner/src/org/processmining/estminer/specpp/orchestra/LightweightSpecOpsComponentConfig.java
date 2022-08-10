package org.processmining.estminer.specpp.orchestra;

import org.processmining.estminer.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.estminer.specpp.componenting.system.ComponentSystemAdapter;
import org.processmining.estminer.specpp.config.Configurators;
import org.processmining.estminer.specpp.config.GeneratingTreeConfiguration;
import org.processmining.estminer.specpp.config.SupervisionConfiguration;
import org.processmining.estminer.specpp.datastructures.tree.base.PlaceGenerator;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.EnumeratingTree;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.VariableExpansion;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.MonotonousPlaceGenerator;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.estminer.specpp.evaluation.fitness.ShortCircuitingFitnessEvaluator;
import org.processmining.estminer.specpp.evaluation.markings.LogHistoryMaker;
import org.processmining.estminer.specpp.supervision.supervisors.BaseSupervisor;
import org.processmining.estminer.specpp.supervision.supervisors.TerminalSupervisor;

public class LightweightSpecOpsComponentConfig extends BaseSpecOpsComponentConfig {
    @Override
    public EvaluatorConfiguration getEvaluatorConfiguration(ComponentSystemAdapter csa) {
        return Configurators.evaluators()
                            .evaluatorProvider(LogHistoryMaker::new)
                            .evaluatorProvider(ShortCircuitingFitnessEvaluator::new)
                            .build(csa);
    }

    @Override
    public SupervisionConfiguration getSupervisionConfiguration(ComponentSystemAdapter csa) {
        return Configurators.supervisors()
                            .supervisor(BaseSupervisor::new)
                            .supervisor(TerminalSupervisor::new)
                            .build(csa);
    }

    @Override
    public GeneratingTreeConfiguration<PlaceNode, PlaceGenerator> getGeneratingTreeConfiguration(ComponentSystemAdapter csa) {
        return Configurators.<PlaceNode, PlaceGenerator>generatingTree()
                            .generator(new MonotonousPlaceGenerator.Builder())
                            .expansionStrategy(VariableExpansion::bfs)
                            .tree(EnumeratingTree::new)
                            .build(csa);
    }

}
