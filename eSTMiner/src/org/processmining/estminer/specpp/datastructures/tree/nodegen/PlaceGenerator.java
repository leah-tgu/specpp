package org.processmining.estminer.specpp.datastructures.tree.nodegen;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang.NotImplementedException;
import org.processmining.estminer.specpp.componenting.data.DataRequirements;
import org.processmining.estminer.specpp.componenting.data.ParameterRequirements;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.estminer.specpp.componenting.system.ComponentSystemAwareBuilder;
import org.processmining.estminer.specpp.config.parameters.PlaceGeneratorParameters;
import org.processmining.estminer.specpp.datastructures.BitMask;
import org.processmining.estminer.specpp.datastructures.encoding.BitEncodedSet;
import org.processmining.estminer.specpp.datastructures.encoding.IntEncodings;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.petri.Transition;
import org.processmining.estminer.specpp.datastructures.tree.base.ConstrainableLocalNodeGenerator;
import org.processmining.estminer.specpp.datastructures.tree.base.GenerationConstraint;
import org.processmining.estminer.specpp.datastructures.tree.constraints.*;
import org.processmining.estminer.specpp.datastructures.util.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Contains the entire logic for generating child (and in the future parent) local nodes.
 * Relies only on the state of a given node and the global preset- & postset transition orderings to deterministically compute its children while respecting all received constraints.
 * Guarantees to generate all possible nodes that satisfy the incoming constraints, provided that the constraints monotonically shrink the set of future nodes.
 * That is, if constraints loosen the requirements and allow previously excluded nodes to be generated, these may not be correctly returned.
 */
public class PlaceGenerator implements ConstrainableLocalNodeGenerator<Place, PlaceState, PlaceNode, GenerationConstraint> {


    public enum ExpansionType {
        Postset, Preset;
    }

    private final DepthLimiter depthLimiter;

    private final WiringTester wiringTester;
    private final TransitionBlacklister transitionBlacklister;

    private final IntEncodings<Transition> transitionEncodings;
    private final List<PotentialSetExpansionsFilter> potentialExpansionFilters;
    private final List<ExpansionStopper> expansionStoppers;

    public static class Builder extends ComponentSystemAwareBuilder<PlaceGenerator> {

        private final DelegatingDataSource<IntEncodings<Transition>> transitionEncodings = DataRequirements.ENC_TRANS.emptyDelegator();
        private final DelegatingDataSource<PlaceGeneratorParameters> parameters = new DelegatingDataSource<>();


        public Builder() {
            componentSystemAdapter().require(DataRequirements.ENC_TRANS, transitionEncodings)
                                    .require(ParameterRequirements.parameters("placegenerator.parameters", PlaceGeneratorParameters.class), parameters);
        }

        @Override
        public PlaceGenerator buildIfFullySatisfied() {
            return new PlaceGenerator(transitionEncodings.getData(), parameters.getData());
        }

    }

    public PlaceGenerator(IntEncodings<Transition> transitionEncodings) {
        this(transitionEncodings, PlaceGeneratorParameters.getDefault());
    }


    public PlaceGenerator(IntEncodings<Transition> transitionEncodings, PlaceGeneratorParameters parameters) {
        this.transitionEncodings = transitionEncodings;
        potentialExpansionFilters = new LinkedList<>();
        transitionBlacklister = new TransitionBlacklister(transitionEncodings);
        potentialExpansionFilters.add(transitionBlacklister);
        wiringTester = new WiringTester();
        potentialExpansionFilters.add(wiringTester);
        expansionStoppers = new LinkedList<>();
        depthLimiter = new DepthLimiter(parameters.getMaxTreeDepth());
        expansionStoppers.add(depthLimiter);
    }

    /**
     * @return an empty root node corresponding to the place {@code (∅|∅)} without any existing children
     */
    @Override
    public PlaceNode generateRoot() {
        BitEncodedSet<Transition> preset = BitEncodedSet.empty(transitionEncodings.pre());
        BitEncodedSet<Transition> postset = BitEncodedSet.empty(transitionEncodings.post());
        return PlaceNode.root(Place.of(preset, postset), PlaceState.withPotentialExpansions(getStaticPotentialExpansions(preset), getStaticPotentialExpansions(postset)), this);
    }


    @Override
    public PlaceNode generateParent(PlaceNode child) {
        /*
        Place place = child.getProperties();
        BitEncodedSet<Transition> pre = place.preset(), post = place.postset();

        BitMask presetChildrenMask;
        BitMask postsetChildrenMask;

        if (isPostsetExpansion(place)) {
            // child is postset-expansion/blue edge to parent
            BitEncodedSet<Transition> newPost = place.postset().copy();
            newPost.removeIndex(newPost.maximalIndex());
            post = newPost;
            presetChildrenMask = new BitMask(pre.maxSize());
            postsetChildrenMask = sameTypeYoungerSiblings(post);
        } else {
            // child is preset-expansion/red edge to parent; all the parent's postset expansions were already visited
            BitEncodedSet<Transition> newPre = place.preset().copy();
            newPre.removeIndex(newPre.maximalIndex());
            pre = newPre;
            presetChildrenMask = sameTypeYoungerSiblings(pre);
            postsetChildrenMask = allYoungerSiblings(post);
        }

        Place parent = new Place(pre, post);

        return child.parent(parent, PlaceState.inst(presetChildrenMask, postsetChildrenMask), parent.isEmpty());
        */
        throw new NotImplementedException("the current implementation does not support constraints");
    }

    /*
    private static BitMask allYoungerSiblings(BitEncodedSet<Transition> set) {
        return set.kMaxRangeMask(1);
    }

    private static BitMask sameTypeYoungerSiblings(BitEncodedSet<Transition> set) {
        return set.kMaxRangeMask(2);
    }

    private boolean isPostsetExpansion(Place place) {
        BitEncodedSet<Transition> pre = place.preset();
        BitEncodedSet<Transition> post = place.postset();
        boolean isParentRoot = pre.cardinality() + post.cardinality() == 1;
        return !isParentRoot && post.cardinality() == 1;
    }

*/


    @Override
    public PlaceNode generateChild(PlaceNode parent) {
        Pair<BitMask> potentialExpansions = computePotentialExpansions(parent);
        return makeChild(parent, potentialExpansions, !potentialExpansions.second()
                                                                          .isEmpty() ? ExpansionType.Postset : ExpansionType.Preset);
    }

    /**
     * Creates the next child node to the given parent according to the potential expansion sets.
     * Mutates the internal state of the parent to mark the child's existence.
     * The child's potential expansions are set equal to its parent's minus itself.
     *
     * @param parent              the parent node whose next child is to be generated
     * @param potentialExpansions the pair of potential (preset, postset)-expansions
     * @param expansionType       whether to expand the {@code ExpansionType.Preset} or {@code ExpansionType.Postset}
     * @return the generated child node
     */
    protected PlaceNode makeChild(PlaceNode parent, Pair<BitMask> potentialExpansions, ExpansionType expansionType) {
        BitMask relevant = expansionType == ExpansionType.Postset ? potentialExpansions.second() : potentialExpansions.first();
        int i = relevant.nextSetBit(0);
        Place place = parent.getPlace();
        BitEncodedSet<Transition> presetCopy = place.preset().copy(), postsetCopy = place.postset().copy();
        if (expansionType == ExpansionType.Postset) postsetCopy.addIndex(i);
        else presetCopy.addIndex(i);
        parent.getState().getActualExpansions(expansionType).set(i);
        parent.getState().getPotentialExpansions(expansionType).clear(i);
        relevant.clear(i);

        BitMask childPotentialPresetExpansions = potentialExpansions.first(), childPotentialPostsetExpansions = potentialExpansions.second();
        if (!canHavePresetChildren(place)) childPotentialPresetExpansions = getStaticPotentialExpansions(presetCopy);
        if (!canHavePostsetChildren(place)) childPotentialPostsetExpansions = getStaticPotentialExpansions(postsetCopy);

        PlaceState childState = PlaceState.withPotentialExpansions(childPotentialPresetExpansions, childPotentialPostsetExpansions);
        Place childPlace = new Place(presetCopy, postsetCopy);
        return parent.makeChild(childPlace, childState);
    }

    /**
     * @param parent
     * @return whether {@code parent} has any possible children as computed by {@code computePotentialExpansions()}
     * @see #computePotentialExpansions(PlaceNode)
     */
    @Override
    public boolean hasChildrenLeft(PlaceNode parent) {
        Pair<BitMask> possibleExpansions = computePotentialExpansions(parent);
        return !possibleExpansions.first().isEmpty() || !possibleExpansions.second().isEmpty();
    }

    /**
     * Computes potential preset and postset expansions using all available constraints.
     * Internally updates the queried node state with the computed result.
     * By design of the constraint system, potential expansions are monotonically decreasing subsets.
     *
     * @param parent node whose possible expansions are to be computes for
     * @return pair(presetExpansions, postsetExpansions)
     */
    protected Pair<BitMask> computePotentialExpansions(PlaceNode parent) {
        PlaceState state = parent.getState();

        if (!expansionStoppers.stream().allMatch(es -> es.allowedToExpand(parent))) {
            state.getPotentialPresetExpansions().clear();
            state.getPotentialPostsetExpansions().clear();
            return new Pair<>(new BitMask(), new BitMask());
        } else {
            Place place = parent.getPlace();
            BitMask possiblePresetExpansions = new BitMask(), possiblePostsetExpansions = new BitMask();
            if (canHavePostsetChildren(place))
                possiblePostsetExpansions = computeFilteredPotentialExpansions(state, ExpansionType.Postset);
            if (canHavePresetChildren(place))
                possiblePresetExpansions = computeFilteredPotentialExpansions(state, ExpansionType.Preset);
            return new Pair<>(possiblePresetExpansions, possiblePostsetExpansions);
        }
    }


    /**
     * Computes potential expansions for the specified expansion type.
     * Mutates the state it is querying.
     *
     * @param state         NodeState which is queried and updated
     * @param expansionType the expansion type
     * @return potential expansions represented by a bitmask
     */
    private BitMask computeFilteredPotentialExpansions(PlaceState state, ExpansionType expansionType) {
        BitMask potentialExpansions = expansionType == ExpansionType.Postset ? state.getPotentialPostsetExpansions() : state.getPotentialPresetExpansions();

        for (PotentialSetExpansionsFilter filter : potentialExpansionFilters) {
            filter.filterPotentialSetExpansions(potentialExpansions, expansionType);
        }

        return potentialExpansions.copy();
    }

    /**
     * The potential expansions statically determined solely by the transition set ordering.
     * All transitions greater than the maximum of {@code transitions} are potential expansions.
     *
     * @param transitions ordered subset of transitions
     * @return potential expansions represented by a bitmask
     */
    private BitMask getStaticPotentialExpansions(BitEncodedSet<Transition> transitions) {
        return transitions.kMaxRangeMask(1);
    }

    /**
     * @param place
     * @return whether {@code place} is allowed to have postset expansion children
     */
    private boolean canHavePostsetChildren(Place place) {
        return place.preset().cardinality() > 0 || place.postset().cardinality() == 0;
    }

    /**
     * @param place
     * @return whether {@code place} is allowed to have preset expansion children
     */
    private boolean canHavePresetChildren(Place place) {
        return place.postset().cardinality() == 1;
    }


    /**
     * The number of, at this point, potential children as computed by {@code potentialChildren}.
     *
     * @param parent
     * @return
     * @see #potentialChildren(PlaceNode)
     */
    @Override
    public int potentialChildrenCount(PlaceNode parent) {
        Pair<BitMask> pair = computePotentialExpansions(parent);
        return pair.first().cardinality() + pair.second().cardinality();
    }

    /**
     * Provides a lazily computed iterator of at this point considered potential children.
     * It may be used by tree expansion heuristics.
     *
     * @param parent
     * @return
     */
    @Override
    public Iterable<PlaceNode> potentialChildren(PlaceNode parent) {
        Place place = parent.getPlace();
        PlaceState state = parent.getState();

        Pair<BitMask> pair = computePotentialExpansions(parent);

        Stream<PlaceNode> postsetExpansionsStream = pair.second().stream().mapToObj(i -> {
            BitEncodedSet<Transition> preCopy = place.preset().copy();
            BitEncodedSet<Transition> postCopy = place.postset().copy();
            postCopy.addIndex(i);
            BitMask preExp = state.getPotentialPresetExpansions().copy();
            BitMask postExp = state.getPotentialPostsetExpansions().copy();
            postExp.clear(i);
            return parent.makeChild(Place.of(preCopy, postCopy), PlaceState.withPotentialExpansions(preExp, postExp));
        });

        Stream<PlaceNode> presetExpansionsStream = pair.first().stream().mapToObj(i -> {
            BitEncodedSet<Transition> preCopy = place.preset().copy();
            BitEncodedSet<Transition> postCopy = place.postset().copy();
            preCopy.addIndex(i);
            BitMask preExp = state.getPotentialPresetExpansions().copy();
            BitMask postExp = state.getPotentialPostsetExpansions().copy();
            preExp.clear(i);
            return parent.makeChild(Place.of(preCopy, postCopy), PlaceState.withPotentialExpansions(preExp, postExp));
        });

        return IteratorUtils.asIterable(IteratorUtils.chainedIterator(postsetExpansionsStream.iterator(), presetExpansionsStream.iterator()));
    }


    /**
     * Receives and internally applies generation constraints with {@code ExpansionStopper}s and {@code PotentialSetExpansionsFilter}s.
     * In particular, those are
     * <pre>
     *     {@code DepthConstraints} into the {@code depthLimiter}
     *     {@code WiringConstraints} into the {@code wiringTester}
     *     {@code BlacklistTransition} into the {@code transitionBlacklister}
     *      {@code CullPostsetChildren} into {@code cullChildren()}
     * </pre>
     *
     * @param constraint
     * @see ExpansionStopper
     * @see PotentialSetExpansionsFilter
     */
    @Override
    public void acceptConstraint(GenerationConstraint constraint) {
        if (constraint instanceof DepthConstraint) depthLimiter.setMaxDepth(((DepthConstraint) constraint).getDepth());
        else if (constraint instanceof CullPostsetChildren)
            cullChildren(((CullPostsetChildren) constraint).getAffectedNode(), ExpansionType.Postset);
        else if (constraint instanceof WiringConstraint) {
            if (constraint instanceof AddWiredPlace)
                wiringTester.wire(((WiringConstraint) constraint).getAffectedCandidate());
            else if (constraint instanceof RemoveWiredPlace)
                wiringTester.unwire(((WiringConstraint) constraint).getAffectedCandidate());
        } else if (constraint instanceof BlacklistTransition) {
            transitionBlacklister.blacklist(((BlacklistTransition) constraint).getTransition());
        }
    }

    public void cullChildren(PlaceNode node, ExpansionType expansionType) {
        node.getState().getPotentialExpansions(expansionType).clear();
    }


}
