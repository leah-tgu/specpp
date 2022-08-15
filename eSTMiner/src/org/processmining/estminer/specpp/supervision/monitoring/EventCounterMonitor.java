package org.processmining.estminer.specpp.supervision.monitoring;

import org.processmining.estminer.specpp.datastructures.util.Counter;
import org.processmining.estminer.specpp.supervision.observations.ClassKey;
import org.processmining.estminer.specpp.supervision.observations.Count;
import org.processmining.estminer.specpp.supervision.observations.Event;
import org.processmining.estminer.specpp.supervision.observations.EventCountStatistics;

public class EventCounterMonitor implements ComputingMonitor<Event, Counter<ClassKey<? extends Event>>, String> {

    private Counter<ClassKey<? extends Event>> counter;


    public EventCounterMonitor() {
        counter = new Counter<>();
    }

    @Override
    public String computeResult() {
        EventCountStatistics ecs = new EventCountStatistics();
        counter.forEach((key, value) -> ecs.record((ClassKey<Event>) key, new Count(value)));
        return ecs.toPrettyString();
    }

    @Override
    public Counter<ClassKey<? extends Event>> getMonitoringState() {
        return counter;
    }

    @Override
    public void handleObservation(Event observation) {
        counter.inc(ClassKey.ofObj(observation));
    }
}
