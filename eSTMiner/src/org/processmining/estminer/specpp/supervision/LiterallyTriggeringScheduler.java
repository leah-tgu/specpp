package org.processmining.estminer.specpp.supervision;

import org.processmining.estminer.specpp.traits.Triggerable;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

public class LiterallyTriggeringScheduler extends RegularScheduler {

    private final Set<Triggerable> alreadyTriggered;

    public LiterallyTriggeringScheduler() {
        alreadyTriggered = new HashSet<>();
    }

    public static LiterallyTriggeringScheduler inst() {
        return new LiterallyTriggeringScheduler();
    }


    public LiterallyTriggeringScheduler schedule(Triggerable triggerable, Duration timeInterval) {
        if (!alreadyTriggered.contains(triggerable)) {
            alreadyTriggered.add(triggerable);
            super.schedule(triggerable::trigger, timeInterval);
        }
        return this;
    }

    @Override
    public void start() {
        super.start();
        alreadyTriggered.clear();
    }

    @Override
    public void stop() {
        super.stop();
    }

    public boolean isTriggered(Triggerable triggerable) {
        return alreadyTriggered.contains(triggerable);
    }
}
