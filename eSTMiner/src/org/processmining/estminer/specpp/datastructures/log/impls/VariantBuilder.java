package org.processmining.estminer.specpp.datastructures.log.impls;

import org.processmining.estminer.specpp.config.SimpleBuilder;
import org.processmining.estminer.specpp.datastructures.log.Activity;
import org.processmining.estminer.specpp.datastructures.log.Variant;

public interface VariantBuilder<V extends Variant> extends SimpleBuilder<V> {

    VariantBuilder<V> append(Activity activity);


}
