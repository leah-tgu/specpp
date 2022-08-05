package org.processmining.estminer.specpp.representations.log.impls;

import org.processmining.estminer.specpp.config.SimpleBuilder;
import org.processmining.estminer.specpp.representations.log.Activity;
import org.processmining.estminer.specpp.representations.log.Variant;

public interface VariantBuilder<V extends Variant> extends SimpleBuilder<V> {

    VariantBuilder<V> append(Activity activity);


}
