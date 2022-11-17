package org.processmining.specpp.headless.local;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.base.impls.SPECpp;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.specpp.composition.StatefulPlaceComposition;
import org.processmining.specpp.composition.composers.PlaceComposerWithCIPR;
import org.processmining.specpp.composition.composers.PlaceFitnessFilter;
import org.processmining.specpp.config.*;
import org.processmining.specpp.config.parameters.*;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.tree.base.impls.EnumeratingTree;
import org.processmining.specpp.datastructures.tree.heuristic.HeuristicTreeExpansion;
import org.processmining.specpp.datastructures.tree.heuristic.TreeNodeScore;
import org.processmining.specpp.datastructures.tree.nodegen.MonotonousPlaceGenerationLogic;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceState;
import org.processmining.specpp.evaluation.fitness.AbsolutelyNoFrillsFitnessEvaluator;
import org.processmining.specpp.evaluation.heuristics.DirectlyFollowsHeuristic;
import org.processmining.specpp.evaluation.heuristics.EventuallyFollowsTreeHeuristic;
import org.processmining.specpp.evaluation.implicitness.LPBasedImplicitnessCalculator;
import org.processmining.specpp.evaluation.markings.LogHistoryMaker;
import org.processmining.specpp.orchestra.PreProcessingParameters;
import org.processmining.specpp.orchestra.SPECppOperations;
import org.processmining.specpp.postprocessing.LPBasedImplicitnessPostProcessing;
import org.processmining.specpp.postprocessing.ProMConverter;
import org.processmining.specpp.postprocessing.ReplayBasedImplicitnessPostProcessing;
import org.processmining.specpp.postprocessing.SelfLoopPlaceMerger;
import org.processmining.specpp.preprocessing.InputData;
import org.processmining.specpp.preprocessing.InputDataBundle;
import org.processmining.specpp.preprocessing.orderings.AverageFirstOccurrenceIndex;
import org.processmining.specpp.prom.mvc.config.ConfiguratorCollection;
import org.processmining.specpp.proposal.ConstrainablePlaceProposer;
import org.processmining.specpp.util.FileUtils;
import org.processmining.specpp.util.PathTools;
import org.processmining.specpp.util.PrintingUtils;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.StreamSupport;

public class Evaluation {


    public static final String ATTEMPT_IDENTIFIER = "attempt_0";
    public static final String OUTPUT_FOLDER = PathTools.join("evaluation", ATTEMPT_IDENTIFIER) + PathTools.PATH_FOLDER_SEPARATOR;
    private static final Duration PEC_TIMEOUT = Duration.ofMinutes(2);
    private static final Duration ABSOLUTE_TIMEOUT = Duration.ofMinutes(3);

    public static void main(String[] args) {
        /*

        CLOptionDescriptor eventLogOption = new CLOptionDescriptor("log", CLOptionDescriptor.ARGUMENT_REQUIRED, 0, "path to the input event log");
        CLOptionDescriptor configOption = new CLOptionDescriptor("config", CLOptionDescriptor.ARGUMENT_OPTIONAL, 1, "path to a json configuration file");
        CLOptionDescriptor[] clOptions = {eventLogOption, configOption};
        CLArgsParser argsParser = new CLArgsParser(args, clOptions);
        String logPath = argsParser.getArgumentByName("log").getArgument();
        String configPath = argsParser.getArgumentByName("config").getArgument();

        */


        String path = PrivatePaths.toAbsolutePath(PrivatePaths.ROAD_TRAFFIC_FINE_MANAGEMENT_PROCESS);

        PreProcessingParameters prePar = new PreProcessingParameters(new XEventNameClassifier(), true, AverageFirstOccurrenceIndex.class);
        InputDataBundle inputData = InputData.loadData(path, prePar).getData();

        int num_threads = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
        int num_replications = 1;

        String meta_string = "Evaluation Attempt: " + ATTEMPT_IDENTIFIER + " @" + LocalDateTime.now() + "\n" + "PEC Timeout: " + PEC_TIMEOUT + "\n" + "Absolute Timeout: " + ABSOLUTE_TIMEOUT + "\n" + "Number of Threads: " + num_threads + ", " + "Number of Replications per Config: " + num_replications + "\n" + "Log Path: " + path + "\n" + "Pre-Processing Parameters: " + prePar;


        Log log = inputData.getLog();
        IntEncodings<Transition> transitionEncodings = inputData.getTransitionEncodings();
        BidiMap<Activity, Transition> mapping = inputData.getMapping();

        DescriptiveStatistics ds = new DescriptiveStatistics(StreamSupport.intStream(log.getVariantFrequencies()
                                                                                        .spliterator(), false)
                                                                          .mapToDouble(i -> i)
                                                                          .toArray());
        String log_info = "Log Info:" + "\n" + "|L| = " + log.totalTraceCount() + ", |V| = " + log.variantCount() + ", |A| = " + mapping.keySet()
                                                                                                                                        .size() + "\n" + "Activities = " + mapping.keySet() + "\n" + "Variant Frequency Statistics = " + ds;
        String enc_info = "Preset Transition Ordering: " + transitionEncodings.pre()
                                                                              .toString() + "\n" + "Postset Transition Ordering: " + transitionEncodings.post()
                                                                                                                                                        .toString();
        String data_string = log_info + "\n" + enc_info;

        // save evaluation attempt strings

        new File(OUTPUT_FOLDER).mkdirs();

        FileUtils.saveString(OUTPUT_FOLDER + "meta_info.txt", meta_string);
        FileUtils.saveString(OUTPUT_FOLDER + "input_data_info.txt", data_string);


        ScheduledExecutorService controlThreadsService = Executors.newScheduledThreadPool(1);
        ExecutorService executorService = Executors.newFixedThreadPool(num_threads - 1);
        ExecutorCompletionService<?> completionService = new ExecutorCompletionService<>(executorService);

        List<ConfiguratorCollection> configurations = new ArrayList<>();
        configurations.add(simplifiedConfig("run_0", 0.5));

        List<Future<?>> futures = new ArrayList<>();

        Object[][] results = new Object[configurations.size()][3];

        for (int i = 0; i < configurations.size(); i++) {
            int runId = i;
            ConfiguratorCollection cfg = configurations.get(i);
            SPECpp<Place, StatefulPlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper> specpp = SPECppOperations.setup(cfg, inputData);
            Runnable runnable = () -> {
                LocalDateTime start = LocalDateTime.now();
                specpp.start();
                specpp.executeAll();
                specpp.stop();
                LocalDateTime end = LocalDateTime.now();
                Duration duration = Duration.between(start, end);
                String parametersString = PrintingUtils.parametersToPrettyString(specpp.getGlobalComponentRepository()
                                                                                       .parameters());
                ProMPetrinetWrapper petrinetWrapper = specpp.getPostProcessedResult();
                Object[] objects = {duration, parametersString, petrinetWrapper};
                results[runId] = objects;
                controlThreadsService.submit(() -> saveResults(OUTPUT_FOLDER, runId, objects));
            };
            Future<?> future = completionService.submit(runnable, null);
            futures.add(future);

            Runnable killer = () -> {
                if (specpp.isActive()) specpp.cancelGracefully();
            };
            Runnable stoneColdKiller = () -> {
                if (specpp.isActive()) specpp.stop();
                if (!future.isDone()) future.cancel(true);
            };
            controlThreadsService.schedule(killer, PEC_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
            controlThreadsService.schedule(stoneColdKiller, ABSOLUTE_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
        }

        for (int i = 0; i < futures.size(); i++) {
            try {
                completionService.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            executorService.shutdown();
        }

        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        for (int i = 0; i < results.length; i++) {
            if (results[i] == null) {
                System.out.println("Run id " + i + ": did not finish.");
            } else {
                Object[] o = results[i];
                System.out.println("Run id " + i + ": " + Arrays.toString(o));
            }
        }
    }

    public static void saveResults(String outputFolder, int runId, Object[] results) {
        FileUtils.saveString(outputFolder + "_" + runId + ".txt", Arrays.toString(results));
    }

    public static ConfiguratorCollection simplifiedConfig(String runIdentifier, double tau) {
        OutputPathParameters opp = new OutputPathParameters(OUTPUT_FOLDER, "", runIdentifier);
        TauFitnessThresholds fitnessThresholds = TauFitnessThresholds.tau(tau);
        ParameterProvider temp = new ParameterProvider() {
            @Override
            public void init() {
                globalComponentSystem().provide(ParameterRequirements.OUTPUT_PATH_PARAMETERS.fulfilWithStatic(opp))
                                       .provide(ParameterRequirements.TAU_FITNESS_THRESHOLDS.fulfilWithStatic(fitnessThresholds));
            }
        };

        return createEvaluationConfig(temp);
    }

    public static ConfiguratorCollection createEvaluationConfig(ParameterProvider changedParameters) {
        // ** Supervision ** //

        SupervisionConfiguration.Configurator svConfig = Configurators.supervisors();

        // ** Evaluation ** //

        EvaluatorConfiguration.Configurator evConfig = Configurators.evaluators()
                                                                    .addEvaluatorProvider(new AbsolutelyNoFrillsFitnessEvaluator.Builder())
                                                                    .addEvaluatorProvider(new LogHistoryMaker.Builder())
                                                                    .addEvaluatorProvider(new LPBasedImplicitnessCalculator.Builder())
                                                                    .addEvaluatorProvider(new DirectlyFollowsHeuristic.Builder());

        HeuristicTreeConfiguration.Configurator<Place, PlaceState, PlaceNode, TreeNodeScore> htConfig = Configurators.<Place, PlaceState, PlaceNode, TreeNodeScore>heuristicTree()
                                                                                                                     .heuristicExpansion(HeuristicTreeExpansion::new)
                                                                                                                     .childGenerationLogic(new MonotonousPlaceGenerationLogic.Builder())
                                                                                                                     .tree(EnumeratingTree::new);
        // tree node heuristic
        //htConfig.heuristic(HeuristicUtils::bfs);
        htConfig.heuristic(new EventuallyFollowsTreeHeuristic.Builder());

        // ** Proposal & Composition ** //

        ProposerComposerConfiguration.Configurator<Place, AdvancedComposition<Place>, CollectionOfPlaces> pcConfig = Configurators.<Place, AdvancedComposition<Place>, CollectionOfPlaces>proposerComposer()
                                                                                                                                  .composition(StatefulPlaceComposition::new)
                                                                                                                                  .proposer(new ConstrainablePlaceProposer.Builder());

        pcConfig.terminalComposer(PlaceComposerWithCIPR::new);
        // without concurrent implicit place removal
        // pcConfig.terminalComposer(PlaceAccepter::new);
        //pcConfig.composerChain(PlaceFitnessFilter::new);
        // pcConfig.composerChain(PlaceFitnessFilter::new, UniwiredComposer::new);
        pcConfig.recursiveComposers(PlaceFitnessFilter::new);

        // ** Post Processing ** //

        PostProcessingConfiguration.Configurator<CollectionOfPlaces, CollectionOfPlaces> temp_ppConfig = Configurators.postProcessing();
        // ppConfig.processor(new UniwiredSelfLoopAdditionPostProcessing.Builder());
        // ppConfig.processor(SelfLoopPlaceMerger::new);
        temp_ppConfig.addPostProcessor(new ReplayBasedImplicitnessPostProcessing.Builder())
                     .addPostProcessor(new LPBasedImplicitnessPostProcessing.Builder())
                     .addPostProcessor(SelfLoopPlaceMerger::new);
        PostProcessingConfiguration.Configurator<CollectionOfPlaces, ProMPetrinetWrapper> ppConfig = temp_ppConfig.addPostProcessor(ProMConverter::new);

        // ** Parameters ** //
        //.provide(ParameterRequirements.DELTA_PARAMETERS.fulfilWithStatic(DeltaParameters.steepDelta(0.3, 3)))
        //.provide(ParameterRequirements.DELTA_COMPOSER_PARAMETERS.fulfilWithStatic(DeltaComposerParameters.getDefault()))

        ParameterProvider parProv = new ParameterProvider() {
            @Override
            public void init() {
                globalComponentSystem().provide(ParameterRequirements.TAU_FITNESS_THRESHOLDS.fulfilWithStatic(TauFitnessThresholds.tau(1)))
                                       .provide(ParameterRequirements.PLACE_GENERATOR_PARAMETERS.fulfilWithStatic(new PlaceGeneratorParameters(6, true, false, false, false)))
                                       .provide(ParameterRequirements.SUPERVISION_PARAMETERS.fulfilWithStatic(SupervisionParameters.instrumentNone(false, false)));
            }
        };

        if (changedParameters != null) parProv.globalComponentSystem().overridingAbsorb(changedParameters);

        return new ConfiguratorCollection(svConfig, pcConfig, evConfig, htConfig, ppConfig, parProv);
    }

}
