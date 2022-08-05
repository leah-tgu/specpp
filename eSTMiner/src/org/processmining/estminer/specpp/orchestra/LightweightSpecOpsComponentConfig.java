package org.processmining.estminer.specpp.orchestra;

import org.processmining.estminer.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.estminer.specpp.componenting.system.ComponentSystemAdapter;
import org.processmining.estminer.specpp.config.Configurators;
import org.processmining.estminer.specpp.config.GeneratingTreeConfiguration;
import org.processmining.estminer.specpp.config.SupervisionConfiguration;
import org.processmining.estminer.specpp.est.PlaceNode;
import org.processmining.estminer.specpp.evaluation.ShortCircuitingFitnessEvaluator;
import org.processmining.estminer.specpp.representations.log.LogHistoryMaker;
import org.processmining.estminer.specpp.representations.tree.base.impls.InstrumentedEnumeratingTree;
import org.processmining.estminer.specpp.representations.tree.base.impls.VariableExpansion;
import org.processmining.estminer.specpp.representations.tree.nodegen.PlaceGenerator;
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
                            .generator(new PlaceGenerator.Builder())
                            .expansionStrategy(VariableExpansion::bfs)
                            .tree(InstrumentedEnumeratingTree::new)
                            .build(csa);
    }

}
