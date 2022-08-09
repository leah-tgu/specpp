package org.processmining.estminer.specpp.orchestra;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.processmining.estminer.specpp.base.impls.SpecPP;
import org.processmining.estminer.specpp.componenting.data.DataRequirements;
import org.processmining.estminer.specpp.componenting.data.DataSource;
import org.processmining.estminer.specpp.componenting.system.ComponentRepository;
import org.processmining.estminer.specpp.composition.PlaceCollection;
import org.processmining.estminer.specpp.datastructures.petri.PetriNet;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.ChangeTree;
import org.processmining.estminer.specpp.datastructures.util.TypedItem;
import org.processmining.estminer.specpp.preprocessing.InputDataBundle;
import org.processmining.estminer.specpp.supervision.Supervisor;
import org.processmining.estminer.specpp.supervision.monitoring.*;
import org.processmining.estminer.specpp.supervision.observations.Visualization;
import org.processmining.estminer.specpp.supervision.supervisors.DebuggingSupervisor;
import org.processmining.estminer.specpp.supervision.traits.Monitoring;
import org.processmining.estminer.specpp.util.FileUtils;
import org.processmining.estminer.specpp.util.JavaTypingUtils;
import org.processmining.estminer.specpp.util.VizUtils;
import org.processmining.plugins.graphviz.visualisation.DotPanel;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SpecOpsSetup {

    public static SpecPP<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper> specOps(DataSource<SpecOpsConfigBundle> configBundleSource, DataSource<InputDataBundle> inputDataBundleSource) {

        SpecPP<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper> specPP = setupSpecOps(configBundleSource.getData(), inputDataBundleSource.getData());
        executeSpecOps(specPP);

        return specPP;
    }


    public static SpecPP<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper> setupSpecOps(SpecOpsConfigBundle configBundle, InputDataBundle dataBundle) {
        ComponentRepository cr = new ComponentRepository();

        configBundle.instantiate(cr, dataBundle);

        SpecPP<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper> specpp = cr.createFrom(new SpecPP.Builder<>(), cr);

        specpp.init();

        return specpp;
    }

    public static void executeSpecOps(SpecPP<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper> specpp) {
        specpp.start();

        try {
            ExecutorService executorService = Executors.newCachedThreadPool();

            System.out.println("# Commencing SpecOps @" + LocalDateTime.now());
            System.out.println("// ========================================= //");
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
                        } else if (monitor instanceof TreeMonitor) {
                            TreeMonitor treeMonitor = (TreeMonitor) monitor;
                            // TODO the ugliness. i cri
                            specpp.getComponentRepository()
                                  .dataSources()
                                  .register(DataRequirements.dataSource("ChangeTree", JavaTypingUtils.castClass(ChangeTree.class), treeMonitor::getMonitoringState));
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
            VizUtils.visualizeProMPetrinet(finalResult);

            for (Supervisor supervisor : specpp.getSupervisors()) {
                if (supervisor instanceof Monitoring) {
                    for (Monitor<?, ?> monitor : ((Monitoring) supervisor).getMonitors()) {
                        if (monitor instanceof MultiComputingMonitor) {
                            MultiComputingMonitor<?, ?> multiComputingMonitor = (MultiComputingMonitor<?, ?>) monitor;
                            for (TypedItem<?> typedItem : multiComputingMonitor.computeResults()) {
                                Object item = typedItem.getItem();
                                handleMonitoringResult(item);
                            }
                        } else if (monitor instanceof ComputingMonitor) {
                            handleMonitoringResult(((ComputingMonitor<?, ?, ?>) monitor).computeResult());
                        } else DebuggingSupervisor.debug("monitoring results", monitor.getMonitoringState());
                    }
                }
            }

            for (JFreeChart chart : charts) {
                FileUtils.saveChart(chart);
            }

        } catch (InterruptedException | ExecutionException e) {
            specpp.stop();
            throw new RuntimeException(e);
        }
    }

    public static void handleMonitoringResult(Object item) {
        if (item instanceof Visualization) {
            VizUtils.showVisualization((Visualization<?>) item);
            JComponent component = ((Visualization<?>) item).getComponent();
            if (component instanceof DotPanel) {
                FileUtils.saveDot(((DotPanel) component).getDot());
            }
        } else DebuggingSupervisor.debug("monitoring computations", item);
    }


}
