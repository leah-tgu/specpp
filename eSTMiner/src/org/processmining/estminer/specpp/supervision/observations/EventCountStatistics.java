package org.processmining.estminer.specpp.supervision.observations;

public class EventCountStatistics extends CountStatistics<ClassKey<Event>> {
    public EventCountStatistics() {
    }


    @Override
    public String toPrettyString() {
        return "Event" + super.toPrettyString();
    }
}
