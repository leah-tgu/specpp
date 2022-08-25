package org.processmining.estminer.specpp.supervision.supervisors;

import org.processmining.estminer.specpp.componenting.delegators.ContainerUtils;
import org.processmining.estminer.specpp.componenting.delegators.ListContainer;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.supervision.AbstractSupervisor;
import org.processmining.estminer.specpp.supervision.observations.Observation;
import org.processmining.estminer.specpp.supervision.piping.Observable;
import org.processmining.estminer.specpp.supervision.piping.PipeSystemFlusher;
import org.processmining.estminer.specpp.traits.Joinable;

public class TerminalSupervisor extends AbstractSupervisor implements Joinable {

    private final ListContainer<Observable<?>> observables = ContainerUtils.listContainer();

    public TerminalSupervisor() {
        globalComponentSystem().require(SupervisionRequirements.observable(SupervisionRequirements.regex("\\w+"), Observation.class), observables);
    }

    @Override
    public void initSelf() {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void join() {
        PipeSystemFlusher.flush(observables.getContents());
    }
}
