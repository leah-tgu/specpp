package org.processmining.estminer.specpp.orchestra;

import org.processmining.estminer.specpp.base.impls.SpecPP;
import org.processmining.estminer.specpp.componenting.data.DataSource;
import org.processmining.estminer.specpp.componenting.data.DataSourceCollection;
import org.processmining.estminer.specpp.componenting.data.ParameterRequirements;
import org.processmining.estminer.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.estminer.specpp.componenting.traits.ProvidesParameters;
import org.processmining.estminer.specpp.composition.PlaceCollection;
import org.processmining.estminer.specpp.config.parameters.OutputPathParameters;
import org.processmining.estminer.specpp.datastructures.log.Activity;
import org.processmining.estminer.specpp.datastructures.log.Log;
import org.processmining.estminer.specpp.datastructures.petri.PetriNet;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.estminer.specpp.preprocessing.InputDataBundle;
import org.processmining.estminer.specpp.supervision.traits.ProvidesOngoingVisualization;
import org.processmining.estminer.specpp.util.FileUtils;
import org.processmining.estminer.specpp.util.PathTools;
import org.processmining.estminer.specpp.util.VizUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpecOps {

    public static List<SpecPP<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper>> multiOps(DataSource<InputDataBundle> inputDataBundleSource, List<ProvidesParameters> parametersList, boolean doInParallel) {
        Stream<ProvidesParameters> stream = parametersList.stream();
        if (doInParallel) stream = stream.parallel();
        LocalDateTime start = LocalDateTime.now();
        System.out.println("# Commencing " + parametersList.size() + " Multi SpecOps" + (doInParallel ? " in parallel" : "") + " @" + start);
        System.out.println("// ========================================= //");

        List<SpecPP<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper>> collect = stream.map(pp -> SpecOps.specOps(() -> new CustomSpecOpsConfigBundle(pp), inputDataBundleSource, false, true))
                                                                                            .collect(Collectors.toList());
        System.out.println("// ========================================= //");
        LocalDateTime end = LocalDateTime.now();
        System.out.println("# Finished Multi SpecOps in " + Duration.between(start, end)
                                                                    .toString()
                                                                    .substring(2) + " @" + end);

        String s = collect.stream()
                          .map(SpecPP::getComponentRepository)
                          .map(GlobalComponentRepository::parameters)
                          .map(dc -> dc.askForData(ParameterRequirements.OUTPUT_PATH_PARAMETERS))
                          .map(opp -> opp.getFolderPath(PathTools.FolderStructure.BASE_OUTPUT_FOLDER))
                          .distinct()
                          .findFirst()
                          .orElse("");
        System.out.println("Outputs were saved to " + s);

        return collect;
    }


    public static SpecPP<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper> specOps(DataSource<SpecOpsConfigBundle> configBundleSource, DataSource<InputDataBundle> inputDataBundleSource, boolean suppressAnyOutput) {
        return specOps(configBundleSource, inputDataBundleSource, !suppressAnyOutput, !suppressAnyOutput);
    }

    public static SpecPP<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper> specOps(DataSource<SpecOpsConfigBundle> configBundleSource, DataSource<InputDataBundle> inputDataBundleSource, boolean allowPrinting, boolean allowFinalResultOutput) {
        SpecOpsConfigBundle configBundle = configBundleSource.getData();
        InputDataBundle inputDataBundle = inputDataBundleSource.getData();

        preSetup(configBundle, inputDataBundle, allowPrinting);
        SpecPP<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper> specPP = setupSpecOps(configBundle, inputDataBundle);
        postSetup(specPP, allowPrinting);

        executeSpecOps(specPP, allowPrinting);

        PostSpecOps.postExecution(specPP, allowPrinting, allowFinalResultOutput);

        return specPP;
    }

    private static void postSetup(SpecPP<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper> specPP, boolean allowPrinting) {
        DataSourceCollection parameters = specPP.getComponentRepository().parameters();
        String x = parameters.toString();
        OutputPathParameters outputPathParameters = parameters.askForData(ParameterRequirements.OUTPUT_PATH_PARAMETERS);
        String filePath = outputPathParameters.getFilePath(PathTools.OutputFileType.MISC_EXPORT, "parameters", ".txt");
        FileUtils.saveString(filePath, x);
        if (allowPrinting) System.out.println(x);
    }

    private static void preSetup(SpecOpsConfigBundle configBundle, InputDataBundle data, boolean allowPrinting) {
        if (!allowPrinting) return;
        System.out.println("Executing: " + configBundle.getTitle());
        Log log = data.getLog();
        int traceCount = log.totalTraceCount();
        int variantCount = log.variantCount();
        Set<Activity> activities = data.getMapping().keySet();
        int activityCount = activities.size();
        System.out.println("Traces: " + traceCount + "\tVariants: " + variantCount + "\t Activities: " + activityCount);
        System.out.println("Top 7 variants:");
        log.stream()
           .sorted(Comparator.comparingInt(ii -> -log.getVariantFrequency(ii.getIndex())))
           .limit(7)
           .forEach(ii -> System.out.println(log.getVariantFrequency(ii.getIndex()) + "\t" + ii.getVariant()));
    }


    public static SpecPP<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper> setupSpecOps(SpecOpsConfigBundle configBundle, InputDataBundle dataBundle) {
        GlobalComponentRepository cr = new GlobalComponentRepository();

        configBundle.instantiate(cr, dataBundle);

        SpecPP<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper> specpp = cr.createFrom(new SpecPP.Builder<>(), cr);

        specpp.init();

        return specpp;
    }

    public static void executeSpecOps(SpecPP<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper> specpp, boolean allowPrinting) {
        if (allowPrinting) {
            System.out.println("# Commencing SpecOps @" + LocalDateTime.now());
            System.out.println("// ========================================= //");
        }
        try {
            ExecutorService executorService = Executors.newCachedThreadPool();

            specpp.start();

            CompletableFuture<ProMPetrinetWrapper> future = specpp.future(executorService);

            for (ProvidesOngoingVisualization<?> ongoingVisualization : getOngoingVisualizations(specpp)) {
                VizUtils.showVisualization(ongoingVisualization.getOngoingVisualization());
            }

            future.get();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            if (allowPrinting) {
                System.out.println("// ========================================= //");
                System.out.println("# Shutting Down SpecOps @" + LocalDateTime.now());
            }
            specpp.stop();
        }
        if (allowPrinting) System.out.println("# Shutdown SpecOps @" + LocalDateTime.now());
    }

    private static List<ProvidesOngoingVisualization<?>> getOngoingVisualizations(SpecPP<?, ?, ?, ?> specpp) {
        return PostSpecOps.getMonitorStream(specpp)
                          .filter(m -> m instanceof ProvidesOngoingVisualization)
                          .map(m -> (ProvidesOngoingVisualization<?>) m)
                          .collect(Collectors.toList());
    }


}
