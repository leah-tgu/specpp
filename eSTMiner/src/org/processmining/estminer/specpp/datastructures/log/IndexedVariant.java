package org.processmining.estminer.specpp.datastructures.log;

import org.processmining.estminer.specpp.datastructures.util.IndexedItem;

public class IndexedVariant extends IndexedItem<Variant> {
    public IndexedVariant(int index, Variant item) {
        super(index, item);
    }

    public Variant getVariant() {
        return getItem();
    }

}
