package org.processmining.specpp.headless.batch;

import org.processmining.specpp.supervision.observations.CSVRowEvent;
import org.processmining.specpp.util.EvalUtils;
import org.processmining.specpp.util.PrintingUtils;

import java.util.Objects;

public class SPECppEvaluated implements CSVRowEvent {

    public static final String[] COLUMN_NAMES = new String[]{"run identifier", "perfectly fitting traces", "alignment based fitness", "etc precision", "f1", "duration"};

    private final String runIdentifier;
    private final double fittingTraces, alignmentFitness, etcPrecision;
    private final long duration;

    public SPECppEvaluated(String runIdentifier, double fittingTraces, double alignmentFitness, double etcPrecision, long duration) {
        this.runIdentifier = runIdentifier;
        this.fittingTraces = fittingTraces;
        this.alignmentFitness = alignmentFitness;
        this.etcPrecision = etcPrecision;
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SPECppEvaluated that = (SPECppEvaluated) o;

        return Objects.equals(runIdentifier, that.runIdentifier);
    }

    @Override
    public String toString() {
        return "SPECppEvaluated" + PrintingUtils.stringifyRow(getColumnNames(), toRow());
    }

    @Override
    public int hashCode() {
        return runIdentifier.hashCode();
    }

    @Override
    public String[] getColumnNames() {
        return COLUMN_NAMES;
    }

    @Override
    public String[] toRow() {
        return new String[]{runIdentifier, Double.toString(fittingTraces), Double.toString(alignmentFitness), Double.toString(etcPrecision), Double.toString(EvalUtils.computeF1(alignmentFitness, etcPrecision)), Long.toString(duration)};
    }

}
