package org.processmining.estminer.specpp.postprocessing;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.math3.util.Combinations;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.processmining.estminer.specpp.base.PostProcessor;
import org.processmining.estminer.specpp.datastructures.petri.PetriNet;
import org.processmining.estminer.specpp.datastructures.petri.Place;

import java.util.Set;

public class SelfLoopPlaceMerger implements PostProcessor<PetriNet, PetriNet> {
    @Override
    public PetriNet postProcess(PetriNet result) {
        Set<Place> places = result.getPlaces();

        // TODO implement

        for (Place place : places) {

        }

        throw new NotImplementedException();
    }


}
