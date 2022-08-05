package org.processmining.estminer.specpp.supervision.piping;

import org.apache.commons.collections4.IteratorUtils;
import org.processmining.estminer.specpp.supervision.observations.Observation;
import org.processmining.estminer.specpp.supervision.traits.ManyToMany;

import java.util.function.Predicate;

public class FilterPipe<O extends Observation> extends TransformingPipe<Observations<O>, Observations<O>> implements ManyToMany<O, O> {

    public FilterPipe(Predicate<? super O> predicate) {
        super(obs -> new ObservationIterable<>(IteratorUtils.filteredIterator(obs.iterator(), predicate::test)));
    }

}
