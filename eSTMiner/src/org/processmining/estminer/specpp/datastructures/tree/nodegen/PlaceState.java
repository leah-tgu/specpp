package org.processmining.estminer.specpp.datastructures.tree.nodegen;

import org.processmining.estminer.specpp.datastructures.BitMask;
import org.processmining.estminer.specpp.datastructures.tree.base.NodeState;
import org.processmining.estminer.specpp.traits.ProperlyPrintable;

public class PlaceState implements NodeState, ProperlyPrintable {

    private BitMask presetChildrenMask, postsetChildrenMask;

    public PlaceState(BitMask presetChildrenMask, BitMask postsetChildrenMask) {
        this.presetChildrenMask = presetChildrenMask;
        this.postsetChildrenMask = postsetChildrenMask;
    }

    public BitMask getPresetChildrenMask() {
        return presetChildrenMask;
    }

    public BitMask getPostsetChildrenMask() {
        return postsetChildrenMask;
    }

    public void setPresetChildrenMask(BitMask presetChildrenMask) {
        this.presetChildrenMask = presetChildrenMask;
    }

    public void setPostsetChildrenMask(BitMask postsetChildrenMask) {
        this.postsetChildrenMask = postsetChildrenMask;
    }

    protected PlaceState() {
        this(new BitMask(), new BitMask());
    }

    public static PlaceState inst(BitMask presetChildrenMask, BitMask postsetChildrenMask) {
        return new PlaceState(presetChildrenMask, postsetChildrenMask);
    }

    public static PlaceState initialState() {
        return new PlaceState();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlaceState that = (PlaceState) o;

        if (!presetChildrenMask.equals(that.presetChildrenMask)) return false;
        return postsetChildrenMask.equals(that.postsetChildrenMask);
    }

    @Override
    public int hashCode() {
        int result = presetChildrenMask.hashCode();
        result = 31 * result + postsetChildrenMask.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PlaceState{" + "presetChildrenMask=" + presetChildrenMask + ", postsetChildrenMask=" + postsetChildrenMask + '}';
    }
}
