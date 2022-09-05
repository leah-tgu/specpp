package org.processmining.specpp.prom.events;

import java.time.LocalDateTime;

public class ComputationEnded extends ComputationEvent {
    private final LocalDateTime end;

    private ComputationEnded(LocalDateTime end) {
        this.end = end;
    }

    public LocalDateTime getEnd() {
        return end;
    }
}
