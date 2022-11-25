package org.processmining.specpp.orchestra.presets;

import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.config.Configurators;
import org.processmining.specpp.config.SupervisionConfiguration;
import org.processmining.specpp.supervision.supervisors.*;

public class ExpansiveComponentConfig extends BaseComponentConfig {

    @Override
    public SupervisionConfiguration getSupervisionConfiguration(GlobalComponentRepository gcr) {
        return Configurators.supervisors()
                            .addSupervisor(BaseSupervisor::new)
                            .addSupervisor(PerformanceSupervisor::new)
                            .addSupervisor(EventCountsSupervisor::new)
                            .addSupervisor(DetailedHeuristicsSupervisor::new)
                            .addSupervisor(DetailedTreeSupervisor::new)
                            .addSupervisor(ProposalTreeSupervisor::new)
                            .addSupervisor(TerminalSupervisor::new)
                            .build(gcr);
    }
}
