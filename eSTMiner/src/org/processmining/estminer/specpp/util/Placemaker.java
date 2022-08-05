package org.processmining.estminer.specpp.util;

import org.processmining.estminer.specpp.representations.encoding.BitEncodedSet;
import org.processmining.estminer.specpp.representations.encoding.IntEncoding;
import org.processmining.estminer.specpp.representations.encoding.IntEncodings;
import org.processmining.estminer.specpp.representations.petri.Place;
import org.processmining.estminer.specpp.representations.petri.Transition;

public class Placemaker {
    private final IntEncoding<Transition> preEnc, postEnc;

    public Placemaker(IntEncodings<Transition> encs) {
        this.preEnc = encs.pre();
        this.postEnc = encs.post();
    }

    public class InPro {

        BitEncodedSet<Transition> pre, post;

        public InPro() {
            this.pre = BitEncodedSet.empty(preEnc);
            this.post = BitEncodedSet.empty(postEnc);
        }

        public InPro preset(Transition... pre) {
            this.pre.addAll(pre);
            return this;
        }

        public InPro postset(Transition... post) {
            this.post.addAll(post);
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
