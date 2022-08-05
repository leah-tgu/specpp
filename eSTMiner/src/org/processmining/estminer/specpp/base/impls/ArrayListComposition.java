package org.processmining.estminer.specpp.base.impls;

import org.processmining.estminer.specpp.base.Candidate;
import org.processmining.estminer.specpp.base.MutableCappedComposition;

import java.util.ArrayList;

public class ArrayListComposition<C extends Candidate> extends AbstractComposition<C, ArrayList<C>> implements MutableCappedComposition<C> {

    public static final int ABSOLUTE_SIZE_LIMIT = 100000;

    public ArrayListComposition() {
        super(ArrayList::new);
    }

    @Override
    public int maxSize() {
        return ABSOLUTE_SIZE_LIMIT;
    }

    @Override
    public boolean hasCapacityLeft() {
        return size() < ABSOLUTE_SIZE_LIMIT;
    }

    @Override
    public void remove(C item) {
        candidates.remove(item);
    }

    @Override
    public C removeLast() {
        C last = getLastAcceptedCandidate();
        remove(last);
        setLastAcceptedCandidate(candidates.get(candidates.size() - 1));
        return last;
    }

}
