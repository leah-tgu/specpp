package org.processmining.specpp.prom.alg;

import org.processmining.specpp.base.IdentityPostProcessor;
import org.processmining.specpp.base.PostProcessor;
import org.processmining.specpp.componenting.traits.ProvidesEvaluators;
import org.processmining.specpp.config.SimpleBuilder;
import org.processmining.specpp.datastructures.petri.PetriNet;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.datastructures.tree.base.HeuristicStrategy;
import org.processmining.specpp.datastructures.tree.heuristic.DoubleScore;
import org.processmining.specpp.datastructures.tree.heuristic.HeuristicUtils;
import org.processmining.specpp.datastructures.tree.heuristic.InterestingnessHeuristic;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.specpp.evaluation.fitness.AbsolutelyNoFrillsFitnessEvaluator;
import org.processmining.specpp.evaluation.fitness.ForkJoinFitnessEvaluator;
import org.processmining.specpp.evaluation.heuristics.DeltaAdaptationFunction;
import org.processmining.specpp.evaluation.markings.LogHistoryMaker;
import org.processmining.specpp.postprocessing.ProMConverter;
import org.processmining.specpp.postprocessing.ReplayBasedImplicitnessPostProcessing;
import org.processmining.specpp.postprocessing.SelfLoopPlaceMerger;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class FrameworkBridge {

    public static final List<BridgedHeuristics> HEURISTICS = Arrays.asList(BridgedHeuristics.values());
    public static final List<BridgedEvaluators> EVALUATORS = Arrays.asList(BridgedEvaluators.values());
    public static final List<BridgedDeltaAdaptationFunctions> DELTA_FUNCTIONS = Arrays.asList(BridgedDeltaAdaptationFunctions.values());
    public static final List<BridgedPostProcessors> POST_PROCESSORS = Arrays.asList(BridgedPostProcessors.values());

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
        Identity(new BridgedPostProcessor("Identity", PetriNet.class, PetriNet.class, () -> IdentityPostProcessor::new)), ReplayBasedImplicitPlaceRemoval(new BridgedPostProcessor("Replay-Based Implicit Place Removal", PetriNet.class, PetriNet.class, ReplayBasedImplicitnessPostProcessing.Builder::new)), SelfLoopPlacesMerging(new BridgedPostProcessor("Merging Self Loop Places", PetriNet.class, PetriNet.class, () -> SelfLoopPlaceMerger::new)), ProMPetrinetConversion(new BridgedPostProcessor("ProM Petri net Conversion", PetriNet.class, ProMPetrinetWrapper.class, () -> ProMConverter::new));
        private final BridgedPostProcessor bpp;

        BridgedPostProcessors(BridgedPostProcessor bpp) {
            this.bpp = bpp;
        }

        public BridgedPostProcessor getBridge() {
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
        Static(new BridgedEvaluator("Static Delta", DeltaAdaptationFunction.Builder::new));

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

        public SimpleBuilder<? extends T> getBuilder() {
            return builderSupplier.get();
        }
    }

    public static class BridgedEvaluator extends Bridged<ProvidesEvaluators> {

        BridgedEvaluator(String printableName, Supplier<SimpleBuilder<? extends ProvidesEvaluators>> simpleBuilderSupplier) {
            super(printableName, simpleBuilderSupplier);
        }

    }

    public static class BridgedTreeHeuristic extends Bridged<HeuristicStrategy<PlaceNode, DoubleScore>> {

        BridgedTreeHeuristic(String printableName, Supplier<SimpleBuilder<? extends HeuristicStrategy<PlaceNode, DoubleScore>>> simpleBuilderSupplier) {
            super(printableName, simpleBuilderSupplier);
        }
    }

    public static class BridgedPostProcessor extends Bridged<PostProcessor<?, ?>> {

        private final Class<?> inType;
        private final Class<?> outType;

        BridgedPostProcessor(String printableName, Class<?> inType, Class<?> outType, Supplier<SimpleBuilder<? extends PostProcessor<?, ?>>> simpleBuilderSupplier) {
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
