package org.processmining.estminer.specpp.representations.tree.heuristic;

public class DoubleScore implements NodeHeuristic<DoubleScore> {

    private final double score;

    public DoubleScore(double score) {
        this.score = score;
    }

    @Override
    public int compareTo(DoubleScore o) {
        return Double.compare(score, o.score);
    }

    @Override
    public String toString() {
        return Double.toString(score);
    }

}
