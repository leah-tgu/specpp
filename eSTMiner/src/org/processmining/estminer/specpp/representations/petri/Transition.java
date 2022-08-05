package org.processmining.estminer.specpp.representations.petri;

import org.processmining.estminer.specpp.traits.ProperlyPrintable;

public class Transition implements ProperlyPrintable {

    private final String label;

    public Transition(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
