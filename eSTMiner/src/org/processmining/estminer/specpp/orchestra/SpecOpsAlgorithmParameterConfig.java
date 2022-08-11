package org.processmining.estminer.specpp.orchestra;

import org.processmining.estminer.specpp.componenting.system.ComponentRepository;
import org.processmining.estminer.specpp.config.parameters.DefaultParameters;

public interface SpecOpsAlgorithmParameterConfig {

    default void registerAlgorithmParameters(ComponentRepository cr) {
        cr.absorb(new DefaultParameters());
    }

}
