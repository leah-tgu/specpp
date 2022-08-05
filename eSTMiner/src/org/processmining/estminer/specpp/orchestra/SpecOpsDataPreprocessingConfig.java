package org.processmining.estminer.specpp.orchestra;

import org.processmining.estminer.specpp.componenting.system.ComponentRepository;
import org.processmining.estminer.specpp.representations.InputDataBundle;

public interface SpecOpsDataPreprocessingConfig {

    void registerDataSources(ComponentRepository cr, InputDataBundle bundle);

}
