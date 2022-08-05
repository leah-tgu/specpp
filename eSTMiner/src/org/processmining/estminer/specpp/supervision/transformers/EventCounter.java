package org.processmining.estminer.specpp.supervision.transformers;

import org.processmining.estminer.specpp.supervision.observations.ClassStatisticKey;
import org.processmining.estminer.specpp.supervision.observations.Count;
import org.processmining.estminer.specpp.supervision.observations.Event;
import org.processmining.estminer.specpp.supervision.observations.EventCountStatistics;
import org.processmining.estminer.specpp.supervision.piping.ObservationSummarizer;
import org.processmining.estminer.specpp.supervision.piping.Observations;
import org.processmining.estminer.specpp.util.datastructures.Counter;

import java.util.Map;

public class EventCounter implements ObservationSummarizer<Event, EventCountStatistics> {
    @Override
    public EventCountStatistics summarize(Observations<? extends Event> events) {
        Counter<Class<? extends Event>> counts = new Counter<>();
        for (Event e : events) {
            counts.inc(e.getClass());
        }
        EventCountStatistics result = new EventCountStatistics();
        for (Map.Entry<Class<? extends Event>, Integer> entry : counts.entrySet()) {
            ClassStatisticKey<Event> key = new ClassStatisticKey<>(entry.getKey());
            result.record(key, new Count(entry.getValue()));
        }
        return result;
    }
}
