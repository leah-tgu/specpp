package org.processmining.estminer.specpp.representations.log.impls;

import org.processmining.estminer.specpp.representations.log.Activity;
import org.processmining.estminer.specpp.util.datastructures.NoRehashing;

public class ActivityImpl extends NoRehashing<String> implements Activity {

    private final String label;

    public ActivityImpl(String label) {
        super(label);
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

}
