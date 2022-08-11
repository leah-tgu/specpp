package org.processmining.estminer.specpp.orchestra;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.processmining.estminer.specpp.base.impls.SpecPP;
import org.processmining.estminer.specpp.componenting.data.DataSource;
import org.processmining.estminer.specpp.componenting.data.ParameterRequirements;
import org.processmining.estminer.specpp.componenting.data.StaticDataSource;
import org.processmining.estminer.specpp.componenting.system.ComponentRepository;
import org.processmining.estminer.specpp.composition.PlaceCollection;
import org.processmining.estminer.specpp.config.parameters.PlaceGeneratorParameters;
import org.processmining.estminer.specpp.datastructures.log.Activity;
import org.processmining.estminer.specpp.datastructures.log.Log;
import org.processmining.estminer.specpp.datastructures.petri.PetriNet;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.estminer.specpp.datastructures.util.TypedItem;
import org.processmining.estminer.specpp.preprocessing.InputDataBundle;
import org.processmining.estminer.specpp.supervision.Supervisor;
import org.processmining.estminer.specpp.supervision.monitoring.*;
import org.processmining.estminer.specpp.supervision.observations.Visualization;
import org.processmining.estminer.specpp.supervision.traits.Monitoring;
import org.processmining.estminer.specpp.util.FileUtils;
import org.processmining.estminer.specpp.util.PrintingUtils;
import org.processmining.estminer.specpp.util.VizUtils;
import org.processmining.plugins.graphviz.visualisation.DotPanel;

import javax.swing.*;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SpecOpsSetup {

    public static SpecPP<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper> specOps(DataSource<SpecOpsConfigBundle> configBundleSource, DataSource<InputDataBundle> inputDataBundleSource) {
        SpecOpsConfigBundle configBundle = configBundleSource.getData();
        InputDataBundle inputDataBundle = inputDataBundleSource.getData();

        preSetup(configBundle, inputDataBundle);
        SpecPP<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper> specPP = setupSpecOps(configBundle, inputDataBundle);
        postSetup(specPP);
        executeSpecOps(specPP);

        return specPP;
    }

    private static void postSetup(SpecPP<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper> specPP) {
        System.out.println(PrintingUtils.printParameters(specPP));
    }

    private static void preSetup(SpecOpsConfigBundle configBundle, InputDataBundle data) {
        System.out.println("Executing: " + configBundle.getTitle());
        Log log = data.getLog();
        int traceCount = log.totalTraceCount();
        int variantCount = log.variantCount();
        Set<Activity> activities = data.getMapping().keySet();
        int activityCount = activities.size();
        BigInteger p_total = BigInteger.valueOf(4).pow(activityCount);
        System.out.println("(2^|A|)^2=" + p_total);
        BigInteger cutoff = BigInteger.valueOf(2).pow(activityCount - 2 + activityCount);
        BigInteger p_remaining = p_total.subtract(cutoff);
        System.out.println("cutoff=" + cutoff);
        System.out.println("remaining=" + p_remaining);

        System.out.println("Traces: " + traceCount + "\tVariants: " + variantCount + "\t Activities: " + activityCount);
        System.out.println("Top 7 variants:");
        log.stream()
           .sorted(Comparator.comparingInt(ii -> -log.getVariantFrequency(ii.getIndex())))
           .limit(7)
           .forEach(ii -> System.out.println(log.getVariantFrequency(ii.getIndex()) + "\t" + ii.getVariant()));
    }


    public static SpecPP<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper> setupSpecOps(SpecOpsConfigBundle configBundle, InputDataBundle dataBundle) {
        ComponentRepository cr = new ComponentRepository();

        configBundle.instantiate(cr, dataBundle);

        cr.parameters()
          .register(ParameterRequirements.parameters("placegenerator.parameters", PlaceGeneratorParameters.class, StaticDataSource.of(new PlaceGeneratorParameters(6, true, false, false, false))));

        SpecPP<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper> specpp = cr.createFrom(new SpecPP.Builder<>(), cr);

        specpp.init();

        return specpp;
    }

    public static void executeSpecOps(SpecPP<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper> specpp) {
        try {
            ExecutorService executorService = Executors.newCachedThreadPool();

            System.out.println("# Commencing SpecOps @" + LocalDateTime.now());
            System.out.println("// ========================================= //");

            specpp.start();

            CompletableFuture<ProMPetrinetWrapper> future = specpp.future(executorService);

            // TODO MONITORING BULLSH1T. REDO REDO REDO
            List<JFreeChart> charts = new LinkedList<>();
            for (Supervisor supervisor : specpp.getSupervisors()) {
                if (supervisor instanceof Monitoring) {
                    for (Monitor<?, ?> monitor : ((Monitoring) supervisor).getMonitors()) {
                        if (monitor instanceof ChartingMonitor) {
                            JFreeChart jFreeChart = ((TimeSeriesMonitor<?>) monitor).getMonitoringState();
                            ChartPanel panel = new ChartPanel(jFreeChart);
                            charts.add(jFreeChart);
                            VizUtils.showJPanel(panel);
                        }
                    }
                }
            }

            future.get();

            System.out.println("// ========================================= //");
            System.out.println("# Shutting Down SpecOps @" + LocalDateTime.now());
            specpp.stop();
            System.out.println("# Shutdown SpecOps @" + LocalDateTime.now());
            System.out.println("// ========================================= //");

            System.out.println("Executed " + specpp.stepCount() + " PEC cycles.");

            ProMPetrinetWrapper finalResult = specpp.getFinalResult();

            int edgeCount = finalResult.getNet().getEdges().size();
            int transitionCount = finalResult.getNet().getTransitions().size();
            int placeCount = finalResult.getNet().getPlaces().size();
            System.out.println("Resulting Petri net contains " + placeCount + " places, " + transitionCount + " transitions and " + edgeCount + " arcs.");
            VizUtils.visualizeProMPetrinet(finalResult);

            for (Supervisor supervisor : specpp.getSupervisors()) {
                if (supervisor instanceof Monitoring) {
                    for (Monitor<?, ?> monitor : ((Monitoring) supervisor).getMonitors()) {
                        if (monitor instanceof MultiComputingMonitor) {
                            MultiComputingMonitor<?, ?> multiComputingMonitor = (MultiComputingMonitor<?, ?>) monitor;
                            for (TypedItem<?> typedItem : multiComputingMonitor.computeResults()) {
                                Object item = typedItem.getItem();
                                handleMonitoringResult(monitor, item);
                            }
                        } else if (monitor instanceof ComputingMonitor) {
                            handleMonitoringResult(monitor, ((ComputingMonitor<?, ?, ?>) monitor).computeResult());
                        } else handleMonitoringResult(monitor, monitor.getMonitoringState());
                    }
                }
            }

            for (JFreeChart chart : charts) {
                FileUtils.saveChart(chart);
            }

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            specpp.stop();
        }
    }

    public static void handleMonitoringResult(Monitor<?, ?> monitor, Object item) {
        if (item instanceof Visualization) {
            VizUtils.showVisualization((Visualization<?>) item);
            JComponent component = ((Visualization<?>) item).getComponent();
            if (component instanceof DotPanel) {
                FileUtils.saveDot(((DotPanel) component).getDot());
            }
        } else {
            System.out.println("Monitoring result of " + monitor.getClass().getSimpleName());
            System.out.println(item);
        }
    }


}
