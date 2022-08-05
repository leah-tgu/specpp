package org.processmining.estminer.specpp.datastructures.tree.nodegen;

import org.apache.commons.collections4.IteratorUtils;
import org.processmining.estminer.specpp.componenting.data.DataRequirements;
import org.processmining.estminer.specpp.componenting.data.ParameterRequirements;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.estminer.specpp.componenting.system.AbstractComponentSystemUser;
import org.processmining.estminer.specpp.componenting.system.ComponentSystemAwareBuilder;
import org.processmining.estminer.specpp.config.parameters.PlaceGeneratorParameters;
import org.processmining.estminer.specpp.datastructures.BitMask;
import org.processmining.estminer.specpp.datastructures.encoding.BitEncodedSet;
import org.processmining.estminer.specpp.datastructures.encoding.IntEncoding;
import org.processmining.estminer.specpp.datastructures.encoding.IntEncodings;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.petri.Transition;
import org.processmining.estminer.specpp.datastructures.tree.base.ConstrainableLocalNodeGenerator;
import org.processmining.estminer.specpp.datastructures.tree.base.GenerationConstraint;
import org.processmining.estminer.specpp.datastructures.tree.constraints.*;
import org.processmining.estminer.specpp.datastructures.util.Pair;

import java.util.Iterator;

public class PlaceGenerator extends AbstractComponentSystemUser implements ConstrainableLocalNodeGenerator<Place, PlaceState, PlaceNode, GenerationConstraint> {

    private final WiringTester wiringTester;
    private final TransitionBlacklister transitionBlacklister;
    private final IntEncodings<Transition> transitionEncodings;

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
        maxDepth = parameters.getMaxTreeDepth();
        wiringTester = new WiringTester();
        transitionBlacklister = new TransitionBlacklister(transitionEncodings);
    }

    protected IntEncoding<Transition> getPresetEncoding() {
        return transitionEncodings.getPresetEncoding();
    }

    protected IntEncoding<Transition> getPostsetEncoding() {
        return transitionEncodings.getPostsetEncoding();
    }

    @Override
    public PlaceNode generateRoot() {
        return PlaceNode.root(new Place(BitEncodedSet.empty(getPresetEncoding()), BitEncodedSet.empty(getPostsetEncoding())), this);
    }

    public enum SetExpansionPriority {
        Preset, Postset

    }
    //private final SetExpansionPriority priority;

    private int maxDepth;


    protected void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public boolean isBelowDepthLimit(PlaceNode node) {
        return node.getDepth() < maxDepth;
    }

    @Override
    public PlaceNode generateParent(PlaceNode child) {
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
    }

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

    // mutates node state, i.e. adds generated child
    @Override
    public PlaceNode generateChild(PlaceNode parent) {
        Place place = parent.getProperties();
        PlaceState state = parent.getState();

        if (isBelowDepthLimit(parent)) {
            if (canHavePostsetChildren(place)) {
                BitEncodedSet<Transition> possiblePostsetExpansions = possiblePostsetExpansions(place, state);
                if (!possiblePostsetExpansions.isEmpty()) {
                    return parent.child(generatePostsetExpandedPlace(place, state, possiblePostsetExpansions));
                }
            }
            if (canHavePresetChildren(place)) {
                BitEncodedSet<Transition> possiblePresetExpansions = possiblePresetExpansions(place, state);
                if (!possiblePresetExpansions.isEmpty()) {
                    return parent.child(generatePresetExpandedPlace(place, state, possiblePresetExpansions));
                }
            }
        }

        return null;
    }

    private Place generatePostsetExpandedPlace(Place parent, PlaceState parentState, BitEncodedSet<Transition> postsetExpansions) {
        return new Place(parent.preset(), expandSet(parent.postset(), postsetExpansions, parentState.getPostsetMasks()));
    }

    private Place generatePresetExpandedPlace(Place parent, PlaceState parentState, BitEncodedSet<Transition> presetExpansions) {
        return new Place(expandSet(parent.preset(), presetExpansions, parentState.getPresetMasks()), parent.postset());
    }

    private BitEncodedSet<Transition> expandSet(BitEncodedSet<Transition> baseSet, BitEncodedSet<Transition> possibleExpansions, Pair<BitMask> actualExpansionMasks) {
        int i = possibleExpansions.minimalIndex();
        actualExpansionMasks.first().set(i);

        BitMask maxFutureExpansions = actualExpansionMasks.second();
        updateAdditionalStateInfo(maxFutureExpansions, possibleExpansions.getBitMask());
        maxFutureExpansions.clear(i);

        BitEncodedSet<Transition> copy = baseSet.copy();
        copy.addIndex(i);
        return copy;
    }

    private void updateAdditionalStateInfo(BitMask currentMaxFutureExpansions, BitMask maxFutureExpansions) {
        currentMaxFutureExpansions.and(maxFutureExpansions);
    }

    private void updateAdditionalStateInfo(PlaceState placeState, BitMask maxFutureExpansions, boolean updatingPostset) {
        if (updatingPostset) updateAdditionalStateInfo(placeState.getMaximalFuturePostsetChildrenMask(), maxFutureExpansions);
        else updateAdditionalStateInfo(placeState.getMaximalFuturePresetChildrenMask(), maxFutureExpansions);
    }

    @Override
    public boolean hasChildrenLeft(PlaceNode parent) {
        PlaceState state = parent.getState();
        Place place = parent.getProperties();

        int c = 0;

        if (isBelowDepthLimit(parent)) {
            if (canHavePostsetChildren(place)) {
                BitEncodedSet<Transition> possiblePostsetExpansions = possiblePostsetExpansions(place, state);
                updateAdditionalStateInfo(parent.getState(), possiblePostsetExpansions.getBitMask(), true);
                c += possiblePostsetExpansions.cardinality();
            }
            if (canHavePresetChildren(place)) {
                BitEncodedSet<Transition> possiblePresetExpansions = possiblePresetExpansions(place, state);
                updateAdditionalStateInfo(parent.getState(), possiblePresetExpansions.getBitMask(), false);
                c += possiblePresetExpansions.cardinality();
            }
        }

        return c > 0;
    }

    private BitEncodedSet<Transition> possiblePostsetExpansions(Place place, PlaceState state) {
        return possiblePlaceExpansions(place, state, true);
    }

    private BitEncodedSet<Transition> possiblePresetExpansions(Place place, PlaceState state) {
        return possiblePlaceExpansions(place, state, false);
    }

    private BitEncodedSet<Transition> possiblePlaceExpansions(Place place, PlaceState state, boolean isPostsetExpansion) {
        BitEncodedSet<Transition> expansions = potentialChildrenSet(isPostsetExpansion ? place.postset() : place.preset());
        expansions.clearMask(isPostsetExpansion ? state.getPostsetChildrenMask() : state.getPresetChildrenMask());

        transitionBlacklister.filterPotentialSetExpansions(expansions, isPostsetExpansion);
        wiringTester.filterPotentialSetExpansions(expansions, isPostsetExpansion);

        return expansions;
    }

    public void cullPresetChildren(PlaceNode node) {
        node.getState().setPresetChildrenMask(potentialChildrenMask(node.getProperties().preset()));
    }

    public void cullPostsetChildren(PlaceNode node) {
        node.getState().setPostsetChildrenMask(potentialChildrenMask(node.getProperties().postset()));
    }

    private BitMask potentialChildrenMask(BitEncodedSet<Transition> transitions) {
        return transitions.kMaxRangeMask(1);
    }

    private BitEncodedSet<Transition> potentialChildrenSet(BitEncodedSet<Transition> transitions) {
        return new BitEncodedSet<>(transitions.getEncoding(), potentialChildrenMask(transitions));
    }

    private boolean canHavePostsetChildren(Place place) {
        return place.preset().cardinality() > 0 || place.postset().cardinality() == 0;
    }

    private boolean canHavePresetChildren(Place place) {
        return place.postset().cardinality() == 1;
    }


    @Override
    public int potentialChildrenCount(PlaceNode parent) {
        if (!isBelowDepthLimit(parent)) return 0;

        PlaceState state = parent.getState();
        Place place = parent.getProperties();
        int i = 0;
        if (canHavePostsetChildren(place)) i += possiblePostsetExpansions(place, state).cardinality();
        if (canHavePresetChildren(place)) i += possiblePostsetExpansions(place, state).cardinality();
        return i;
    }

    @Override
    public Iterable<PlaceNode> potentialChildren(PlaceNode parent) {
        PlaceState state = parent.getState();
        Place place = parent.getProperties();
        BitEncodedSet<Transition> pre = place.preset();
        BitEncodedSet<Transition> post = place.postset();

        Iterator<PlaceNode> result = IteratorUtils.emptyIterator();

        if (canHavePostsetChildren(place)) {
            result = possiblePostsetExpansions(place, state).getBitMask().stream().mapToObj(poId -> {
                BitEncodedSet<Transition> newPost = post.copy();
                newPost.addIndex(poId);
                return parent.child(new Place(pre, newPost));
            }).iterator();
        }

        if (canHavePresetChildren(place)) {
            result = IteratorUtils.chainedIterator(result, possiblePresetExpansions(place, state).getBitMask()
                                                                                                 .stream()
                                                                                                 .mapToObj(prId -> {
                                                                                                     BitEncodedSet<Transition> newPre = pre.copy();
                                                                                                     newPre.addIndex(prId);
                                                                                                     return parent.child(new Place(newPre, post));
                                                                                                 })
                                                                                                 .iterator());
        }
        return IteratorUtils.asIterable(result);
    }


    @Override
    public void acceptConstraint(GenerationConstraint constraint) {
        if (constraint instanceof DepthConstraint)
            setMaxDepth(Math.min(getMaxDepth(), ((DepthConstraint) (constraint)).getDepth()));
        else if (constraint instanceof CullPostsetChildren)
            cullPostsetChildren(((CullPostsetChildren) constraint).getAffectedNode());
        else if (constraint instanceof WiringConstraint) {
            if (constraint instanceof AddWiredPlace)
                wiringTester.wire(((WiringConstraint) constraint).getAffectedCandidate());
            else if (constraint instanceof RemoveWiredPlace)
                wiringTester.unwire(((WiringConstraint) constraint).getAffectedCandidate());
        } else if (constraint instanceof BlacklistTransition) {
            transitionBlacklister.blacklist(((BlacklistTransition) constraint).getTransition());
        }
    }


}
