package org.processmining.estminer.specpp.representations.log.impls;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.SetUtils;
import org.processmining.estminer.specpp.config.SimpleBuilder;
import org.processmining.estminer.specpp.representations.encoding.HashmapEncoding;
import org.processmining.estminer.specpp.representations.encoding.IntEncoding;
import org.processmining.estminer.specpp.representations.encoding.IntEncodings;
import org.processmining.estminer.specpp.representations.log.Activity;
import org.processmining.estminer.specpp.representations.log.Log;
import org.processmining.estminer.specpp.representations.petri.Transition;
import org.processmining.estminer.specpp.util.datastructures.Pair;

import java.util.*;
import java.util.stream.Collectors;

public abstract class TransitionEncodingsBuilder implements SimpleBuilder<IntEncodings<Transition>> {

    private final Log log;
    private final Map<String, Activity> activityMapping;
    private final BidiMap<Activity, Transition> transitionMapping;

    public TransitionEncodingsBuilder(Log log, Map<String, Activity> activityMapping, BidiMap<Activity, Transition> transitionMapping) {
        this.log = log;
        this.activityMapping = activityMapping;
        this.transitionMapping = transitionMapping;
    }

    protected static IntEncoding<Transition> createPresetEncoding(Collection<Activity> activities, Comparator<Activity> comparator, BidiMap<Activity, Transition> mapping) {
        return createEncoding(activities, SetUtils.unmodifiableSet(Factory.ARTIFICIAL_END), comparator, mapping);
    }

    protected static IntEncoding<Transition> createPostsetEncoding(Collection<Activity> activities, Comparator<Activity> comparator, BidiMap<Activity, Transition> mapping) {
        return createEncoding(activities, SetUtils.unmodifiableSet(Factory.ARTIFICIAL_START), comparator, mapping);
    }

    protected static IntEncoding<Transition> createEncoding(Collection<Activity> activities, Set<Activity> toIgnore, Comparator<Activity> comparator, BidiMap<Activity, Transition> mapping) {
        TreeSet<Activity> set = new TreeSet<>(comparator);
        set.addAll(activities);
        set.removeAll(toIgnore);
        List<Transition> list = set.stream().map(mapping::get).collect(Collectors.toList());
        return new HashmapEncoding<>(list);
    }


    protected abstract Pair<Comparator<Activity>> computeActivityOrderings(Log log, Map<String, Activity> mapping);


    @Override
    public IntEncodings<Transition> build() {


        Pair<Comparator<Activity>> comparators = computeActivityOrderings(log, activityMapping);

        IntEncoding<Transition> presetEncoding = TransitionEncodingsBuilder.createPresetEncoding(activityMapping.values(), comparators.first(), transitionMapping);
        IntEncoding<Transition> postsetEncoding = TransitionEncodingsBuilder.createPostsetEncoding(activityMapping.values(), comparators.second(), transitionMapping);

        return new IntEncodings<>(presetEncoding, postsetEncoding);
    }
}
