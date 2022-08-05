package org.processmining.estminer.specpp.representations.tree.heuristic;

import org.processmining.estminer.specpp.base.Evaluation;

public interface NodeHeuristic<T extends NodeHeuristic<T>> extends Evaluation, Comparable<T> {

}
