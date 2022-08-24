package org.processmining.estminer.specpp.orchestra;

import org.processmining.estminer.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.estminer.specpp.config.Configurators;
import org.processmining.estminer.specpp.config.SupervisionConfiguration;
import org.processmining.estminer.specpp.supervision.supervisors.*;

public class ExpansiveSpecOpsComponentConfig extends BaseSpecOpsComponentConfig {

    @Override
    public SupervisionConfiguration getSupervisionConfiguration(GlobalComponentRepository gcr) {
        return Configurators.supervisors()
                            .supervisor(BaseSupervisor::new)
                            .supervisor(PerformanceSupervisor::new)
                            .supervisor(EventCountsSupervisor::new)
                            .supervisor(DetailedHeuristicsSupervisor::new)
                            .supervisor(DetailedTreeSupervisor::new)
                            .supervisor(ProposalTreeSupervisor::new)
                            .supervisor(TerminalSupervisor::new)
                            .build(gcr);
    }
}
