package org.processmining.specpp.headless.local;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.processmining.specpp.base.impls.SPECpp;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.traits.ProvidesParameters;
import org.processmining.specpp.composition.BasePlaceComposition;
import org.processmining.specpp.config.parameters.ExecutionParameters;
import org.processmining.specpp.config.parameters.OutputPathParameters;
import org.processmining.specpp.config.parameters.ParameterProvider;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.util.ImmutableTuple2;
import org.processmining.specpp.datastructures.util.Tuple2;
import org.processmining.specpp.orchestra.*;
import org.processmining.specpp.preprocessing.InputDataBundle;
import org.processmining.specpp.supervision.CSVWriter;
import org.processmining.specpp.supervision.observations.Observation;
import org.processmining.specpp.util.ConfigurationParsing;
import org.processmining.specpp.util.FileUtils;
import org.processmining.specpp.util.ParameterVariationsParsing;
import org.processmining.specpp.util.PathTools;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Evaluation {


    public static final String ATTEMPT_IDENTIFIER = "attempt_0";
    private static final Options CLI_OPTIONS = new Options().addOption("l", "log", true, "path to the input event log")
                                                            .addOption("c", "config", true, "path to a json base configuration file")
                                                            .addOption("v", "variations", true, "path to a json parameter variation configuration file")
                                                            .addOption("o", "out", true, "path to the output directory")
                                                            .addOption("pec_tout", "pec_timeout", true, "pec timeout in s")
                                                            .addOption("pp_tout", "pp_timeout", true, "postprocessing timeout in s")
                                                            .addOption("total_tout", "total_timeout", true, "total timeout in s")
                                                            .addOption("lb", "label", true, "label identifying this evaluation")
                                                            .addOption("nt", "num_threads", true, "targeted number of threads");

    public static void main(String[] args) {
        DefaultParser defaultParser = new DefaultParser(false);
        CommandLine parsedArgs;
        try {
            parsedArgs = defaultParser.parse(CLI_OPTIONS, args);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        String labelValue = parsedArgs.getOptionValue("label");
        String attemptLabel = labelValue != null ? labelValue : ATTEMPT_IDENTIFIER;

        String num_threadsValue = parsedArgs.getOptionValue("num_threads");
        int num_threads = num_threadsValue != null ? Integer.parseInt(num_threadsValue) : Math.max(1, Runtime.getRuntime()
                                                                                                             .availableProcessors() - 1);

        String outValue = parsedArgs.getOptionValue("out");
        String outFolder = outValue != null ? outValue : PathTools.join("evaluation", ATTEMPT_IDENTIFIER) + PathTools.PATH_FOLDER_SEPARATOR;
        if (!outFolder.endsWith(PathTools.PATH_FOLDER_SEPARATOR))
            outFolder = outValue + PathTools.PATH_FOLDER_SEPARATOR;

        String logValue = parsedArgs.getOptionValue("log");
        String logPath = logValue != null ? logValue : PrivatePaths.toAbsolutePath(PrivatePaths.ROAD_TRAFFIC_FINE_MANAGEMENT_PROCESS);

        SPECppConfigBundle configBundle;
        String configValue = parsedArgs.getOptionValue("config");
        if (configValue != null)
            configBundle = FileUtils.readCustomJson(configValue, ConfigurationParsing.getTypeAdapter());
        else configBundle = new CodeDefinedEvaluationConfig();

        List<ProvidesParameters> parameterVariations;
        String variationsValue = parsedArgs.getOptionValue("variations");
        if (variationsValue != null)
            parameterVariations = FileUtils.readCustomJson(variationsValue, ParameterVariationsParsing.getTypeAdapter());
        else parameterVariations = CodeDefinedEvaluationConfig.createParameterVariations();
        System.out.println("Parameter Variations");
        for (ProvidesParameters pv : parameterVariations) {
            System.out.println(pv);
        }

        Duration pecTimeout = null, ppTimeout = null, totalTimeout = null;
        String pecValue = parsedArgs.getOptionValue("pec_timeout");
        if (pecValue != null) pecTimeout = Duration.ofSeconds(Long.parseLong(pecValue));
        String pptValue = parsedArgs.getOptionValue("pp_timeout");
        if (pptValue != null) ppTimeout = Duration.ofSeconds(Long.parseLong(pptValue));
        String ttValue = parsedArgs.getOptionValue("total_timeout");
        if (ttValue != null) totalTimeout = Duration.ofSeconds(Long.parseLong(ttValue));
        ExecutionParameters.ExecutionTimeLimits timeLimits = new ExecutionParameters.ExecutionTimeLimits(pecTimeout, ppTimeout, totalTimeout);
        ExecutionParameters executionParameters = ExecutionParameters.timeouts(timeLimits);

        EvaluationContext ec = new EvaluationContext();
        ec.attempt_identifier = attemptLabel;
        ec.num_threads = num_threads;
        ec.logPath = logPath;
        ec.outputFolder = outFolder;
        ec.parameterVariations = parameterVariations;

        run(configBundle, executionParameters, ec);
    }

    private static void run(SPECppConfigBundle configBundle, ExecutionParameters executionParameters, EvaluationContext ec) {
        InputProcessingConfig inputProcessingConfig = configBundle.getInputProcessingConfig();
        System.out.printf("Loading and preprocessing input log from \"%s\".%n", ec.logPath);
        InputDataBundle inputData = InputDataBundle.load(ec.logPath, inputProcessingConfig);
        System.out.println("Finished preparing input data.");

        int num_threads = ec.num_threads;
        int num_replications = 1;

        String meta_string = "Evaluation Attempt: " + ec.attempt_identifier + " @" + LocalDateTime.now() + "\n" + "Per run Timeouts: " + executionParameters.getTimeLimits() + "\n" + "Number of Threads: " + num_threads + ", " + "Number of Replications per Config: " + num_replications + "\n" + "Log Path: " + ec.logPath + "\n" + "Input Processing Parameters:\n\t" + inputProcessingConfig + "\n" + "Base Parameters:\n\t" + configBundle.getAlgorithmParameterConfig();


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

        File file = new File(ec.outputFolder);
        if (!file.exists() && !file.mkdirs()) return;

        FileUtils.saveString(ec.inOutputFolder("meta_info.txt"), meta_string);
        FileUtils.saveString(ec.inOutputFolder("input_data_info.txt"), data_string);

        ExecutionEnvironment executionEnvironment = new ExecutionEnvironment(num_threads);

        List<Tuple2<String, SPECppConfigBundle>> configurations = new ArrayList<>();
        for (int i = 0; i < ec.parameterVariations.size(); i++) {
            for (int r = 0; r < num_replications; r++) {
                String rid = createRunIdentifier(i, r);
                SPECppConfigBundle rc = createRunConfiguration(rid, ec, configBundle, i);
                configurations.add(new ImmutableTuple2<>(rid, rc));
            }
        }

        ec.perfWriter = new CSVWriter<>(ec.inOutputFolder("perf.csv"), SPECppFinished.COLUMN_NAMES, SPECppFinished::toCSVRow);

        System.out.printf("Commencing evaluation run of %d configurations with %d replications each over %d threads.%n", configurations.size(), num_replications, num_threads);
        List<Tuple2<String, ExecutionEnvironment.SPECppExecution<Place, BasePlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper>>> submittedExecutions = new ArrayList<>(configurations.size());
        for (Tuple2<String, SPECppConfigBundle> tup : configurations) {
            String runIdentifier = tup.getT1();
            SPECppConfigBundle cfg = tup.getT2();

            SPECpp<Place, BasePlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper> specpp = SPECpp.build(cfg, inputData);
            ExecutionEnvironment.SPECppExecution<Place, BasePlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper> execution = executionEnvironment.execute(specpp, executionParameters);
            System.out.println("queued " + runIdentifier + ".");
            executionEnvironment.addCompletionCallback(execution, ex -> handleCompletion(ec, runIdentifier, ex));
            submittedExecutions.add(new ImmutableTuple2<>(runIdentifier, execution));
        }

        try {
            executionEnvironment.join();
            ec.perfWriter.stop();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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
        System.out.printf("Completed evaluation. %d/%d executions terminated successfully.%n", count, configurations.size());
        FileUtils.saveStrings(ec.inOutputFolder("successes.txt"), successful);
        FileUtils.saveStrings(ec.inOutputFolder("failures.txt"), unsuccessful);

    }

    private static class EvaluationContext {

        public List<ProvidesParameters> parameterVariations;
        public int num_threads;
        private String attempt_identifier, outputFolder, logPath;
        private CSVWriter<SPECppFinished> perfWriter;

        public String inOutputFolder(String filename) {
            return outputFolder + filename;
        }

    }

    public static class SPECppFinished implements Observation {

        public static final String[] COLUMN_NAMES = new String[]{"run identifier", "started", "completed", "pec cycling [ms]", "post processing [ms]", "total [ms]", "was cancelled?"};

        private final String runIdentifier;
        private final ExecutionEnvironment.SPECppExecution<Place, BasePlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper> execution;

        public SPECppFinished(String runIdentifier, ExecutionEnvironment.SPECppExecution<Place, BasePlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper> execution) {
            this.runIdentifier = runIdentifier;
            this.execution = execution;
        }

        public ExecutionEnvironment.SPECppExecution<Place, BasePlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper> getExecution() {
            return execution;
        }

        public String getRunIdentifier() {
            return runIdentifier;
        }

        @Override
        public String toString() {
            return "SPECppFinished{" + runIdentifier + ": " + execution + '}';
        }

        public String[] toCSVRow() {
            return new String[]{runIdentifier, execution.getMasterComputation()
                                                        .getStart().toString(), execution.getMasterComputation()
                                                                                         .getEnd().toString(), Long.toString(execution.getDiscoveryComputation()
                                                                                                                                      .calculateRuntime()
                                                                                                                                      .toMillis()), Long.toString(execution.getPostProcessingComputation()
                                                                                                                                                                           .calculateRuntime()
                                                                                                                                                                           .toMillis()), Long.toString(execution.getMasterComputation()
                                                                                                                                                                                                                .calculateRuntime()
                                                                                                                                                                                                                .toMillis()), Boolean.toString(execution.hasTerminatedSuccessfully())};
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SPECppFinished that = (SPECppFinished) o;

            if (!Objects.equals(runIdentifier, that.runIdentifier)) return false;
            return Objects.equals(execution, that.execution);
        }

        @Override
        public int hashCode() {
            int result = runIdentifier != null ? runIdentifier.hashCode() : 0;
            result = 31 * result + (execution != null ? execution.hashCode() : 0);
            return result;
        }
    }

    public static void handleCompletion(EvaluationContext ec, String runIdentifier, ExecutionEnvironment.SPECppExecution<Place, BasePlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper> execution) {
        SPECpp<Place, BasePlaceComposition, CollectionOfPlaces, ProMPetrinetWrapper> specpp = execution.getSPECpp();
        ec.perfWriter.observe(new SPECppFinished(runIdentifier, execution));
        if (execution.hasTerminatedSuccessfully()) {
            String s = runIdentifier + " completed successfully." + "\n" + printComputationStatuses(execution);
            System.out.println(s);

            ProMPetrinetWrapper pn = specpp.getPostProcessedResult();
            //VizUtils.showVisualization(PetrinetVisualization.of(pn));
            /*
            FileUtils.savePetrinetToPnml(ec.outputFolder + "model_" + runIdentifier, pn);
            FileUtils.saveString(ec.outputFolder + "parameters_" + runIdentifier + ".txt", specpp.getGlobalComponentRepository()
                                                                                                 .parameters()
                                                                                                 .toString());
        */
        } else {
            String s = runIdentifier + " completed unsuccessfully." + "\n" + printComputationStatuses(execution);
            System.out.println(s);
        }
    }

    private static String printComputationStatuses(ExecutionEnvironment.SPECppExecution<?, ?, ?, ?> execution) {
        return "Computation Statuses:" + "\n\t" + "PEC-cycling" + execution.getDiscoveryComputation() + "\n" + "\t" + "Post Processing: " + execution.getPostProcessingComputation() + "\n\t" + "Overall: " + execution.getMasterComputation();
    }

    public static SPECppConfigBundle createRunConfiguration(String runIdentifier, EvaluationContext ec, SPECppConfigBundle baseConfigBundle, int variationId) {
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
