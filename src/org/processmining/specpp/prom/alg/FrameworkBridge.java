package org.processmining.specpp.prom.alg;

import org.processmining.specpp.base.IdentityPostProcessor;
import org.processmining.specpp.base.PostProcessor;
import org.processmining.specpp.componenting.traits.ProvidesEvaluators;
import org.processmining.specpp.config.SimpleBuilder;
import org.processmining.specpp.datastructures.petri.PetriNet;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.datastructures.tree.base.HeuristicStrategy;
import org.processmining.specpp.datastructures.tree.heuristic.HeuristicUtils;
import org.processmining.specpp.datastructures.tree.heuristic.InterestingnessHeuristic;
import org.processmining.specpp.datastructures.tree.heuristic.TreeNodeScore;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.specpp.evaluation.fitness.AbsolutelyNoFrillsFitnessEvaluator;
import org.processmining.specpp.evaluation.fitness.ForkJoinFitnessEvaluator;
import org.processmining.specpp.evaluation.heuristics.ConstantDelta;
import org.processmining.specpp.evaluation.heuristics.LinearDelta;
import org.processmining.specpp.evaluation.heuristics.NoDelta;
import org.processmining.specpp.evaluation.heuristics.SigmoidDelta;
import org.processmining.specpp.evaluation.markings.LogHistoryMaker;
import org.processmining.specpp.postprocessing.ProMConverter;
import org.processmining.specpp.postprocessing.ReplayBasedImplicitnessPostProcessing;
import org.processmining.specpp.postprocessing.SelfLoopPlaceMerger;
import org.processmining.specpp.preprocessing.orderings.ActivityOrderingStrategy;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class FrameworkBridge {

    public static final List<BridgedActivityOrderingStrategies> ORDERING_STRATEGIES = Arrays.asList(BridgedActivityOrderingStrategies.values());
    public static final List<BridgedHeuristics> HEURISTICS = Arrays.asList(BridgedHeuristics.values());
    public static final List<BridgedEvaluators> EVALUATORS = Arrays.asList(BridgedEvaluators.values());
    public static final List<BridgedDeltaAdaptationFunctions> DELTA_FUNCTIONS = Arrays.asList(BridgedDeltaAdaptationFunctions.values());
    public static final List<AnnotatedPostProcessor> POST_PROCESSORS = Arrays.asList(BridgedPostProcessors.ReplayBasedImplicitPlaceRemoval.getBridge(), BridgedPostProcessors.SelfLoopPlacesMerging.getBridge(), BridgedPostProcessors.ProMPetrinetConversion.getBridge());

    public enum BridgedHeuristics {
        PlaceInterestingness(new BridgedTreeHeuristic("Place Interestingness", () -> InterestingnessHeuristic::new)), BFS_Emulation(new BridgedTreeHeuristic("BFS Emulation", () -> HeuristicUtils::bfs)), DFS_Emulation(new BridgedTreeHeuristic("DFS Emulation", () -> HeuristicUtils::dfs));

        private final BridgedTreeHeuristic bth;

        BridgedHeuristics(BridgedTreeHeuristic bth) {
            this.bth = bth;
        }

        @Override
        public String toString() {
            return bth.toString();
        }

        public BridgedTreeHeuristic getBridge() {
            return bth;
        }
    }

    public enum BridgedPostProcessors {
        Identity(new AnnotatedPostProcessor("Identity", PetriNet.class, PetriNet.class, () -> IdentityPostProcessor::new)), ReplayBasedImplicitPlaceRemoval(new AnnotatedPostProcessor("Replay-Based Implicit Place Removal", PetriNet.class, PetriNet.class, ReplayBasedImplicitnessPostProcessing.Builder::new)), SelfLoopPlacesMerging(new AnnotatedPostProcessor("Self-Loop Places Merging", PetriNet.class, PetriNet.class, () -> SelfLoopPlaceMerger::new)), ProMPetrinetConversion(new AnnotatedPostProcessor("Conversion to ProM Petri net", PetriNet.class, ProMPetrinetWrapper.class, () -> ProMConverter::new));
        private final AnnotatedPostProcessor bpp;

        BridgedPostProcessors(AnnotatedPostProcessor bpp) {
            this.bpp = bpp;
        }

        public AnnotatedPostProcessor getBridge() {
            return bpp;
        }


        @Override
        public String toString() {
            return bpp.toString();
        }
    }

    public enum BridgedEvaluators {
        BaseFitness(new BridgedEvaluator("Base Fitness Evaluator", () -> AbsolutelyNoFrillsFitnessEvaluator::new)), ForkJoinFitness(new BridgedEvaluator("Concurrent Fitness Evaluator", () -> ForkJoinFitnessEvaluator::new)), MarkingHistory(new BridgedEvaluator("Marking History Computer", () -> LogHistoryMaker::new));

        private final BridgedEvaluator be;

        BridgedEvaluators(BridgedEvaluator be) {
            this.be = be;
        }

        public BridgedEvaluator getBridge() {
            return be;
        }

        @Override
        public String toString() {
            return be.toString();
        }

    }

    public enum BridgedDeltaAdaptationFunctions {
        None(new BridgedEvaluator("None", NoDelta.Builder::new)), Constant(new BridgedEvaluator("Constant Delta", ConstantDelta.Builder::new)), Linear(new BridgedEvaluator("Linear Delta", LinearDelta.Builder::new)), Sigmoid(new BridgedEvaluator("Sigmoid Delta", SigmoidDelta.Builder::new));

        private final BridgedEvaluator be;

        BridgedDeltaAdaptationFunctions(BridgedEvaluator be) {
            this.be = be;
        }

        public BridgedEvaluator getBridge() {
            return be;
        }

        @Override
        public String toString() {
            return be.toString();
        }
    }

    public enum BridgedActivityOrderingStrategies {
        AverageFirstOccurrenceIndex(new AnnotatedActivityOrderingStrategy("Average First Occurrence Index", org.processmining.specpp.preprocessing.orderings.AverageFirstOccurrenceIndex.class)),
        AverageTraceOccurrence(new AnnotatedActivityOrderingStrategy("Average Trace Occurrence", org.processmining.specpp.preprocessing.orderings.AverageTraceOccurrence.class)),
        AbsoluteTraceFrequency(new AnnotatedActivityOrderingStrategy("Absolute Trace Frequency", org.processmining.specpp.preprocessing.orderings.AbsoluteTraceFrequency.class)),
        AbsoluteActivityFrequency(new AnnotatedActivityOrderingStrategy("Absolute Activity Frequency", org.processmining.specpp.preprocessing.orderings.AbsoluteActivityFrequency.class)),
        Lexicographic(new AnnotatedActivityOrderingStrategy("Lexicographic", org.processmining.specpp.preprocessing.orderings.Lexicographic.class));

        private final AnnotatedActivityOrderingStrategy strategy;

        BridgedActivityOrderingStrategies(AnnotatedActivityOrderingStrategy strategy) {
            this.strategy = strategy;
        }

        public Class<? extends ActivityOrderingStrategy> getStrategyClass() {
            return strategy.getStrategyClass();
        }

        @Override
        public String toString() {
            return strategy.toString();
        }
    }

    public static class Bridged<T> {
        private final String printableName;
        private final Supplier<SimpleBuilder<? extends T>> builderSupplier;

        Bridged(String printableName, Supplier<SimpleBuilder<? extends T>> builderSupplier) {
            this.printableName = printableName;
            this.builderSupplier = builderSupplier;
        }

        @Override
        public String toString() {
            return printableName;
        }

        public String getPrintableName() {
            return printableName;
        }

        public SimpleBuilder<? extends T> getBuilder() {
            return builderSupplier.get();
        }
    }

    public static class BridgedEvaluator extends Bridged<ProvidesEvaluators> {

        BridgedEvaluator(String printableName, Supplier<SimpleBuilder<? extends ProvidesEvaluators>> simpleBuilderSupplier) {
            super(printableName, simpleBuilderSupplier);
        }

    }

    public static class BridgedTreeHeuristic extends Bridged<HeuristicStrategy<PlaceNode, TreeNodeScore>> {

        BridgedTreeHeuristic(String printableName, Supplier<SimpleBuilder<? extends HeuristicStrategy<PlaceNode, TreeNodeScore>>> simpleBuilderSupplier) {
            super(printableName, simpleBuilderSupplier);
        }
    }

    public static class AnnotatedActivityOrderingStrategy {

        private final Class<? extends ActivityOrderingStrategy> strategyClass;
        private final String printableName;

        AnnotatedActivityOrderingStrategy(String printableName, Class<? extends ActivityOrderingStrategy> strategyClass) {
            this.printableName = printableName;
            this.strategyClass = strategyClass;
        }

        @Override
        public String toString() {
            return printableName;
        }

        public Class<? extends ActivityOrderingStrategy> getStrategyClass() {
            return strategyClass;
        }
    }

    public static class AnnotatedPostProcessor extends Bridged<PostProcessor<?, ?>> {

        private final Class<?> inType;
        private final Class<?> outType;

        AnnotatedPostProcessor(String printableName, Class<?> inType, Class<?> outType, Supplier<SimpleBuilder<? extends PostProcessor<?, ?>>> simpleBuilderSupplier) {
            super(printableName, simpleBuilderSupplier);
            this.inType = inType;
            this.outType = outType;
        }

        public Class<?> getInType() {
            return inType;
        }

        public Class<?> getOutType() {
            return outType;
        }

        @Override
        public String toString() {
            return "[" + getInType().getSimpleName() + " => " + getOutType().getSimpleName() + "]" + " " + super.toString();
        }
    }


}
