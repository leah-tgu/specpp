package org.processmining.estminer.specpp.proposal;

import org.processmining.estminer.specpp.base.ConstrainableProposer;
import org.processmining.estminer.specpp.base.ConstraintPublisher;
import org.processmining.estminer.specpp.base.impls.CandidateConstraint;
import org.processmining.estminer.specpp.componenting.data.DataRequirements;
import org.processmining.estminer.specpp.componenting.delegators.ContainerUtils;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.componenting.system.ComponentSystemAwareBuilder;
import org.processmining.estminer.specpp.config.GeneratingTreeConfiguration;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.tree.base.ConstrainableLocalNodeGenerator;
import org.processmining.estminer.specpp.datastructures.tree.base.GenerationConstraint;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.EnumeratingTree;
import org.processmining.estminer.specpp.datastructures.tree.constraints.ClinicallyUnderfedPlace;
import org.processmining.estminer.specpp.datastructures.tree.constraints.CullPostsetChildren;
import org.processmining.estminer.specpp.datastructures.tree.constraints.WiringConstraint;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceGenerator;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceState;
import org.processmining.estminer.specpp.supervision.EventSupervision;
import org.processmining.estminer.specpp.supervision.piping.Observable;
import org.processmining.estminer.specpp.supervision.piping.PipeWorks;
import org.processmining.estminer.specpp.util.JavaTypingUtils;

/**
 * This is the base implementation of a <it>constrainable</it> place proposer.
 * It may receive {@code CandidateConstraint} events and in turn publishes {@code GenerationConstraint} events that may in turn be used by the {@code constrainable generator}.
 *
 * @see PlaceProposer
 * @see CandidateConstraint
 * @see ConstrainableLocalNodeGenerator
 */
public class ConstrainablePlaceProposer extends PlaceProposer implements ConstrainableProposer<Place, CandidateConstraint<Place>>, ConstraintPublisher<GenerationConstraint> {

    private final EventSupervision<GenerationConstraint> evs;

    public ConstrainablePlaceProposer(ConstrainableLocalNodeGenerator<Place, PlaceState, PlaceNode, GenerationConstraint> generator, EnumeratingTree<PlaceNode> tree) {
        super(generator, tree);
        evs = PipeWorks.eventSupervision();
        evs.addObserver(generator);

        componentSystemAdapter().require(SupervisionRequirements.observable("composer.constraints", JavaTypingUtils.castClass(CandidateConstraint.class)), ContainerUtils.observeResults(this))
                                .provide(SupervisionRequirements.observable("proposer.constraints", GenerationConstraint.class, evs));
    }

    @Override
    public Observable<GenerationConstraint> getConstraintPublisher() {
        return evs;
    }

    public static class Builder extends ComponentSystemAwareBuilder<ConstrainablePlaceProposer> {

        private final DelegatingDataSource<GeneratingTreeConfiguration<PlaceNode, PlaceGenerator>> delegatingDataSource = DataRequirements.<PlaceNode, PlaceGenerator>generatingTreeConfiguration()
                                                                                                                                          .emptyDelegator();

        public Builder() {
            componentSystemAdapter().require(DataRequirements.generatingTreeConfiguration(), delegatingDataSource);
        }

        @Override
        protected ConstrainablePlaceProposer buildIfFullySatisfied() {
            GeneratingTreeConfiguration<PlaceNode, PlaceGenerator> config = delegatingDataSource.getData();
            return new ConstrainablePlaceProposer(config.createGenerator(), config.createTree());
        }

    }


    @Override
    public void acceptConstraint(CandidateConstraint<Place> candidateConstraint) {
        if (candidateConstraint instanceof WiringConstraint) {
            evs.observe((GenerationConstraint) candidateConstraint);
        } else if (candidateConstraint instanceof ClinicallyUnderfedPlace)
            evs.observe(new CullPostsetChildren(getPreviousProposedNode()));
    }

}
