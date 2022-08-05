package org.processmining.estminer.specpp.representations.log;

import org.processmining.estminer.specpp.base.Evaluation;
import org.processmining.estminer.specpp.representations.BitMask;
import org.processmining.estminer.specpp.representations.vectorization.Mathable;
import org.processmining.estminer.specpp.representations.vectorization.OrderingRelation;
import org.processmining.estminer.specpp.traits.Copyable;
import org.processmining.estminer.specpp.traits.IndexAccessible;
import org.processmining.estminer.specpp.traits.PartiallyOrdered;

import java.util.EnumSet;
import java.util.stream.IntStream;

public interface VariantMarkingHistories<T extends VariantMarkingHistories<T>> extends Mathable<T>, Copyable<T>, PartiallyOrdered<T>, IndexAccessible<IntStream>, OnlyCoversIndexSubset, Evaluation {

    IntStream variantIndices();

    BitMask computePerfectlyFitting();

    BitMask computePerfectlyFittingAmong(BitMask variantMask);

    EnumSet<OrderingRelation> orderingRelations(T other);

    BitMask computeNotNonnegative();

    BitMask computeNotNonnegativeAmong(BitMask variantMask);

    BitMask computeNonnegative();

    BitMask computeNonnegativeAmong(BitMask variantMask);
}
