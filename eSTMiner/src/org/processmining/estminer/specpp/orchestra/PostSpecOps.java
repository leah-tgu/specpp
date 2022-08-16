package org.processmining.estminer.specpp.orchestra;

import org.jfree.chart.ChartPanel;
import org.processmining.estminer.specpp.base.impls.SpecPP;
import org.processmining.estminer.specpp.componenting.data.ParameterRequirements;
import org.processmining.estminer.specpp.componenting.system.ComponentRepository;
import org.processmining.estminer.specpp.composition.PlaceCollection;
import org.processmining.estminer.specpp.config.parameters.OutputPathParameters;
import org.processmining.estminer.specpp.datastructures.petri.PetriNet;
import org.processmining.estminer.specpp.datastructures.petri.PetrinetVisualization;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.estminer.specpp.datastructures.util.TypedItem;
import org.processmining.estminer.specpp.supervision.monitoring.Monitor;
import org.processmining.estminer.specpp.supervision.observations.Visualization;
import org.processmining.estminer.specpp.supervision.traits.Monitoring;
import org.processmining.estminer.specpp.supervision.traits.ProvidesResults;
import org.processmining.estminer.specpp.util.FileUtils;
import org.processmining.estminer.specpp.util.PathTools;
import org.processmining.estminer.specpp.util.VizUtils;
import org.processmining.plugins.graphviz.visualisation.DotPanel;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PostSpecOps {


    static void postExecution(SpecPP<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper> specPP, boolean allowPrinting, boolean allowFinalResultOutput) {
        if (allowPrinting) {
            System.out.println("// ========================================= //");
            System.out.println("Executed " + specPP.stepCount() + " ProposalEvaluationComposition cycles.");
        }

        ProMPetrinetWrapper finalResult = specPP.getFinalResult();

        ComponentRepository cr = specPP.getComponentRepository();
        OutputPathParameters outputPathParameters = cr.parameters()
                                                      .askForData(ParameterRequirements.OUTPUT_PATH_PARAMETERS);

        String filePath = outputPathParameters.getFilePath(PathTools.OutputFileType.GRAPH, "petri");
        PetrinetVisualization petrinetVisualization = PetrinetVisualization.of(filePath,finalResult);
        if (allowFinalResultOutput){
            showFinalResult(finalResult, petrinetVisualization);
            saveFinalResult(outputPathParameters, finalResult, petrinetVisualization);
        }
        List<Monitor<?, ?>> monitors = getMonitors(specPP);
        if (allowPrinting) showMonitoringResults(monitors);
        saveMonitoringResults(outputPathParameters, monitors);
    }

    private static void saveFinalResult(OutputPathParameters outputPathParameters, ProMPetrinetWrapper finalResult, PetrinetVisualization petrinetVisualization) {
        String filePath = outputPathParameters.getFilePath(PathTools.OutputFileType.GRAPH, "petri");

        FileUtils.saveDotPanel(filePath, petrinetVisualization.getComponent());
    }

    private static PetrinetVisualization showFinalResult(ProMPetrinetWrapper finalResult, PetrinetVisualization petrinetVisualization) {
        int edgeCount = finalResult.getNet().getEdges().size();
        int transitionCount = finalResult.getNet().getTransitions().size();
        int placeCount = finalResult.getNet().getPlaces().size();
        System.out.println("The resulting Petri net contains " + placeCount + " places (incl. artificial start & end), " + transitionCount + " transitions and " + edgeCount + " arcs.");
        VizUtils.showVisualization(petrinetVisualization);
        return petrinetVisualization;
    }

    private static void showMonitoringResults(List<Monitor<?, ?>> monitors) {
        for (Visualization<?> resultingVisualization : getResultingVisualizations(monitors.stream())) {
            VizUtils.showVisualization(resultingVisualization);
        }
        for (String resultingString : getResultingStrings(monitors.stream())) {
            System.out.println(resultingString);
        }
    }

    public static void saveMonitoringResults(OutputPathParameters outputPathParameters, List<Monitor<?, ?>> monitors) {
        for (Visualization<?> resultingVisualization : getResultingVisualizations(monitors.stream())) {
            JComponent component = resultingVisualization.getComponent();
            String title = resultingVisualization.getTitle().toLowerCase().replace(".", "_");
            if (component instanceof DotPanel) {
                String filePath = outputPathParameters.getFilePath(PathTools.OutputFileType.GRAPH, title);
                FileUtils.saveDotPanel(filePath, ((DotPanel) component));
            } else if (component instanceof ChartPanel) {
                String filePath = outputPathParameters.getFilePath(PathTools.OutputFileType.CHART, title);
                FileUtils.saveChart(filePath, ((ChartPanel) component).getChart());
            }
        }
        String filePath = outputPathParameters.getFilePath(PathTools.OutputFileType.MISC_EXPORT, "monitoring_results", ".txt");
        FileUtils.saveStrings(filePath, getResultingStrings(monitors.stream()));
    }

    static Stream<Monitor<?, ?>> getMonitorStream(SpecPP<?, ?, ?, ?> specpp) {
        return specpp.getSupervisors()
                     .stream()
                     .filter(s -> s instanceof Monitoring)
                     .map(s -> (Monitoring) s)
                     .flatMap(m -> m.getMonitors().stream());
    }

    private static List<Monitor<?, ?>> getMonitors(SpecPP<?, ?, ?, ?> specpp) {
        return getMonitorStream(specpp).collect(Collectors.toList());
    }

    private static List<String> getResultingStrings(Stream<Monitor<?, ?>> monitors) {
        return monitors.filter(m -> m instanceof ProvidesResults)
                       .map(m -> (ProvidesResults) m)
                       .flatMap(pr -> pr.getResults().stream())
                       .map(TypedItem::getItem)
                       .filter(item -> item instanceof String)
                       .map(s -> (String) s)
                       .collect(Collectors.toList());
    }

    private static List<Visualization<?>> getResultingVisualizations(Stream<Monitor<?, ?>> monitors) {
        return monitors.filter(m -> m instanceof ProvidesResults)
                       .map(m -> (ProvidesResults) m)
                       .flatMap(pr -> pr.getResults().stream())
                       .map(TypedItem::getItem)
                       .filter(r -> r instanceof Visualization)
                       .map(r -> (Visualization<?>) r)
                       .collect(Collectors.toList());
    }
}
