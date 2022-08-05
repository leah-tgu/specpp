package org.processmining.estminer.specpp.util;

import org.processmining.estminer.specpp.datastructures.encoding.BitEncodedSet;
import org.processmining.estminer.specpp.datastructures.encoding.IntEncoding;
import org.processmining.estminer.specpp.datastructures.encoding.IntEncodings;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.petri.Transition;

public class Placemaker {
    private final IntEncoding<Transition> preEnc, postEnc;

    public Placemaker(IntEncodings<Transition> encodings) {
        this.preEnc = encodings.pre();
        this.postEnc = encodings.post();
    }

    public class InPro {

        final BitEncodedSet<Transition> pre;
        final BitEncodedSet<Transition> post;

        public InPro() {
            this.pre = BitEncodedSet.empty(preEnc);
            this.post = BitEncodedSet.empty(postEnc);
        }

        public InPro preset(Transition... pre) {
            for (Transition t : pre) {
                this.pre.add(t);
            }
            return this;
        }

        public InPro postset(Transition... post) {
            for (Transition t : post) {
                this.post.add(t);
            }
            return this;
        }

        public Place get() {
            return new Place(pre, post);
        }

    }

    public InPro start() {
        return new InPro();
    }

    public InPro preset(Transition... pre) {
        return start().preset(pre);
    }

}
