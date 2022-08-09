package org.processmining.estminer.specpp.supervision.observations;

import java.util.Map;

public class EventCountStatistics extends CountStatistics<ClassKey<Event>> {
    public EventCountStatistics() {
    }

    public EventCountStatistics(Map<ClassKey<Event>, Count> input) {
        super(input);
    }
}
