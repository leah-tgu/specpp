package org.processmining.estminer.specpp.orchestra;

import org.processmining.estminer.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.estminer.specpp.preprocessing.InputDataBundle;

public interface SpecOpsDataPreprocessingConfig {

    void registerDataSources(GlobalComponentRepository cr, InputDataBundle bundle);

}
