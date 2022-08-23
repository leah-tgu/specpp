package org.processmining.estminer.specpp.datastructures.tree.nodegen;

import org.processmining.estminer.specpp.datastructures.encoding.BitMask;
import org.processmining.estminer.specpp.datastructures.encoding.IntEncoding;
import org.processmining.estminer.specpp.datastructures.encoding.IntEncodings;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.petri.Transition;

public class WiringMatrix implements PotentialExpansionsFilter {

    private final boolean[][] wiringMatrix;
    private final IntEncoding<Transition> rowEncoding;
    private final IntEncoding<Transition> colEncoding;

    public WiringMatrix(IntEncodings<Transition> transitionEncodings) {
        rowEncoding = transitionEncodings.pre();
        colEncoding = transitionEncodings.post();
        int rows = transitionEncodings.pre().size();
        int cols = transitionEncodings.post().size();
        wiringMatrix = new boolean[cols][rows];
    }

    public void wire(Place place) {
        for (Transition tIn : place.preset()) {
            int i = rowEncoding.encode(tIn);
            for (Transition tOut : place.postset()) {
                int j = colEncoding.encode(tOut);
                wiringMatrix[i][j] = true;
            }
        }
    }

    @Override
    public BitMask filterPotentialSetExpansions(Place place, BitMask expansions, MonotonousPlaceGenerationLogic.ExpansionType expansionType) {
        if (expansionType == MonotonousPlaceGenerationLogic.ExpansionType.Preset) {
            return BitMask.of(expansions.stream()
                                        .filter(i -> place.postset()
                                                          .streamIndices()
                                                          .noneMatch(j -> wiringMatrix[i][j])));
        } else {
            return BitMask.of(expansions.stream()
                                        .filter(i -> place.preset()
                                                          .streamIndices()
                                                          .noneMatch(j -> wiringMatrix[j][i])));

        }
    }


}
