package org.processmining.estminer.specpp.supervision.observations;

import org.processmining.estminer.specpp.traits.Mergeable;

public class Count implements Statistic, Mergeable<Count> {

    private int count;

    public Count() {
        this(0);
    }

    public Count(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Count count1 = (Count) o;

        return count == count1.count;
    }

    @Override
    public int hashCode() {
        return count;
    }

    @Override
    public String toString() {
        return Integer.toString(count);
    }

    public int getCount() {
        return count;
    }

    public void inc(int plus) {
        count += plus;
    }

    @Override
    public void merge(Count other) {
        count += other.count;
    }
}
