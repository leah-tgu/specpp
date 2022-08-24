package org.processmining.estminer.specpp.proposal;

import org.processmining.estminer.specpp.base.ConstrainableProposer;
import org.processmining.estminer.specpp.base.Constrainer;
import org.processmining.estminer.specpp.base.impls.CandidateConstraint;
import org.processmining.estminer.specpp.componenting.data.DataRequirements;
import org.processmining.estminer.specpp.componenting.delegators.ContainerUtils;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.componenting.system.ComponentSystemAwareBuilder;
import org.processmining.estminer.specpp.componenting.system.link.ChildGenerationLogicComponent;
import org.processmining.estminer.specpp.componenting.system.link.EfficientTreeComponent;
import org.processmining.estminer.specpp.config.EfficientTreeConfiguration;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.tree.base.ConstrainableChildGenerationLogic;
import org.processmining.estminer.specpp.datastructures.tree.base.GenerationConstraint;
import org.processmining.estminer.specpp.datastructures.tree.constraints.*;
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
 * @see ConstrainableChildGenerationLogic
 */
public class ConstrainablePlaceProposer extends PlaceProposer implements ConstrainableProposer<Place, CandidateConstraint<Place>>, Constrainer<GenerationConstraint> {
    public static class Builder extends ComponentSystemAwareBuilder<ConstrainablePlaceProposer> {

        private final DelegatingDataSource<EfficientTreeConfiguration<Place, PlaceState, PlaceNode>> delegatingDataSource = new DelegatingDataSource<>();

        public Builder() {
            componentSystemAdapter().require(DataRequirements.efficientTreeConfiguration(), delegatingDataSource);
        }

        @Override
        protected ConstrainablePlaceProposer buildIfFullySatisfied() {
            EfficientTreeConfiguration<Place, PlaceState, PlaceNode> config = delegatingDataSource.getData();
            return new ConstrainablePlaceProposer(config.createPossiblyInstrumentedChildGenerationLogic(), config.createPossiblyInstrumentedTree());
        }

    }

    protected final EventSupervision<GenerationConstraint> constraintOutput = PipeWorks.eventSupervision();

    public ConstrainablePlaceProposer(ChildGenerationLogicComponent<Place, PlaceState, PlaceNode> cgl, EfficientTreeComponent<PlaceNode> tree) {
        super(cgl, tree);
        componentSystemAdapter().provide(SupervisionRequirements.observable("proposer.constraints", getPublishedConstraintClass(), getConstraintPublisher()));
        localComponentSystem().require(SupervisionRequirements.observable(SupervisionRequirements.regex("composer\\.constraints.*"), getAcceptedConstraintClass()), ContainerUtils.observeResults(this))
                              .require(SupervisionRequirements.observable(SupervisionRequirements.regex("composition\\.constraints.*"), getAcceptedConstraintClass()), ContainerUtils.observeResults(this))
                              .provide(SupervisionRequirements.observable("proposer.constraints", getPublishedConstraintClass(), getConstraintPublisher()));
    }


    @Override
    public void acceptConstraint(CandidateConstraint<Place> candidateConstraint) {
        PlaceNode placeNode = getPreviousProposedNode();
        if (candidateConstraint instanceof WiringConstraint) {
            constraintOutput.observe((GenerationConstraint) candidateConstraint);
        } else if (candidateConstraint instanceof ClinicallyUnderfedPlace) {
            constraintOutput.observe(new CullPostsetChildren(placeNode));
        } else if (candidateConstraint instanceof ClinicallyOverfedPlace) {
            if (placeNode.getState().getPotentialPostsetExpansions().isEmpty()) {
                constraintOutput.observe(new CullPresetChildren(placeNode));
            }
        }
    }

    @Override
    public Observable<GenerationConstraint> getConstraintPublisher() {
        return constraintOutput;
    }

    @Override
    public Class<GenerationConstraint> getPublishedConstraintClass() {
        return GenerationConstraint.class;
    }

    @Override
    public Class<CandidateConstraint<Place>> getAcceptedConstraintClass() {
        return JavaTypingUtils.castClass(CandidateConstraint.class);
    }


}
