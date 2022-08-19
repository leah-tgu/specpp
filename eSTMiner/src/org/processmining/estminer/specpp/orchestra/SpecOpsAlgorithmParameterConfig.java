package org.processmining.estminer.specpp.orchestra;

import org.processmining.estminer.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.estminer.specpp.config.parameters.DefaultParameters;

public interface SpecOpsAlgorithmParameterConfig {

    default void registerAlgorithmParameters(GlobalComponentRepository cr) {
        cr.absorb(new DefaultParameters());
    }

}
