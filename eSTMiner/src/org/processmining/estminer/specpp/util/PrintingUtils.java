package org.processmining.estminer.specpp.util;

import org.processmining.estminer.specpp.base.impls.SpecPP;
import org.processmining.estminer.specpp.componenting.data.FulfilledDataRequirement;
import org.processmining.estminer.specpp.composition.PlaceCollection;
import org.processmining.estminer.specpp.datastructures.petri.PetriNet;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.petri.ProMPetrinetWrapper;

import java.util.stream.Collectors;

public class PrintingUtils {
    public static String quote(Object o) {
        return "\"" + o + "\"";
    }


    public static String printParameters(SpecPP<Place, PlaceCollection, PetriNet, ProMPetrinetWrapper> specPP) {
        return specPP.getComponentRepository()
                     .parameters()
                     .fulfilledRequirements()
                     .stream()
                     .map(f -> (FulfilledDataRequirement<?>) f)
                     .map(f -> "\t" + f.getComparable().toString() + " = " + f.getContent()
                                                                                        .getData()
                                                                                        .toString())
                     .collect(Collectors.joining("\n", "Configured Parameters:\n", ""));
    }
}
