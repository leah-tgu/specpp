package org.processmining.estminer.specpp.headless;

import org.processmining.estminer.specpp.base.Evaluator;
import org.processmining.estminer.specpp.base.impls.SpecPP;
import org.processmining.estminer.specpp.componenting.data.DataRequirements;
import org.processmining.estminer.specpp.componenting.data.DataSourceCollection;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluatorCollection;
import org.processmining.estminer.specpp.componenting.system.ComponentRepository;
import org.processmining.estminer.specpp.composition.PlaceCollection;
import org.processmining.estminer.specpp.config.SimpleBuilder;
import org.processmining.estminer.specpp.datastructures.XLogDataSource;
import org.processmining.estminer.specpp.datastructures.encoding.IntEncodings;
import org.processmining.estminer.specpp.datastructures.log.impls.DenseVariantMarkingHistories;
import org.processmining.estminer.specpp.datastructures.petri.PetriNet;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.estminer.specpp.datastructures.petri.Transition;
import org.processmining.estminer.specpp.evaluation.fitness.AggregatedBasicFitnessEvaluation;
import org.processmining.estminer.specpp.evaluation.fitness.BasicVariantFitnessStatus;
import org.processmining.estminer.specpp.evaluation.fitness.FullBasicFitnessEvaluation;
import org.processmining.estminer.specpp.orchestra.BaseSpecOpsConfigBundle;
import org.processmining.estminer.specpp.orchestra.SpecOpsSetup;
import org.processmining.estminer.specpp.util.NaivePlacemaker;
import org.processmining.estminer.specpp.util.TestFactory;

import java.util.Arrays;

public class Playground {

    public static void main(String[] args) {
        XLogDataSource xLogDataSource = new XLogDataSource(TestFactory.LOG_4, true);

        SpecPP<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper> specPP = SpecOpsSetup.specOps(BaseSpecOpsConfigBundle::new, xLogDataSource);

        // ========================================= //

        System.out.println("// ========================================= //");
        System.out.println("POST EXECUTION");

        ComponentRepository cr = specPP.getComponentRepository();

        DataSourceCollection dc = cr.dataSources();

        IntEncodings<Transition> transitionEncodings = dc.askForData(DataRequirements.ENC_TRANS);

        EvaluatorCollection ec = cr.evaluators();
        Evaluator<Place, DenseVariantMarkingHistories> historiesEvaluator = ec.askForEvaluator(EvaluationRequirements.PLACE_MARKING_HISTORY);

        Evaluator<Place, AggregatedBasicFitnessEvaluation> aggregatedBasicFitnessEvaluator = ec.askForEvaluator(EvaluationRequirements.AGG_PLACE_FITNESS);

        Evaluator<Place, FullBasicFitnessEvaluation> fullBasicFitnessEvaluator = ec.askForEvaluator(EvaluationRequirements.FULL_PLACE_FITNESS);

        playAround(cr, new NaivePlacemaker(transitionEncodings), historiesEvaluator, aggregatedBasicFitnessEvaluator, fullBasicFitnessEvaluator);
    }

    public static void playAround(ComponentRepository cr, NaivePlacemaker placemaker, Evaluator<Place, DenseVariantMarkingHistories> markingHistoriesEvaluator, Evaluator<Place, AggregatedBasicFitnessEvaluation> basicFitnessFractionsEvaluator, Evaluator<Place, FullBasicFitnessEvaluation> fullBasicFitnessEvaluator) {

        Place p1 = placemaker.preset("place order", "send reminder")
                             .postset("cancel order", "pay", "send reminder")
                             .get();
        Place p2 = placemaker.preset("place order").postset("cancel order", "pay").get();


        DenseVariantMarkingHistories h1 = markingHistoriesEvaluator.eval(p1);
        System.out.println(h1);
        DenseVariantMarkingHistories h2 = markingHistoriesEvaluator.eval(p2);
        System.out.println(h2);

        System.out.println(h1.getIndexSubset());
        System.out.println(h1.gt(h2));
        System.out.println(h2.lt(h1));
        System.out.println(h2.gt(h1));
        System.out.println(h1.lt(h2));

        System.out.println(Arrays.toString(BasicVariantFitnessStatus.values()));
        System.out.println("basic fitness");
        System.out.println(basicFitnessFractionsEvaluator.eval(p1));
        System.out.println(basicFitnessFractionsEvaluator.eval(p2));
        System.out.println("full fitness");
        System.out.println(fullBasicFitnessEvaluator.eval(p1));
        System.out.println(fullBasicFitnessEvaluator.eval(p2));

        SimpleBuilder<PlaceCollection> createComposition = cr.dataSources()
                                                             .askForData(DataRequirements.<Place, PlaceCollection, PetriNet>proposerComposerConfiguration())::createComposition;
        PlaceCollection comp1 = createComposition.get();
        comp1.accept(p1);
        System.out.println(comp1.rateImplicitness(p2));
        comp1.accept(p2);
        System.out.println(comp1);

        PlaceCollection comp2 = createComposition.get();
        comp2.accept(p2);
        System.out.println(comp2.rateImplicitness(p1));
        comp2.accept(p1);
        System.out.println(comp2);

    }

}
