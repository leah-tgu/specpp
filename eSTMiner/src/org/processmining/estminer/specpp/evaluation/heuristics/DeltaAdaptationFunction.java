package org.processmining.estminer.specpp.evaluation.heuristics;

import org.processmining.estminer.specpp.base.Evaluator;
import org.processmining.estminer.specpp.componenting.data.ParameterRequirements;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.estminer.specpp.componenting.system.ComponentSystemAwareBuilder;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.tree.heuristic.DoubleScore;
import org.processmining.estminer.specpp.datastructures.util.EvaluationParameterTuple2;

public class DeltaAdaptationFunction implements Evaluator<EvaluationParameterTuple2<Place, Integer>, DoubleScore> {

    public static class Builder extends ComponentSystemAwareBuilder<DeltaAdaptationFunction> {

        private final DelegatingDataSource<Double> delta = new DelegatingDataSource<>();

        public Builder() {
            componentSystemAdapter().require(ParameterRequirements.DELTA_PARAMETERS, delta);
        }

        @Override
        protected DeltaAdaptationFunction buildIfFullySatisfied() {
            return new DeltaAdaptationFunction(delta.getData());
        }
    }


    private final double delta;
    private final DoubleScore base;


    public DeltaAdaptationFunction(double delta) {
        this.delta = delta;
        base = new DoubleScore(delta);
    }


    @Override
    public DoubleScore eval(EvaluationParameterTuple2<Place, Integer> input) {
        Place place = input.getT1();
        Integer treeLevel = input.getT2();
        if (place.size() == treeLevel) return new DoubleScore(0);
        else return base;
    }
}
