package org.processmining.estminer.specpp.supervision.observations;

import org.processmining.estminer.specpp.datastructures.util.NoRehashing;

public class StringStatisticKey extends NoRehashing<String> implements StatisticKey {

    private final String description;

    public StringStatisticKey(String description) {
        super(description);
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

}
