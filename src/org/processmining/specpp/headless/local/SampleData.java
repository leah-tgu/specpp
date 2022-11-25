package org.processmining.specpp.headless.local;

import org.processmining.specpp.orchestra.ConfigFactory;
import org.processmining.specpp.orchestra.DataExtractionParameters;
import org.processmining.specpp.orchestra.PreProcessingParameters;
import org.processmining.specpp.preprocessing.InputDataBundle;
import org.processmining.specpp.util.PublicPaths;

public class SampleData {

    public static InputDataBundle sample_1() {
        return InputDataBundle.load(PublicPaths.SAMPLE_EVENTLOG_2, ConfigFactory.create(PreProcessingParameters.getDefault(), DataExtractionParameters.getDefault()));
    }


}
