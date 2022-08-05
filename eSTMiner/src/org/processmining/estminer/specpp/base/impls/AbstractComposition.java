package org.processmining.estminer.specpp.base.impls;

import com.google.common.collect.ImmutableSet;
import org.processmining.estminer.specpp.base.Candidate;
import org.processmining.estminer.specpp.base.Composition;
import org.processmining.estminer.specpp.traits.ProperlyPrintable;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Supplier;

public abstract class AbstractComposition<C extends Candidate, K extends Collection<C>> implements Composition<C>, ProperlyPrintable {

    protected final K candidates;
    private C lastAcceptedCandidate;

    public AbstractComposition(Supplier<K> candidateCollection) {
        this.candidates = candidateCollection.get();
    }

    public C getLastAcceptedCandidate() {
        return lastAcceptedCandidate;
    }

    @Override
    public int size() {
        return candidates.size();
    }

    @Override
    public Set<C> toSet() {
        return ImmutableSet.copyOf(candidates);
    }

    @Override
    public Iterator<C> iterator() {
        return candidates.iterator();
    }

    @Override
    public void accept(C candidate) {
        candidates.add(candidate);
        setLastAcceptedCandidate(candidate);
    }

    protected void setLastAcceptedCandidate(C candidate) {
        lastAcceptedCandidate = candidate;
    }

    @Override
    public String toString() {
        return candidates.toString();
    }
}
