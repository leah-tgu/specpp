package org.processmining.estminer.specpp.datastructures.tree.heuristic;

import org.processmining.estminer.specpp.base.Evaluation;

public interface HeuristicValue<T extends HeuristicValue<T>> extends Evaluation, Comparable<T> {

}
