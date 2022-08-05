package org.processmining.estminer.specpp.base;

public interface Proposer<C extends Candidate> {

    C proposeCandidate();

    boolean isExhausted();

}
