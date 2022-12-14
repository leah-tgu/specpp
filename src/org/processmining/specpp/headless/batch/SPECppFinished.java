package org.processmining.specpp.headless.batch;

import org.processmining.specpp.composition.BasePlaceComposition;
import org.processmining.specpp.datastructures.petri.CollectionOfPlaces;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.orchestra.ExecutionEnvironment;
import org.processmining.specpp.prom.computations.OngoingComputation;
import org.processmining.specpp.prom.computations.OngoingStagedComputation;
import org.processmining.specpp.supervision.observations.CSVRowEvent;

import java.util.Objects;

public class SPECppFinished implements CSVRowEvent {

    public static final String[] COLUMN_NAMES = new String[]{"run identifier", "started", "completed", "pec cycling [ms]", "post processing [ms]", "total [ms]", "terminated successfully?"};

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SPECppFinished that = (SPECppFinished) o;

        return Objects.equals(runIdentifier, that.runIdentifier);
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
        OngoingComputation mc = execution.getMasterComputation();
        OngoingComputation dc = execution.getDiscoveryComputation();
        OngoingStagedComputation ppc = execution.getPostProcessingComputation();
        return new String[]{runIdentifier, Objects.toString(mc.getStart()), Objects.toString(mc.getEnd()), dc.hasTerminated() ? Long.toString(dc.calculateRuntime()
                                                                                                                                                .toMillis()) : "dnf", ppc.hasTerminated() ? Long.toString(ppc.calculateRuntime()
                                                                                                                                                                                                             .toMillis()) : "dnf", mc.hasTerminated() ? Long.toString(mc.calculateRuntime().toMillis()) : "dnf", Boolean.toString(execution.hasTerminatedSuccessfully())};
    }
}
