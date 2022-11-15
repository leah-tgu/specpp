package org.processmining.specpp.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.base.PostProcessor;
import org.processmining.specpp.componenting.data.FulfilledDataRequirement;
import org.processmining.specpp.componenting.data.ParameterRequirement;
import org.processmining.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.componenting.system.link.*;
import org.processmining.specpp.componenting.traits.ProvidesEvaluators;
import org.processmining.specpp.composition.StatefulPlaceComposition;
import org.processmining.specpp.config.*;
import org.processmining.specpp.config.parameters.ParameterProvider;
import org.processmining.specpp.config.parameters.Parameters;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.datastructures.tree.base.HeuristicStrategy;
import org.processmining.specpp.datastructures.tree.heuristic.TreeNodeScore;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceState;
import org.processmining.specpp.orchestra.PreProcessingParameters;
import org.processmining.specpp.postprocessing.ProMConverter;
import org.processmining.specpp.preprocessing.orderings.ActivityOrderingStrategy;
import org.processmining.specpp.prom.mvc.config.ConfiguratorCollection;
import org.processmining.specpp.proposal.ConstrainablePlaceProposer;
import org.processmining.specpp.supervision.Supervisor;
import org.processmining.specpp.supervision.supervisors.BaseSupervisor;
import org.processmining.specpp.supervision.supervisors.TerminalSupervisor;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

public class ConfigurationParsing {

    public static final TypeAdapter<PreProcessingParameters> PREPROCESSING_TYPE_ADAPTER = new TypeAdapter<PreProcessingParameters>() {

        private final Gson gson = new Gson();

        @Override
        public void write(JsonWriter out, PreProcessingParameters value) throws IOException {
            out.nullValue();
        }

        @Override
        public PreProcessingParameters read(JsonReader in) throws IOException {
            PreProcessingParameters ppp = PreProcessingParameters.getDefault();
            if (in.peek() == JsonToken.NULL) in.nextNull();
            else {
                JsonElement jsonElement = gson.fromJson(in, JsonElement.class);
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                PreProcessingParameters defaultParameters = PreProcessingParameters.getDefault();

                XEventClassifier eventClassifier = defaultParameters.getEventClassifier();
                if (jsonObject.has("eventClassifier")) try {
                    String eventClassifierString = jsonObject.get("eventClassifier").getAsString();
                    Class<XEventClassifier> eventClassifierClass = (Class<XEventClassifier>) Class.forName(getFullyQualifiedClassName(BasePackage.XES_Classifier, eventClassifierString));
                    eventClassifier = Reflection.instance(eventClassifierClass);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

                boolean addStartEndTransitions = defaultParameters.isAddStartEndTransitions();
                if (jsonObject.has("addStartEndTransitions"))
                    addStartEndTransitions = jsonObject.get("addStartEndTransitions").getAsBoolean();

                Class<? extends ActivityOrderingStrategy> activityOrderingStrategyClass = defaultParameters.getActivityOrderingStrategy();

                if (jsonObject.has("activityOrderingStrategy")) try {
                    String activityOrderingStrategyString = jsonObject.get("activityOrderingStrategy").getAsString();
                    activityOrderingStrategyClass = (Class<? extends ActivityOrderingStrategy>) Class.forName(getFullyQualifiedClassName(BasePackage.ActivityOrdering, activityOrderingStrategyString));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

                ppp = new PreProcessingParameters(eventClassifier, addStartEndTransitions, activityOrderingStrategyClass);
            }
            return ppp;
        }
    };

    public static final TypeAdapter<SupervisionConfiguration.Configurator> SUPERVISORS_TYPE_ADAPTER = new TypeAdapter<SupervisionConfiguration.Configurator>() {

        @Override
        public void write(JsonWriter out, SupervisionConfiguration.Configurator value) throws IOException {
            out.nullValue();
        }

        @Override
        public SupervisionConfiguration.Configurator read(JsonReader in) throws IOException {
            SupervisionConfiguration.Configurator sc = Configurators.supervisors();
            if (in.peek() == JsonToken.NULL) in.nextNull();
            else {
                in.beginArray();
                boolean nonEmpty = in.hasNext();
                if (nonEmpty) sc.addSupervisor(BaseSupervisor::new);
                while (in.hasNext()) {
                    String s = in.nextString();
                    s = getFullyQualifiedClassName(BasePackage.Supervisors, s);
                    SimpleBuilder<? extends Supervisor> simpleBuilder = ConfigurationParsing.parseBuilder(s, Supervisor.class);
                    sc.addSupervisor(simpleBuilder);
                }
                in.endArray();
                if (nonEmpty) sc.addSupervisor(TerminalSupervisor::new);
            }
            return sc;
        }
    };

    public static final TypeAdapter<EvaluatorConfiguration.Configurator> EVALUATORS_TYPE_ADAPTER = new TypeAdapter<EvaluatorConfiguration.Configurator>() {
        @Override
        public void write(JsonWriter out, EvaluatorConfiguration.Configurator value) throws IOException {
            out.nullValue();
        }

        @Override
        public EvaluatorConfiguration.Configurator read(JsonReader in) throws IOException {
            EvaluatorConfiguration.Configurator ec = Configurators.evaluators();
            if (in.peek() == JsonToken.NULL) in.nextNull();
            else {
                in.beginArray();
                while (in.hasNext()) {
                    String s = in.nextString();
                    s = getFullyQualifiedClassName(BasePackage.Evaluation, s);
                    SimpleBuilder<? extends ProvidesEvaluators> simpleBuilder = ConfigurationParsing.parseBuilder(s, ProvidesEvaluators.class);
                    ec.addEvaluatorProvider(simpleBuilder);
                }
                in.endArray();
            }
            return ec;
        }
    };

    public static final TypeAdapter<EfficientTreeConfiguration.Configurator<Place, PlaceState, PlaceNode>> EFFICIENT_TREE_TYPE_ADAPTER = new TypeAdapter<EfficientTreeConfiguration.Configurator<Place, PlaceState, PlaceNode>>() {
        @Override
        public void write(JsonWriter out, EfficientTreeConfiguration.Configurator<Place, PlaceState, PlaceNode> value) throws IOException {
            out.nullValue();
        }

        @Override
        public EfficientTreeConfiguration.Configurator<Place, PlaceState, PlaceNode> read(JsonReader in) throws IOException {
            EfficientTreeConfiguration.Configurator<Place, PlaceState, PlaceNode> etc = Configurators.generatingTree();
            HeuristicTreeConfiguration.Configurator<Place, PlaceState, PlaceNode, TreeNodeScore> htc = Configurators.heuristicTree();
            boolean isHeuristic = false;
            if (in.peek() == JsonToken.NULL) in.nextNull();
            else {
                in.beginObject();

                String name;
                name = in.nextName();
                assert "tree".equalsIgnoreCase(name);
                String s = in.nextString();
                s = getFullyQualifiedClassName(BasePackage.Tree, s);
                InitializingBuilder<? extends EfficientTreeComponent<PlaceNode>, ExpansionStrategyComponent<PlaceNode>> tree = ConfigurationParsing.parseInitializingBuilder(s, JavaTypingUtils.castClass(EfficientTreeComponent.class), JavaTypingUtils.castClass(ExpansionStrategyComponent.class));
                etc.tree(tree);
                htc.tree(tree);

                name = in.nextName();
                assert "expansion strategy".equalsIgnoreCase(name);
                s = in.nextString();
                s = getFullyQualifiedClassName(BasePackage.Tree, s);
                SimpleBuilder<? extends ExpansionStrategyComponent<PlaceNode>> exp = parseBuilder(s, JavaTypingUtils.castClass(ExpansionStrategyComponent.class));
                etc.expansionStrategy(exp);
                htc.expansionStrategy(exp);

                name = in.nextName();
                assert "node generation logic".equalsIgnoreCase(name);
                s = in.nextString();
                s = getFullyQualifiedClassName(BasePackage.NodeGeneration, s);
                SimpleBuilder<? extends ChildGenerationLogicComponent<Place, PlaceState, PlaceNode>> gen = parseBuilder(s, JavaTypingUtils.castClass(ChildGenerationLogicComponent.class));
                etc.childGenerationLogic(gen);
                htc.childGenerationLogic(gen);

                if (in.hasNext() && "heuristic".equalsIgnoreCase(in.nextName())) {
                    s = in.nextString();
                    s = getFullyQualifiedClassName(BasePackage.Heuristic, s);
                    SimpleBuilder<? extends HeuristicStrategy<PlaceNode, TreeNodeScore>> heu = parseBuilder(s, JavaTypingUtils.castClass(HeuristicStrategy.class));
                    htc.heuristic(heu);
                    isHeuristic = true;
                }

                in.endObject();
            }
            return isHeuristic ? htc : etc;
        }
    };

    public static final TypeAdapter<PostProcessingConfiguration.Configurator<CollectionOfPlaces, ProMPetrinetWrapper>> POST_PROCESSING_TYPE_ADAPTER = new TypeAdapter<PostProcessingConfiguration.Configurator<CollectionOfPlaces, ProMPetrinetWrapper>>() {
        @Override
        public void write(JsonWriter out, PostProcessingConfiguration.Configurator<CollectionOfPlaces, ProMPetrinetWrapper> value) throws IOException {
            out.nullValue();
        }

        @Override
        public PostProcessingConfiguration.Configurator<CollectionOfPlaces, ProMPetrinetWrapper> read(JsonReader in) throws IOException {
            PostProcessingConfiguration.Configurator<CollectionOfPlaces, ProMPetrinetWrapper> configurator = Configurators.<CollectionOfPlaces>postProcessing()
                                                                                                                          .addPostProcessor(ProMConverter::new);
            if (in.peek() == JsonToken.NULL) in.nextNull();
            else {
                in.beginArray();
                PostProcessingConfiguration.Configurator ppc = Configurators.postProcessing();
                while (in.hasNext()) {
                    String s = in.nextString();
                    s = getFullyQualifiedClassName(BasePackage.PostProcessor, s);
                    SimpleBuilder<? extends PostProcessor> simpleBuilder = parseBuilder(s, PostProcessor.class);
                    ppc.addPostProcessor(simpleBuilder);
                }
                in.endArray();
                ppc.addPostProcessor(ProMConverter::new);
                configurator = (PostProcessingConfiguration.Configurator<CollectionOfPlaces, ProMPetrinetWrapper>) ppc;
            }
            return configurator;
        }
    };


    public static final TypeAdapter<FulfilledDataRequirement<? extends Parameters>> PARAMETER_REQUIREMENT_TYPE_ADAPTER = new TypeAdapter<FulfilledDataRequirement<? extends Parameters>>() {

        private final Gson gson = new Gson();

        @Override
        public void write(JsonWriter out, FulfilledDataRequirement<? extends Parameters> value) throws IOException {
            out.nullValue();
        }

        @Override
        public FulfilledDataRequirement<? extends Parameters> read(JsonReader in) throws IOException {
            FulfilledDataRequirement<? extends Parameters> freq = null;
            if (in.peek() == JsonToken.NULL) in.nextNull();
            else {
                in.beginObject();
                String s = in.nextName();
                assert "label".equalsIgnoreCase(s);
                String key = in.nextString();
                s = in.nextName();
                assert "type".equalsIgnoreCase(s);
                String type = in.nextString();
                type = getFullyQualifiedClassName(BasePackage.Parameters, type);
                Class<Parameters> forName;
                try {
                    forName = (Class<Parameters>) Class.forName(type);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                Class<Parameters> aClass = forName;
                s = in.nextName();
                assert "args".equalsIgnoreCase(s);
                Parameters o = aClass.cast(gson.fromJson(in, aClass));
                ParameterRequirement<Parameters> req = new ParameterRequirement<>(key, aClass);
                freq = req.fulfilWithStatic(o);
                in.endObject();
            }
            return freq;
        }
    };

    public static final TypeAdapter<ParameterProvider> PARAMETERS_TYPE_ADAPTER = new TypeAdapter<ParameterProvider>() {
        @Override
        public void write(JsonWriter out, ParameterProvider value) throws IOException {
            out.nullValue();
        }

        @Override
        public ParameterProvider read(JsonReader in) throws IOException {
            ParameterProvider pp = new ParameterProvider() {
                @Override
                public void init() {
                }
            };
            if (in.peek() == JsonToken.NULL) in.nextNull();
            else {
                List<FulfilledDataRequirement<? extends Parameters>> list = new LinkedList<>();

                in.beginArray();
                while (in.hasNext()) {
                    FulfilledDataRequirement<? extends Parameters> freq = PARAMETER_REQUIREMENT_TYPE_ADAPTER.read(in);
                    list.add(freq);
                }
                in.endArray();

                pp = new ParameterProvider() {
                    @Override
                    public void init() {
                        for (FulfilledDataRequirement<? extends Parameters> f : list) {
                            globalComponentSystem().provide(f);
                        }
                    }
                };

            }
            return pp;
        }
    };

    public static final TypeAdapter<ConfiguratorCollection> CONFIGURATOR_COLLECTION_TYPE_ADAPTER = new TypeAdapter<ConfiguratorCollection>() {
        @Override
        public void write(JsonWriter out, ConfiguratorCollection value) throws IOException {
            out.nullValue();
        }

        @Override
        public ConfiguratorCollection read(JsonReader in) throws IOException {
            ConfiguratorCollection cc = null;
            if (in.peek() == JsonToken.NULL) in.nextNull();
            else {
                in.beginObject(); // root

                String name;
                name = in.nextName();
                if ("pre processing".equalsIgnoreCase(name)) {
                    PreProcessingParameters ppp = PREPROCESSING_TYPE_ADAPTER.read(in);
                    name = in.nextName();
                }
                assert "components".equalsIgnoreCase(name);
                in.beginObject(); // components

                // ** Supervisors ** //
                name = in.nextName();
                assert "supervisors".equalsIgnoreCase(name);
                SupervisionConfiguration.Configurator sc = SUPERVISORS_TYPE_ADAPTER.read(in);

                // ** Evaluators ** //
                name = in.nextName();
                assert "evaluators".equalsIgnoreCase(name);
                EvaluatorConfiguration.Configurator evc = EVALUATORS_TYPE_ADAPTER.read(in);

                // ** Proposing ** //
                name = in.nextName();
                assert "proposing".equals(in.nextName());
                in.beginObject(); // proposing
                ProposerComposerConfiguration.Configurator<Place, AdvancedComposition<Place>, CollectionOfPlaces> pcc = Configurators.<Place, AdvancedComposition<Place>, CollectionOfPlaces>proposerComposer()
                                                                                                                                     .composition(StatefulPlaceComposition::new)
                                                                                                                                     .proposer(new ConstrainablePlaceProposer.Builder());


                name = in.nextName();
                assert "proposer".equalsIgnoreCase(name);
                String s = in.nextString();
                s = getFullyQualifiedClassName(BasePackage.Proposal, s);
                SimpleBuilder<? extends ProposerComponent<Place>> prop = parseBuilder(s, JavaTypingUtils.castClass(ProposerComponent.class));
                pcc.proposer(prop);
                name = in.nextName();
                assert "tree structure".equalsIgnoreCase(name);
                EfficientTreeConfiguration.Configurator<Place, PlaceState, PlaceNode> etc = EFFICIENT_TREE_TYPE_ADAPTER.read(in);
                in.endObject(); // proposing

                // ** Compositing ** //
                name = in.nextName();
                assert "compositing".equalsIgnoreCase(name);
                in.beginObject(); // compositing

                name = in.nextName();
                assert "composition".equalsIgnoreCase(name);
                if (in.peek() == JsonToken.STRING) {
                    s = in.nextString();
                    s = getFullyQualifiedClassName(BasePackage.Composition, s);
                    SimpleBuilder<? extends AdvancedComposition<Place>> co = parseBuilder(s, JavaTypingUtils.castClass(AdvancedComposition.class));
                    pcc.composition(co);
                } else if (in.peek() == JsonToken.BEGIN_ARRAY) {
                    in.beginArray();
                    s = in.nextString();
                    s = getFullyQualifiedClassName(BasePackage.Composition, s);
                    SimpleBuilder<AdvancedComposition<Place>> nest = (SimpleBuilder<AdvancedComposition<Place>>) parseBuilder(s, JavaTypingUtils.castClass(AdvancedComposition.class));
                    s = in.nextString();
                    s = getFullyQualifiedClassName(BasePackage.Composition, s);
                    InitializingBuilder<? extends AdvancedComposition<Place>, AdvancedComposition<Place>> outer = parseInitializingBuilder(s, JavaTypingUtils.castClass(AdvancedComposition.class), JavaTypingUtils.castClass(AdvancedComposition.class));
                    in.endArray();
                    pcc.nestedComposition(nest, outer);
                }

                name = in.nextName();
                assert "composer".equalsIgnoreCase(name);
                if (in.peek() == JsonToken.STRING) {
                    s = in.nextString();
                    s = getFullyQualifiedClassName(BasePackage.Composer, s);
                    InitializingBuilder<? extends ComposerComponent<Place, AdvancedComposition<Place>, CollectionOfPlaces>, Object> tc = parseInitializingBuilder(s, JavaTypingUtils.castClass(ComposerComponent.class), JavaTypingUtils.castClass(CompositionComponent.class));
                    pcc.composer(tc);
                } else if (in.peek() == JsonToken.BEGIN_ARRAY) {
                    in.beginArray();
                    InitializingBuilder<? extends ComposerComponent<Place, AdvancedComposition<Place>, CollectionOfPlaces>, AdvancedComposition<Place>> termcomp = null;
                    List<InitializingBuilder<? extends ComposerComponent<Place, AdvancedComposition<Place>, CollectionOfPlaces>, ComposerComponent<Place, AdvancedComposition<Place>, CollectionOfPlaces>>> l = new LinkedList<>();
                    while (in.hasNext()) {
                        s = in.nextString();
                        s = getFullyQualifiedClassName(BasePackage.Composer, s);
                        if (in.hasNext()) {
                            InitializingBuilder<? extends ComposerComponent<Place, AdvancedComposition<Place>, CollectionOfPlaces>, ComposerComponent<Place, AdvancedComposition<Place>, CollectionOfPlaces>> compcomp = parseInitializingBuilder(s, JavaTypingUtils.castClass(ComposerComponent.class), JavaTypingUtils.castClass(ComposerComponent.class));
                            l.add(compcomp);
                        } else {
                            termcomp = parseInitializingBuilder(s, JavaTypingUtils.castClass(ComposerComponent.class), JavaTypingUtils.castClass(AdvancedComposition.class));
                        }
                    }
                    in.endArray();
                    pcc.terminalComposer(termcomp);
                    InitializingBuilder[] arr = l.toArray(new InitializingBuilder[0]);
                    pcc.composerChain(arr);
                }
                in.endObject(); // compositing

                // ** Post Processing ** //
                name = in.nextName();
                assert "post processors".equalsIgnoreCase(name);
                PostProcessingConfiguration.Configurator<CollectionOfPlaces, ProMPetrinetWrapper> ppc = POST_PROCESSING_TYPE_ADAPTER.read(in);

                in.endObject(); // components

                // ** Parameters (optional) ** //
                ParameterProvider pp = null;
                if (in.hasNext()) {
                    name = in.nextName();
                    assert "parameters".equalsIgnoreCase(name);
                    pp = PARAMETERS_TYPE_ADAPTER.read(in);
                }

                cc = new ConfiguratorCollection(sc, pcc, evc, etc, ppc, pp);

                in.endObject(); // root

            }
            return cc;
        }
    };

    public enum BasePackage {
        Evaluation("org.processmining.specpp.evaluation"),
        Supervisors("org.processmining.specpp.supervision.supervisors"),
        Tree("org.processmining.specpp.datastructures.tree"),
        NodeGeneration("org.processmining.specpp.datastructures.tree.nodegen"),
        Heuristic("org.processmining.specpp.evaluation.heuristics"),
        Composer("org.processmining.specpp.composition.composers"),
        Composition("org.processmining.specpp.composition"),
        Proposal("org.processmining.specpp.proposal"),
        PostProcessor("org.processmining.specpp.postprocessing"),
        Parameters("org.processmining.specpp.config.parameters"),
        XES_Classifier("org.deckfour.xes.classification"),
        ActivityOrdering("org.processmining.specpp.preprocessing.orderings");

        private final String packagePath;

        BasePackage(String packagePath) {
            this.packagePath = packagePath;
        }

        public String getPackagePath() {
            return packagePath;
        }

    }

    public static String getFullyQualifiedClassName(BasePackage basePackage, String s) {
        // if the string starts like that, it is already fully qualified, otherwise, we guess with the 'typical' base package as a prefix
        if (s.startsWith("org.processmining")) return s;
        else return basePackage.getPackagePath() + "." + s;
    }

    public static <I> SimpleBuilder<? extends I> parseBuilder(String s, Class<I> typeToParse) {
        SimpleBuilder<? extends I> simpleBuilder = null;
        try {
            Class<?> aClass = Class.forName(s);
            if (typeToParse.isAssignableFrom(aClass)) {
                Class<? extends I> directClassType = aClass.asSubclass(typeToParse);
                simpleBuilder = () -> Reflection.instance(directClassType);
            } else if (SimpleBuilder.class.isAssignableFrom(aClass)) {
                Class<? extends SimpleBuilder<? extends I>> builderClassType = aClass.asSubclass(JavaTypingUtils.castClass(SimpleBuilder.class));
                simpleBuilder = Reflection.instance(builderClassType);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return simpleBuilder;
    }

    private static <T, A> InitializingBuilder<? extends T, A> parseInitializingBuilder(String s, Class<T> typeToParse, Class<A> argType) {
        InitializingBuilder<? extends T, A> initializingBuilder = null;
        try {
            Class<?> aClass = Class.forName(s);
            if (typeToParse.isAssignableFrom(aClass)) {
                Class<? extends T> directClassType = aClass.asSubclass(typeToParse);
                initializingBuilder = a -> Reflection.instance(directClassType, a);
            } else if (InitializingBuilder.class.isAssignableFrom(aClass)) {
                Class<? extends InitializingBuilder<? extends T, A>> builderClassType = aClass.asSubclass(JavaTypingUtils.castClass(InitializingBuilder.class));
                initializingBuilder = Reflection.instance(builderClassType);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return initializingBuilder;
    }

    public static void read() {
        String path = PathTools.getRelativeFolderPath(PathTools.FolderStructure.BASE_INPUT_FOLDER) + "test.json";
        try (Reader stream = new FileReader(path)) {
            JsonReader in = new JsonReader(stream);
            ConfiguratorCollection configuratorCollection = CONFIGURATOR_COLLECTION_TYPE_ADAPTER.read(in);
            GlobalComponentRepository cr = new GlobalComponentRepository();
            configuratorCollection.registerAlgorithmParameters(cr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {

        read();
    }

}
