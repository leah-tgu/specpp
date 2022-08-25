package org.processmining.estminer.specpp.proposal;

import org.processmining.estminer.specpp.base.impls.AbstractEfficientTreeBasedProposer;
import org.processmining.estminer.specpp.componenting.data.DataRequirements;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.componenting.system.link.ChildGenerationLogicComponent;
import org.processmining.estminer.specpp.componenting.system.link.EfficientTreeComponent;
import org.processmining.estminer.specpp.config.EfficientTreeConfiguration;
import org.processmining.estminer.specpp.config.SimpleBuilder;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceState;
import org.processmining.estminer.specpp.datastructures.util.Button;
import org.processmining.estminer.specpp.supervision.EventSupervision;
import org.processmining.estminer.specpp.supervision.piping.PipeWorks;
import org.processmining.estminer.specpp.supervision.supervisors.DebuggingSupervisor;

public class RestartablePlaceProposer extends ConstrainablePlaceProposer {

    private final Button updateLocalComponentSystem = new Button();

    private final EventSupervision<ProposerSignal> proposerSignalOutput = PipeWorks.eventSupervision();

    public static class Builder extends ConstrainablePlaceProposer.Builder {

        @Override
        protected ConstrainablePlaceProposer buildIfFullySatisfied() {
            EfficientTreeConfiguration<Place, PlaceState, PlaceNode> config = delegatingDataSource.getData();
            return new RestartablePlaceProposer(config.createPossiblyInstrumentedChildGenerationLogic(), config::createPossiblyInstrumentedTree);
        }
    }

    public RestartablePlaceProposer(ChildGenerationLogicComponent<Place, PlaceState, PlaceNode> cgl, SimpleBuilder<EfficientTreeComponent<PlaceNode>> treeBuilder) {
        super(cgl, treeBuilder);
        localComponentSystem().require(DataRequirements.dataSource("update_local_component_system", Runnable.class), updateLocalComponentSystem)
                              .provide(SupervisionRequirements.observable("proposer.signals.out", ProposerSignal.class, proposerSignalOutput))
                              .provide(SupervisionRequirements.observer("proposer.signals.in", ProposerSignal.class, this::receiveSignal));
    }


    private void receiveSignal(ProposerSignal proposerSignal) {
        if (proposerSignal instanceof RestartProposer) {
            restart();
        }
    }

    @Override
    protected void setProposer(AbstractEfficientTreeBasedProposer<Place, PlaceNode> proposer) {
        if (this.proposer != null) unregisterSubComponent(this.proposer);
        super.setProposer(proposer);
    }

    public void restart() {
        setProposer(createSubProposer());
        updateLocalComponentSystem.press();
        proposer.init();
    }

}
