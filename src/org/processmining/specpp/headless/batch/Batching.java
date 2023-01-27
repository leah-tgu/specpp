package org.processmining.specpp.headless.batch;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.model.XLog;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.etconformance.ETCResults;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.specpp.base.impls.SPECpp;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.traits.ProvidesParameters;
import org.processmining.specpp.composition.BasePlaceComposition;
import org.processmining.specpp.config.*;
import org.processmining.specpp.config.parameters.ExecutionParameters;
import org.processmining.specpp.config.parameters.OutputPathParameters;
import org.processmining.specpp.config.parameters.ParameterProvider;
import org.processmining.specpp.config.parsing.ConfigurationParsing;
import org.processmining.specpp.config.parsing.InformalParameterVariationsParsing;
import org.processmining.specpp.config.parsing.ParameterVariationsParsing;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.petri.*;
import org.processmining.specpp.datastructures.util.ImmutableTuple2;
import org.processmining.specpp.datastructures.util.Tuple2;
import org.processmining.specpp.headless.CodeDefinedEvaluationConfig;
import org.processmining.specpp.orchestra.ExecutionEnvironment;
import org.processmining.specpp.orchestra.SPECppOutputtingUtils;
import org.processmining.specpp.preprocessing.InputDataBundle;
import org.processmining.specpp.preprocessing.XLogParser;
import org.processmining.specpp.supervision.DirectCSVWriter;
import org.processmining.specpp.util.*;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Batching {

    public static final String ATTEMPT_IDENTIFIER = "attempt_0";
    private static final Options CLI_OPTIONS = new Options().addOption("l", "log", true, "path to the input event log")
                                                            .addOption("c", "config", true, "path to a json base configuration file")
                                                            .addOption("v", "variations", true, "path to a json parameter variation configuration file")
                                                            .addOption("o", "out", true, "path to the output directory")
                                                            .addOption("ev", "evaluate", false, "whether to compute model quality metrics")
                                                            .addOption("m", "monitor", false, "whether to save the output of data monitors to files")
                                                            .addOption("viz", "visualize", false, "whether to visualize and thus layout the resulting petri nets")
                                                            .addOption("pec_time", "pec_timeout", true, "pec timeout in s")
                                                            .addOption("pp_time", "pp_timeout", true, "postprocessing timeout in s")
                                                            .addOption("total_time", "total_timeout", true, "total timeout in s")
                                                            .addOption("ev_time", "evaluation_timeout", true, "evaluation timeout in s")
                                                            .addOption("lb", "label", true, "label identifying this batch execution")
                                                            .addOption("nt", "num_threads", true, "targeted number of concurrent threads")
                                                            .addOption("dry", "dry_run", false, "to test the configuration variation setup");

    public static void main(String[] args) {
        DefaultParser defaultParser = new DefaultParser();
        CommandLine parsedArgs;
        try {
            parsedArgs = defaultParser.parse(CLI_OPTIONS, args);
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        String num_threadsValue = parsedArgs.getOptionValue("num_threads");
        int num_threads = num_threadsValue != null ? Integer.parseInt(num_threadsValue) : Math.max(1, Runtime.getRuntime()
                                                                                                             .availableProcessors() - 1);

        String outValue = parsedArgs.getOptionValue("out");
        String outFolder = outValue != null ? outValue : "batch" + PathTools.PATH_FOLDER_SEPARATOR;
        if (!outFolder.endsWith(PathTools.PATH_FOLDER_SEPARATOR))
            outFolder = outValue + PathTools.PATH_FOLDER_SEPARATOR;

        String labelValue = parsedArgs.getOptionValue("label");
        if (labelValue != null) outFolder += labelValue + PathTools.PATH_FOLDER_SEPARATOR;
        String attemptLabel = labelValue != null ? labelValue : ATTEMPT_IDENTIFIER;

        String logValue = parsedArgs.getOptionValue("log");
        if (logValue == null) {
            System.out.println("no input log was configured!");
            return;
        }
        String logPath = logValue;

        SPECppConfigBundle configBundle;
        String configValue = parsedArgs.getOptionValue("config");
        if (configValue != null)
            configBundle = FileUtils.readCustomJson(configValue, ConfigurationParsing.getTypeAdapter());
        else configBundle = new CodeDefinedEvaluationConfig();

        List<ProvidesParameters> parameterVariations;
        List<Tuple2<String, List<String>>> informalParameterVariations = null;
        String variationsValue = parsedArgs.getOptionValue("variations");
        if (variationsValue != null) {
            parameterVariations = FileUtils.readCustomJson(variationsValue, ParameterVariationsParsing.getTypeAdapter());
            informalParameterVariations = FileUtils.readCustomJson(variationsValue, InformalParameterVariationsParsing.getTypeAdapter());
        } else parameterVariations = CodeDefinedEvaluationConfig.createParameterVariations();

        /*
        int maxParameterVariationsToPrint = 50;
        int V = parameterVariations.size();
        System.out.printf("Batching %d Parameter Variations (printing first and last %d)%n", V, maxParameterVariationsToPrint / 2);
        for (ProvidesParameters pv : parameterVariations.subList(0, Math.min(maxParameterVariationsToPrint / 2, V))) {
            System.out.println(pv);
        }
        System.out.println("...");
        for (ProvidesParameters pv : parameterVariations.subList(V - Math.min(maxParameterVariationsToPrint / 2, V), V)) {
            System.out.println(pv);
        }
         */

        Duration pecTimeout = null, ppTimeout = null, totalTimeout = null;
        String pecValue = parsedArgs.getOptionValue("pec_timeout");
        if (pecValue != null) pecTimeout = Duration.ofSeconds(Long.parseLong(pecValue));
        String pptValue = parsedArgs.getOptionValue("pp_timeout");
        if (pptValue != null) ppTimeout = Duration.ofSeconds(Long.parseLong(pptValue));
        String ttValue = parsedArgs.getOptionValue("total_timeout");
        if (ttValue != null) totalTimeout = Duration.ofSeconds(Long.parseLong(ttValue));
        ExecutionParameters.ExecutionTimeLimits timeLimits = new ExecutionParameters.ExecutionTimeLimits(pecTimeout, ppTimeout, totalTimeout);
        ExecutionParameters executionParameters = ExecutionParameters.timeouts(timeLimits);

        BatchContext bc = new BatchContext();
        bc.attempt_identifier = attemptLabel;
        bc.num_threads = num_threads;
        bc.logPath = logPath;
        bc.outputFolder = outFolder;
        bc.parameterVariations = parameterVariations;
        bc.informalParameterVariations = informalParameterVariations;

        if (parsedArgs.hasOption("evaluate")) {
            Duration evalTimeout = null;
            String evtValue = parsedArgs.getOptionValue("evaluation_timeout");
            if (evtValue != null) evalTimeout = Duration.ofSeconds(Long.parseLong(evtValue));

            EvalContext evalContext = new EvalContext();
            evalContext.timeout = evalTimeout;
            bc.evalContext = evalContext;
            bc.options.add(BatchOptions.Evaluate);
        }

        if (parsedArgs.hasOption("visualize")) bc.options.add(BatchOptions.ShowResultingPetrinet);

        if (parsedArgs.hasOption("dry")) bc.options.add(BatchOptions.DryRun);

        if (parsedArgs.hasOption("monitor")) bc.options.add(BatchOptions.SaveMonitoring);

        run(configBundle, executionParameters, bc);
    }

    private static void run(SPECppConfigBundle configBundle, ExecutionParameters executionParameters, BatchContext bc) {
        InputProcessingConfig inputProcessingConfig = configBundle.getInputProcessingConfig();
        System.out.printf("Loading and preprocessing input log from \"%s\".%n", bc.logPath);
        XLog inputLog = XLogParser.readLog(bc.logPath);
        InputDataBundle inputData = InputDataBundle.process(inputLog, inputProcessingConfig);

        if (bc.options.contains(BatchOptions.Evaluate)) {
            PreProcessingParameters preProcessingParameters = inputProcessingConfig.getPreProcessingParameters();
            XLog evalLog = EvalUtils.createEvalLog(inputLog, preProcessingParameters);
            Set<XEventClass> eventClasses = EvalUtils.createEventClasses(preProcessingParameters.getEventClassifier(), evalLog);
            bc.evalContext.evaluationLogData = new EvalUtils.EvaluationLogData(evalLog, preProcessingParameters.getEventClassifier(), eventClasses);
        }

        inputLog = null;
        System.gc();
        System.out.println("Finished preparing input data.");

        int num_threads = bc.num_threads;
        int num_replications = 1;

        String meta_string = "Batching Attempt: " + bc.attempt_identifier + " @" + LocalDateTime.now() + "\n" + "Per run Timeouts: " + executionParameters.getTimeLimits() + "\n" + "Number of Threads: " + num_threads + ", " + "Number of Replications per Config: " + num_replications + "\n" + "Log Path: " + bc.logPath + "\n" + "Input Processing Parameters:\n\t" + inputProcessingConfig + "\n" + "Base Parameters:\n\t" + configBundle.getAlgorithmParameterConfig();


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


        List<String> parameterVariationStrings = bc.parameterVariations.stream()
                                                                       .map(ProvidesParameters::toString)
                                                                       .collect(Collectors.toList());

        File file = new File(bc.outputFolder);
        if (!file.exists() && !file.mkdirs()) return;

        FileUtils.saveStrings(bc.inOutputFolder("parameter_variations.txt"), parameterVariationStrings);
        FileUtils.saveString(bc.inOutputFolder("meta_info.txt"), meta_string);
        FileUtils.saveString(bc.inOutputFolder("input_data_info.txt"), data_string);


        List<Tuple2<String, SPECppConfigBundle>> configurations = new ArrayList<>();
        for (int i = 0; i < bc.parameterVariations.size(); i++) {
            for (int r = 0; r < num_replications; r++) {
                String rid = createRunIdentifier(i, r);
                SPECppConfigBundle rc = createRunConfiguration(rid, bc, configBundle, i);
                configurations.add(new ImmutableTuple2<>(rid, rc));
            }
        }


        if (bc.informalParameterVariations != null) {
            int variationCount = bc.informalParameterVariations.stream()
                                                               .map(Tuple2::getT2)
                                                               .mapToInt(List::size)
                                                               .min()
                                                               .orElse(0);

            List<Tuple2<String, List<String>>> columnBasedData = new ArrayList<>();

            List<String> runIdentifiers = new ArrayList<>(variationCount * num_replications);
            for (Tuple2<String, List<String>> tuple2 : bc.informalParameterVariations) {
                columnBasedData.add(new ImmutableTuple2<>(tuple2.getT1(), new ArrayList<>()));
            }

            for (int i = 0; i < variationCount; i++) {
                for (int r = 0; r < num_replications; r++) {
                    runIdentifiers.add(createRunIdentifier(i, r));
                    for (int c = 0; c < columnBasedData.size(); c++) {
                        String s = bc.informalParameterVariations.get(c).getT2().get(i);
                        columnBasedData.get(c).getT2().add(s);
                    }
                }
            }

            columnBasedData.add(0, new ImmutableTuple2<>("run identifier", runIdentifiers));

            FileUtils.saveAsCSV(bc.inOutputFolder("varied_parameters_informal.csv"), columnBasedData);
        }

        if (bc.options.contains(BatchOptions.DryRun)) return;

        bc.perfWriter = new DirectCSVWriter<>(bc.inOutputFolder("perf.csv"), SPECppFinished.COLUMN_NAMES, SPECppFinished::toRow);
        if (bc.options.contains(BatchOptions.Evaluate))
            bc.evalContext.evalWriter = new DirectCSVWriter<>(bc.inOutputFolder("eval.csv"), SPECppEvaluated.COLUMN_NAMES, SPECppEvaluated::toRow);

        List<Tuple2<String, ExecutionEnvironment.SPECppExecution<Place, BasePlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper>>> submittedExecutions = new ArrayList<>(configurations.size());

        ExecutionEnvironment.EnvironmentSettings envs = ExecutionEnvironment.EnvironmentSettings.targetParallelism(num_threads / 2 + num_threads % 2, num_threads / 2);
        Thread wrap = ExecutionEnvironment.wrap(envs, exe -> {
            System.out.printf("Commencing batching run of %d configurations with %d replications each with a parallelism target of %d threads @%s.%n", configurations.size(), num_replications, num_threads, LocalDateTime.now());
            for (Tuple2<String, SPECppConfigBundle> tup : configurations) {
                String runIdentifier = tup.getT1();
                SPECppConfigBundle cfg = tup.getT2();

                SPECpp<Place, BasePlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper> specpp = SPECpp.build(cfg, inputData);
                ExecutionEnvironment.SPECppExecution<Place, BasePlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper> execution = exe.execute(specpp, executionParameters);
                System.out.println("Queued " + runIdentifier + ".");
                exe.addLightweightCompletionCallback(execution, ex -> handleCompletion(bc, runIdentifier, cfg, ex));

                if (bc.options.contains(BatchOptions.Evaluate) && bc.evalContext.timeout != null)
                    exe.addTimeLimitedCompletionCallback(execution, ex -> handleEvaluation(bc, runIdentifier, cfg, ex), bc.evalContext.timeout);
                else if (bc.options.contains(BatchOptions.Evaluate))
                    exe.addCompletionCallback(execution, ex -> handleEvaluation(bc, runIdentifier, cfg, ex));

                submittedExecutions.add(new ImmutableTuple2<>(runIdentifier, execution));
            }
        }, () -> {
            bc.perfWriter.stop();
            if (bc.options.contains(BatchOptions.Evaluate)) bc.evalContext.evalWriter.stop();
        });

        boolean cancelled = false;
        try {
            wrap.start();
            while (wrap.isAlive() && !cancelled) {
                wrap.join(1000);
                if (System.console() != null) {
                    String s = System.console().readLine();
                    cancelled = s != null && s.contains("exit");
                    if (cancelled) wrap.interrupt();

                }
            }
        } catch (InterruptedException e) {
            System.out.println("Batch execution was interrupted.");
            e.printStackTrace();
        }

        List<String> successful = submittedExecutions.stream()
                                                     .filter(t -> t.getT2().hasTerminatedSuccessfully())
                                                     .map(Tuple2::getT1)
                                                     .collect(Collectors.toList());
        List<String> unsuccessful = submittedExecutions.stream()
                                                       .filter(t -> !t.getT2().hasTerminatedSuccessfully())
                                                       .map(Tuple2::getT1)
                                                       .collect(Collectors.toList());
        long count = successful.size();
        System.out.printf("Completed batch execution @%s. %d/%d executions terminated successfully.%n", LocalDateTime.now(), count, configurations.size());
        FileUtils.saveStrings(bc.inOutputFolder("successes.txt"), successful);
        FileUtils.saveStrings(bc.inOutputFolder("failures.txt"), unsuccessful);

        System.exit(0);
    }

    public static void handleCompletion(BatchContext bc, String runIdentifier, SPECppConfigBundle cfg, ExecutionEnvironment.SPECppExecution<Place, BasePlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper> execution) {
        SPECpp<Place, BasePlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper> specpp = execution.getSPECpp();
        SPECppFinished finished = new SPECppFinished(runIdentifier, execution);
        bc.perfWriter.observe(finished);
        if (execution.hasTerminatedSuccessfully()) {
            System.out.println("Execution completed successfully:\n\t" + finished);

            ProMPetrinetWrapper pn = specpp.getPostProcessedResult();

            if (bc.options.contains(BatchOptions.ShowResultingPetrinet))
                VizUtils.showVisualization(PetrinetVisualization.of("Result of " + runIdentifier, pn));

            FileUtils.saveString(bc.outputFolder + "parameters_" + runIdentifier + ".txt", specpp.getGlobalComponentRepository()
                                                                                                 .parameters()
                                                                                                 .toString());
            FileUtils.savePetrinetToPnml(bc.outputFolder + "model_" + runIdentifier, pn);

            if (bc.options.contains(BatchOptions.SaveMonitoring)) {
                List<String> resultingStrings = SPECppOutputtingUtils.getResultingStrings(SPECppOutputtingUtils.getMonitors(specpp)
                                                                                                               .stream());
                FileUtils.saveStrings(bc.outputFolder + "monitoring_" + runIdentifier + ".txt", resultingStrings);
            }

        } else {
            String s = runIdentifier + " completed unsuccessfully:" + "\n" + PrintingUtils.stringifyComputationStatuses(execution);
            System.out.println(s);
        }
    }

    public static void handleEvaluation(BatchContext bc, String runIdentifier, SPECppConfigBundle cfg, ExecutionEnvironment.SPECppExecution<Place, BasePlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper> execution) {
        if (execution.hasTerminatedSuccessfully()) performEvaluation(bc.evalContext, runIdentifier, cfg, execution);
    }

    public static void performEvaluation(EvalContext ec, String runIdentifier, SPECppConfigBundle cfg, ExecutionEnvironment.SPECppExecution<Place, BasePlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper> execution) {
        EvalUtils.EvaluationLogData evaluationLogData = ec.evaluationLogData;
        ProMPetrinetWrapper pn = execution.getSPECpp().getPostProcessedResult();
        try {
            long start = System.currentTimeMillis();
            TransEvClassMapping evClassMapping = EvalUtils.createTransEvClassMapping(evaluationLogData.getEventClassifier(), evaluationLogData.getEventClasses(), pn);
            PNRepResult pnRepResult = EvalUtils.computeAlignmentBasedReplay(null, evaluationLogData, evClassMapping, pn);
            double fraction = EvalUtils.derivePerfectlyFitting(evaluationLogData, pnRepResult);
            double fitness = EvalUtils.deriveAlignmentBasedFitness(pnRepResult);
            ETCResults etcResults = EvalUtils.computeETC(null, evaluationLogData, evClassMapping, pn);
            double precision = EvalUtils.deriveETCPrecision(etcResults);
            long end = System.currentTimeMillis();
            long duration = end - start;
            SPECppEvaluated evaluated = new SPECppEvaluated(runIdentifier, fraction, fitness, precision, duration);
            ec.evalWriter.observe(evaluated);
            System.out.println("Evaluation completed successfully:\n\t" + evaluated);
        } catch (Exception e) {
            e.fillInStackTrace();
            System.out.printf("Evaluation computation of %s failed.%n%s%n", runIdentifier, e);
        }
    }

    public static SPECppConfigBundle createRunConfiguration(String runIdentifier, BatchContext ec, SPECppConfigBundle baseConfigBundle, int variationId) {
        ProvidesParameters parameterization = ec.parameterVariations.get(variationId);
        ParameterProvider custom = new ParameterProvider() {
            @Override
            public void init() {
                globalComponentSystem().provide(ParameterRequirements.OUTPUT_PATH_PARAMETERS.fulfilWithStatic(new OutputPathParameters(ec.outputFolder, "", "_" + runIdentifier)));
            }
        };
        AlgorithmParameterConfig parameterConfig = ConfigFactory.create(baseConfigBundle.getAlgorithmParameterConfig()
                                                                                        .getParameters(), custom, parameterization);
        return ConfigFactory.create(baseConfigBundle.getInputProcessingConfig(), baseConfigBundle.getComponentConfig(), parameterConfig);
    }

    public static String createRunIdentifier(int run_id, int replication_id) {
        return "run_" + run_id + "_rep_" + replication_id;
    }


}
