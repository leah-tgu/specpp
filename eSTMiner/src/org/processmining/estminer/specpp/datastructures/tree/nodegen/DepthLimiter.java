package org.processmining.estminer.specpp.datastructures.tree.nodegen;

public class DepthLimiter implements ExpansionStopper {

    private int maxDepth;

    public DepthLimiter(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public DepthLimiter() {
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public void updateToMinimum(int maxDepth) {
        this.maxDepth = Math.min(this.maxDepth, maxDepth);
    }

    @Override
    public boolean allowedToExpand(PlaceNode placeNode) {
        return placeNode.getPlace().size() < maxDepth;
    }
}
