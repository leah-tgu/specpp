package org.processmining.estminer.specpp.representations.tree.nodegen;

import org.apache.commons.collections4.IteratorUtils;
import org.processmining.estminer.specpp.componenting.data.DataRequirements;
import org.processmining.estminer.specpp.componenting.data.ParameterRequirements;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.estminer.specpp.componenting.system.AbstractComponentSystemUser;
import org.processmining.estminer.specpp.componenting.system.ComponentSystemAwareBuilder;
import org.processmining.estminer.specpp.config.parameters.PlaceGeneratorParameters;
import org.processmining.estminer.specpp.est.PlaceNode;
import org.processmining.estminer.specpp.est.PlaceState;
import org.processmining.estminer.specpp.representations.BitMask;
import org.processmining.estminer.specpp.representations.encoding.BitEncodedSet;
import org.processmining.estminer.specpp.representations.encoding.IntEncoding;
import org.processmining.estminer.specpp.representations.encoding.IntEncodings;
import org.processmining.estminer.specpp.representations.petri.Place;
import org.processmining.estminer.specpp.representations.petri.Transition;
import org.processmining.estminer.specpp.representations.tree.base.ConstrainableLocalNodeGenerator;
import org.processmining.estminer.specpp.representations.tree.base.GenerationConstraint;
import org.processmining.estminer.specpp.representations.tree.constraints.*;

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
        Preset, Postset;

    }
    //private final SetExpansionPriority priority;

    private int maxDepth;


    protected void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public int getMaxDepth() {
        return maxDepth;
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

        if (hasPostsetChildren(place)) {
            BitEncodedSet<Transition> postsetExpansions = possiblePostsetExpansions(place, state);
            if (!postsetExpansions.isEmpty()) {
                return parent.child(generatePostsetExpandedPlace(place, state, postsetExpansions));
            }
        }
        if (hasPresetChildren(place)) {
            BitEncodedSet<Transition> presetExpansions = possiblePresetExpansions(place, state);
            if (!presetExpansions.isEmpty()) {
                return parent.child(generatePresetExpandedPlace(place, state, presetExpansions));
            }
        }

        return null;
    }

    private Place generatePostsetExpandedPlace(Place parent, PlaceState parentState, BitEncodedSet<Transition> postsetExpansions) {
        return new Place(parent.preset(), expandSet(parent.postset(), postsetExpansions, parentState.getPostsetChildrenMask()));
    }

    private Place generatePresetExpandedPlace(Place parent, PlaceState parentState, BitEncodedSet<Transition> presetExpansions) {
        return new Place(expandSet(parent.preset(), presetExpansions, parentState.getPresetChildrenMask()), parent.postset());
    }

    private BitEncodedSet<Transition> expandSet(BitEncodedSet<Transition> baseSet, BitEncodedSet<Transition> possibleExpansions, BitMask actualExpansions) {
        int i = possibleExpansions.minimalIndex();
        actualExpansions.set(i);
        BitEncodedSet<Transition> copy = baseSet.copy();
        copy.addIndex(i);
        return copy;
    }

    @Override
    public boolean hasChildrenLeft(PlaceNode parent) {
        PlaceState state = parent.getState();
        Place place = parent.getProperties();
        return place.size() < getMaxDepth() && ((hasPostsetChildren(place) && !possiblePostsetExpansions(place, state).isEmpty()) || (hasPresetChildren(place) && !possiblePresetExpansions(place, state).isEmpty()));
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

    private boolean hasPostsetChildren(Place place) {
        return place.preset().cardinality() > 0 || place.postset().cardinality() == 0;
    }

    private boolean hasPresetChildren(Place place) {
        return place.postset().cardinality() == 1;
    }


    @Override
    public int potentialChildrenCount(PlaceNode parent) {
        PlaceState state = parent.getState();
        Place place = parent.getProperties();
        int i = 0;
        if (hasPostsetChildren(place)) i += possiblePostsetExpansions(place, state).cardinality();
        if (hasPresetChildren(place)) i += possiblePostsetExpansions(place, state).cardinality();
        return i;
    }

    @Override
    public Iterable<PlaceNode> potentialChildren(PlaceNode parent) {
        PlaceState state = parent.getState();
        Place place = parent.getProperties();
        BitEncodedSet<Transition> pre = place.preset();
        BitEncodedSet<Transition> post = place.postset();

        Iterator<PlaceNode> result = IteratorUtils.emptyIterator();

        if (hasPostsetChildren(place)) {
            result = possiblePostsetExpansions(place, state).getBitMask().stream().mapToObj(poId -> {
                BitEncodedSet<Transition> newPost = post.copy();
                newPost.addIndex(poId);
                return parent.child(new Place(pre, newPost));
            }).iterator();
        }

        if (hasPresetChildren(place)) {
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
        else if (false && constraint instanceof WiringConstraint) {
            if (constraint instanceof AddWiredPlace)
                wiringTester.wire(((WiringConstraint) constraint).getAffectedCandidate());
            else if (constraint instanceof RemoveWiredPlace)
                wiringTester.unwire(((WiringConstraint) constraint).getAffectedCandidate());
        } else if (constraint instanceof BlacklistTransition) {
            transitionBlacklister.blacklist(((BlacklistTransition) constraint).getTransition());
        }
    }


}
