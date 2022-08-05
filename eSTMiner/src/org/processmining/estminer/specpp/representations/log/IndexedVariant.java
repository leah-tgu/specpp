package org.processmining.estminer.specpp.representations.log;

import org.processmining.estminer.specpp.util.datastructures.IndexedItem;

public class IndexedVariant extends IndexedItem<Variant> {
    public IndexedVariant(int index, Variant item) {
        super(index, item);
    }

    public Variant getVariant() {
        return getItem();
    }

}
