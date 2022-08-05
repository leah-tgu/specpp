package org.processmining.estminer.specpp.supervision.piping;

import org.processmining.estminer.specpp.supervision.observations.Observation;
import org.processmining.estminer.specpp.supervision.traits.ManyToOne;

public class UnpackingPipe<O extends Observation> extends InflatingPipe<Observations<O>, O> implements ManyToOne<O, O> {
    public UnpackingPipe() {
        super(ObservationIterable::new);
    }

}
