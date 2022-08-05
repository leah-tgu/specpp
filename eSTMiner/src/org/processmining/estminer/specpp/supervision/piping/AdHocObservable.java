package org.processmining.estminer.specpp.supervision.piping;

import org.processmining.estminer.specpp.supervision.observations.Observation;
import org.processmining.estminer.specpp.traits.Triggerable;

public interface AdHocObservable<O extends Observation> extends AsyncAwareObservable<O>, Triggerable {

    O computeObservation();

    @Override
    default void trigger() {
        publish(computeObservation());
    }

}
