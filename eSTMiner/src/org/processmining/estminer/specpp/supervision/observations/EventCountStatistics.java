package org.processmining.estminer.specpp.supervision.observations;

import java.util.Map;

public class EventCountStatistics extends CountStatistics<ClassStatisticKey<Event>> {
    public EventCountStatistics() {
    }

    public EventCountStatistics(Map<ClassStatisticKey<Event>, Count> input) {
        super(input);
    }
}
