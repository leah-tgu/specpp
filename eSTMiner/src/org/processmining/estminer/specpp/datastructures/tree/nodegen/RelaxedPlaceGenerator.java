package org.processmining.estminer.specpp.datastructures.tree.nodegen;

import org.processmining.estminer.specpp.config.parameters.PlaceGeneratorParameters;
import org.processmining.estminer.specpp.datastructures.encoding.BitMask;
import org.processmining.estminer.specpp.datastructures.encoding.IntEncodings;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.petri.Transition;
import org.processmining.estminer.specpp.datastructures.tree.base.GenerationConstraint;
import org.processmining.estminer.specpp.datastructures.tree.constraints.AddWiredPlace;
import org.processmining.estminer.specpp.datastructures.tree.constraints.DepthConstraint;
import org.processmining.estminer.specpp.datastructures.tree.constraints.RemoveWiredPlace;
import org.processmining.estminer.specpp.datastructures.tree.constraints.WiringConstraint;
import org.processmining.estminer.specpp.datastructures.util.ImmutablePair;
import org.processmining.estminer.specpp.datastructures.util.Pair;


/**
 * A subclass of {@code MonotonousPlaceGenerator} with relaxed requirements as well as guarantees.
 * It operates along the relaxed premise that widening constraints, e.g. removing constraints like {@code RemoveWiredPlace}, will at least be correctly considered in the following node expansions.
 * As a difference to the aforementioned base version, the potential expansion bitmasks stored in the node state are not shrunk to keep up with <it>currently</it> active constraints.
 *
 * @see MonotonousPlaceGenerator
 * @deprecated Not functioning as expected. Quite the opposite in fact.
 */
@Deprecated
public class RelaxedPlaceGenerator extends MonotonousPlaceGenerator {
    public RelaxedPlaceGenerator(IntEncodings<Transition> transitionEncodings) {
        super(transitionEncodings);
    }

    public RelaxedPlaceGenerator(IntEncodings<Transition> transitionEncodings, PlaceGeneratorParameters parameters) {
        super(transitionEncodings, parameters);
    }

    public static class Builder extends MonotonousPlaceGenerator.Builder {
        @Override
        public RelaxedPlaceGenerator buildIfFullySatisfied() {
            return new RelaxedPlaceGenerator(transitionEncodings.getData(), parameters.getData());
        }
    }


    @Override
    protected void handleWiringConstraint(WiringTester wiringTester, GenerationConstraint constraint) {
        if (constraint instanceof AddWiredPlace)
            wiringTester.wire(((WiringConstraint) constraint).getAffectedCandidate());
        else if (constraint instanceof RemoveWiredPlace)
            wiringTester.unwire(((WiringConstraint) constraint).getAffectedCandidate());
    }

    @Override
    protected void handleDepthConstraint(DepthLimiter depthLimiter, GenerationConstraint constraint) {
        depthLimiter.setMaxDepth(((DepthConstraint) constraint).getDepthLimit());
    }


    @Override
    protected BitMask computeFilteredPotentialExpansions(PlaceState state, ExpansionType expansionType) {
        BitMask potentialExpansions = expansionType == ExpansionType.Postset ? state.getPotentialPostsetExpansions() : state.getPotentialPresetExpansions();

        potentialExpansions = potentialExpansions.copy();

        for (PotentialExpansionsFilter filter : potentialExpansionFilters) {
            filter.filterPotentialSetExpansions(potentialExpansions, expansionType);
        }

        return potentialExpansions;
    }

    @Override
    protected Pair<BitMask> computePotentialExpansions(PlaceNode parent) {
        PlaceState state = parent.getState();

        if (expansionStoppers.stream().anyMatch(es -> es.notAllowedToExpand(parent))) {
            cullChildren(parent, ExpansionType.Preset);
            cullChildren(parent, ExpansionType.Postset);
            return new ImmutablePair<>(new BitMask(), new BitMask());
        } else {
            Place place = parent.getPlace();
            BitMask possiblePresetExpansions = new BitMask(), possiblePostsetExpansions = new BitMask();
            // the filtering potential expansion computations cannot be used to update the state and have to be discarded,
            // so it is more efficient to only compute preset expansions, if no postset expansions are possible
            if (canHavePostsetChildren(place))
                possiblePostsetExpansions = computeFilteredPotentialExpansions(state, ExpansionType.Postset);
            else if (possiblePostsetExpansions.isEmpty() && canHavePresetChildren(place))
                possiblePresetExpansions = computeFilteredPotentialExpansions(state, ExpansionType.Preset);

            return new ImmutablePair<>(possiblePresetExpansions, possiblePostsetExpansions);
        }
    }

    /**
     * An adaption to support the relaxed assumptions of this type of node generator.
     * Instead of relying on the {@code potentialExpansion}s in the place state, which cannot be used with these relaxed assumptions, the subtree cutoff is implemented via saturating the actual expansions.
     *
     * @param node
     * @param expansionType
     */
    @Override
    public void cullChildren(PlaceNode node, ExpansionType expansionType) {
        BitMask mask = getStaticPotentialExpansions(expansionType == ExpansionType.Postset ? node.getPlace()
                                                                                                 .postset() : node.getPlace()
                                                                                                                  .preset());
        node.getState().getActualExpansions(expansionType).and(mask);
    }
}
