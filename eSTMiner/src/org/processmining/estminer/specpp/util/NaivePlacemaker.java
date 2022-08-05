package org.processmining.estminer.specpp.util;

import org.processmining.estminer.specpp.representations.encoding.IntEncodings;
import org.processmining.estminer.specpp.representations.petri.Transition;

import java.util.Map;
import java.util.stream.Collectors;

public class NaivePlacemaker extends Placemaker {

    private final Map<String, Transition> preLabels;
    private final Map<String, Transition> postLabels;

    public NaivePlacemaker(IntEncodings<Transition> encs) {
        super(encs);
        preLabels = encs.pre().domain().collect(Collectors.toMap(Transition::toString, t -> t));
        postLabels = encs.post().domain().collect(Collectors.toMap(Transition::toString, t -> t));
    }

    public class NaiveInPro extends InPro {

        public NaiveInPro preset(String... labels) {
            Transition[] ts = new Transition[labels.length];
            for (int i = 0; i < labels.length; i++) {
                ts[i] = preLabels.get(labels[i]);
            }
            preset(ts);
            return this;
        }

        public NaiveInPro postset(String... labels) {
            Transition[] ts = new Transition[labels.length];
            for (int i = 0; i < labels.length; i++) {
                ts[i] = postLabels.get(labels[i]);
            }
            postset(ts);
            return this;
        }

    }

    @Override
    public NaiveInPro start() {
        return new NaiveInPro();
    }

    public NaiveInPro preset(String... labels) {
        return start().preset(labels);
    }

}
