package org.processmining.estminer.specpp.proposal;

import org.processmining.estminer.specpp.base.ConstrainableProposer;
import org.processmining.estminer.specpp.base.Constrainer;
import org.processmining.estminer.specpp.base.impls.CandidateConstraint;
import org.processmining.estminer.specpp.componenting.data.DataRequirements;
import org.processmining.estminer.specpp.componenting.delegators.ContainerUtils;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.componenting.system.ComponentSystemAwareBuilder;
import org.processmining.estminer.specpp.config.EfficientTreeConfiguration;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.tree.base.ConstrainableChildGenerationLogic;
import org.processmining.estminer.specpp.datastructures.tree.base.EfficientTree;
import org.processmining.estminer.specpp.datastructures.tree.base.GenerationConstraint;
import org.processmining.estminer.specpp.datastructures.tree.constraints.*;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.MonotonousPlaceGenerationLogic;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.estminer.specpp.supervision.EventSupervision;
import org.processmining.estminer.specpp.supervision.piping.Observable;
import org.processmining.estminer.specpp.supervision.piping.PipeWorks;
import org.processmining.estminer.specpp.supervision.supervisors.DebuggingSupervisor;
import org.processmining.estminer.specpp.util.JavaTypingUtils;

/**
 * This is the base implementation of a <it>constrainable</it> place proposer.
 * It may receive {@code CandidateConstraint} events and in turn publishes {@code GenerationConstraint} events that may in turn be used by the {@code constrainable generator}.
 *
 * @see PlaceProposer
 * @see CandidateConstraint
 * @see ConstrainableChildGenerationLogic
 */
public class ConstrainablePlaceProposer extends PlaceProposer<MonotonousPlaceGenerationLogic> implements ConstrainableProposer<Place, CandidateConstraint<Place>>, Constrainer<GenerationConstraint> {
    public static class Builder extends ComponentSystemAwareBuilder<ConstrainablePlaceProposer> {

        private final DelegatingDataSource<EfficientTreeConfiguration<PlaceNode, MonotonousPlaceGenerationLogic>> delegatingDataSource = DataRequirements.<PlaceNode, MonotonousPlaceGenerationLogic>generatingTreeConfiguration()
                                                                                                                                                         .emptyDelegator();

        public Builder() {
            componentSystemAdapter().require(DataRequirements.generatingTreeConfiguration(), delegatingDataSource);
        }

        @Override
        protected ConstrainablePlaceProposer buildIfFullySatisfied() {
            EfficientTreeConfiguration<PlaceNode, MonotonousPlaceGenerationLogic> config = delegatingDataSource.getData();
            return new ConstrainablePlaceProposer(config.createChildGenerationLogic(), config.createTree());
        }

    }

    protected final EventSupervision<GenerationConstraint> constraintOutput = PipeWorks.eventSupervision();

    public ConstrainablePlaceProposer(MonotonousPlaceGenerationLogic pgl, EfficientTree<PlaceNode> tree) {
        super(pgl, tree);
        componentSystemAdapter().provide(SupervisionRequirements.observable("proposer.constraints", getPublishedConstraintClass(), getConstraintPublisher()));
        localComponentSystem().require(SupervisionRequirements.observable(SupervisionRequirements.regex("composer\\.constraints.*"), getAcceptedConstraintClass()), ContainerUtils.observeResults(this))
                              .provide(SupervisionRequirements.observable("proposer.constraints", getPublishedConstraintClass(), getConstraintPublisher()));
    }


    @Override
    public void acceptConstraint(CandidateConstraint<Place> candidateConstraint) {
        PlaceNode placeNode = getPreviousProposedNode();
        if (!candidateConstraint.getAffectedCandidate().equals(placeNode.getPlace()))
            DebuggingSupervisor.debug("constr place prop", candidateConstraint + " vs " + placeNode);
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
