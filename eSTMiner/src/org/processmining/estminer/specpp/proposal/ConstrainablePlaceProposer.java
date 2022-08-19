package org.processmining.estminer.specpp.proposal;

import org.processmining.estminer.specpp.base.impls.AbstractConstrainableGeneratingTreeProposer;
import org.processmining.estminer.specpp.base.impls.CandidateConstraint;
import org.processmining.estminer.specpp.componenting.data.DataRequirements;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.componenting.system.ComponentSystemAwareBuilder;
import org.processmining.estminer.specpp.config.GeneratingTreeConfiguration;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.tree.base.ConstrainableLocalNodeGenerator;
import org.processmining.estminer.specpp.datastructures.tree.base.GenerationConstraint;
import org.processmining.estminer.specpp.datastructures.tree.base.PlaceGenerator;
import org.processmining.estminer.specpp.datastructures.tree.constraints.ClinicallyUnderfedPlace;
import org.processmining.estminer.specpp.datastructures.tree.constraints.CullPostsetChildren;
import org.processmining.estminer.specpp.datastructures.tree.constraints.WiringConstraint;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.MonotonousPlaceGenerator;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.estminer.specpp.util.JavaTypingUtils;

/**
 * This is the base implementation of a <it>constrainable</it> place proposer.
 * It may receive {@code CandidateConstraint} events and in turn publishes {@code GenerationConstraint} events that may in turn be used by the {@code constrainable generator}.
 *
 * @see PlaceProposer
 * @see CandidateConstraint
 * @see ConstrainableLocalNodeGenerator
 */
public class ConstrainablePlaceProposer extends AbstractConstrainableGeneratingTreeProposer<Place, PlaceNode, CandidateConstraint<Place>, GenerationConstraint> {
    public static class Builder extends ComponentSystemAwareBuilder<ConstrainablePlaceProposer> {

        private final DelegatingDataSource<GeneratingTreeConfiguration<PlaceNode, MonotonousPlaceGenerator>> delegatingDataSource = DataRequirements.<PlaceNode, MonotonousPlaceGenerator>generatingTreeConfiguration()
                                                                                                                                                    .emptyDelegator();

        public Builder() {
            componentSystemAdapter().require(DataRequirements.generatingTreeConfiguration(), delegatingDataSource);
        }

        @Override
        protected ConstrainablePlaceProposer buildIfFullySatisfied() {
            GeneratingTreeConfiguration<PlaceNode, MonotonousPlaceGenerator> config = delegatingDataSource.getData();
            PlaceProposer<MonotonousPlaceGenerator> pp = new PlaceProposer<>(config.createGenerator(), config.createTree());
            return new ConstrainablePlaceProposer(pp);
        }

    }

    public ConstrainablePlaceProposer(PlaceProposer<? extends PlaceGenerator> delegate) {
        super(delegate);
        gcr.provide(SupervisionRequirements.observable("proposer.constraints", GenerationConstraint.class, constraintOutput));
    }

    @Override
    public void acceptConstraint(CandidateConstraint<Place> candidateConstraint) {
        if (candidateConstraint instanceof WiringConstraint) {
            constraintOutput.observe((GenerationConstraint) candidateConstraint);
        } else if (candidateConstraint instanceof ClinicallyUnderfedPlace)
            constraintOutput.observe(new CullPostsetChildren(delegate.getPreviousProposedNode()));
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
