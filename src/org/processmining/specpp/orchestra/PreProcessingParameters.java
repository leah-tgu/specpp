package org.processmining.specpp.orchestra;

import com.google.common.collect.ImmutableList;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.processmining.specpp.config.parameters.Parameters;
import org.processmining.specpp.preprocessing.orderings.*;

import java.util.List;
import java.util.Objects;

public class PreProcessingParameters implements Parameters {

    private final XEventClassifier eventClassifier;
    private final boolean addStartEndTransitions;
    private final Class<? extends ActivityOrderingStrategy> activityOrderingStrategy;

    public PreProcessingParameters(XEventClassifier eventClassifier, boolean addStartEndTransitions, Class<? extends ActivityOrderingStrategy> activityOrderingStrategy) {
        this.eventClassifier = eventClassifier;
        this.addStartEndTransitions = addStartEndTransitions;
        this.activityOrderingStrategy = activityOrderingStrategy;
    }

    public static PreProcessingParameters getDefault() {
        return new PreProcessingParameters(new XEventNameClassifier(), true, AverageFirstOccurrenceIndex.class);
    }

    public static List<Class<? extends ActivityOrderingStrategy>> getAvailableTransitionEncodingsBuilders() {
        return ImmutableList.of(AverageFirstOccurrenceIndex.class, AbsoluteTraceFrequency.class, AbsoluteActivityFrequency.class, Lexicographic.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PreProcessingParameters that = (PreProcessingParameters) o;
        return addStartEndTransitions == that.addStartEndTransitions && Objects.equals(eventClassifier, that.eventClassifier) && Objects.equals(activityOrderingStrategy, that.activityOrderingStrategy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventClassifier, addStartEndTransitions, activityOrderingStrategy);
    }

    @Override
    public String toString() {
        return "PreProcessingParameters{" + "eventClassifierBuilder=" + eventClassifier + ", addStartEndTransitions=" + addStartEndTransitions + ", transitionEncodingsBuilderClass=" + activityOrderingStrategy + '}';
    }

    public Class<? extends ActivityOrderingStrategy> getActivityOrderingStrategy() {
        return activityOrderingStrategy;
    }

    public boolean isAddStartEndTransitions() {
        return addStartEndTransitions;
    }

    public XEventClassifier getEventClassifier() {
        return eventClassifier;
    }
}
