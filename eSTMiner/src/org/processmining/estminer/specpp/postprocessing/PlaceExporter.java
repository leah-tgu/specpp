package org.processmining.estminer.specpp.postprocessing;

import org.processmining.estminer.specpp.base.PostProcessor;
import org.processmining.estminer.specpp.componenting.data.ParameterRequirements;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.estminer.specpp.componenting.system.AbstractComponentSystemUser;
import org.processmining.estminer.specpp.config.parameters.OutputPathParameters;
import org.processmining.estminer.specpp.datastructures.petri.PetriNet;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.util.FileUtils;
import org.processmining.estminer.specpp.util.PathTools;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class PlaceExporter extends AbstractComponentSystemUser implements PostProcessor<PetriNet, PetriNet> {

    private final DelegatingDataSource<OutputPathParameters> outputPathParameters = new DelegatingDataSource<>();

    public PlaceExporter() {
        componentSystemAdapter().require(ParameterRequirements.OUTPUT_PATH_PARAMETERS, outputPathParameters);
    }

    @Override
    public PetriNet postProcess(PetriNet result) {

        String filePath = outputPathParameters.getData()
                                              .getFilePath(PathTools.OutputFileType.MISC_EXPORT, "places", ".txt");

        try (FileWriter fileWriter = FileUtils.createOutputFileWriter(filePath)) {
            fileWriter.write("" + result.getPlaces().size());
            fileWriter.write("\n");
            for (Place place : result.getPlaces()) {
                fileWriter.write(place.toString());
                fileWriter.write("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
