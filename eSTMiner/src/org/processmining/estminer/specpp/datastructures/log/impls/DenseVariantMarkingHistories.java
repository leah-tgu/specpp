package org.processmining.estminer.specpp.datastructures.log.impls;

import org.processmining.estminer.specpp.datastructures.encoding.BitMask;
import org.processmining.estminer.specpp.datastructures.encoding.IndexSubset;
import org.processmining.estminer.specpp.datastructures.log.NotCoveringRequiredVariantsException;
import org.processmining.estminer.specpp.datastructures.log.NotCoveringSameVariantsException;
import org.processmining.estminer.specpp.datastructures.log.OnlyCoversIndexSubset;
import org.processmining.estminer.specpp.datastructures.log.VariantMarkingHistories;
import org.processmining.estminer.specpp.datastructures.util.IndexedItem;
import org.processmining.estminer.specpp.datastructures.vectorization.IVSComputations;
import org.processmining.estminer.specpp.datastructures.vectorization.IntVectorStorage;
import org.processmining.estminer.specpp.datastructures.vectorization.OrderingRelation;
import org.processmining.estminer.specpp.datastructures.vectorization.spliterators.IndexedBitMaskSplitty;
import org.processmining.estminer.specpp.evaluation.fitness.BasicVariantFitnessStatus;
import org.processmining.estminer.specpp.util.StreamUtils;

import java.nio.IntBuffer;
import java.util.EnumSet;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class DenseVariantMarkingHistories implements VariantMarkingHistories<DenseVariantMarkingHistories>, OnlyCoversIndexSubset {

    private final IndexSubset indexSubset;
    private final IntVectorStorage markingHistories;
    private static final Predicate<IntStream> isFittingMarkingHistory = StreamUtils.intStreamPredicate(a -> a >= 0, a -> a == 0);
    private static final Predicate<IntStream> isNonnegativeMarkingHistory = StreamUtils.intStreamPredicate(a -> a >= 0, a -> a >= 0);

    private static final Predicate<IntStream> isValidHistory = is -> {
        PrimitiveIterator.OfInt it = is.iterator();
        int i = 0;
        int last = 0;
        while (it.hasNext()) {
            int current = it.nextInt();
            if ((i % 2 == 0 && current > last) || (i % 2 != 0 && current < last)) return false;
            i++;
            last = current;
        }
        return true;
    };

    private static final Predicate<IntStream> hasNegativeMarking = is -> is.anyMatch(i -> i < 0);

    public DenseVariantMarkingHistories(IndexSubset indexSubset, IntVectorStorage markingHistories) {
        this.indexSubset = indexSubset;
        this.markingHistories = markingHistories;
    }

    @Override
    public void add(DenseVariantMarkingHistories other) {
        if (!indexSubset.setEquality(other.indexSubset)) throw new NotCoveringSameVariantsException();
        markingHistories.add(other.markingHistories);
    }

    @Override
    public void subtract(DenseVariantMarkingHistories other) {
        if (!indexSubset.setEquality(other.indexSubset)) throw new NotCoveringSameVariantsException();
        markingHistories.subtract(other.markingHistories);
    }

    @Override
    public void negate() {
        markingHistories.negate();
    }

    @Override
    public DenseVariantMarkingHistories copy() {
        return new DenseVariantMarkingHistories(indexSubset.copy(), markingHistories.copy());
    }

    @Override
    public String toString() {
        return markingHistories.toString();
    }

    public boolean gtOn(BitMask mask, DenseVariantMarkingHistories other) {
        return indexSubset.covers(mask) && other.indexSubset.covers(mask) && IVSComputations.gtOn(indexSubset.mapIndices(mask.stream()), markingHistories, other.indexSubset.mapIndices(mask.stream()), other.markingHistories);
    }

    public boolean ltOn(BitMask mask, DenseVariantMarkingHistories other) {
        return indexSubset.covers(mask) && other.indexSubset.covers(mask) && IVSComputations.ltOn(indexSubset.mapIndices(mask.stream()), markingHistories, other.indexSubset.mapIndices(mask.stream()), other.markingHistories);
    }

    public EnumSet<OrderingRelation> orderingRelationsOn(BitMask mask, DenseVariantMarkingHistories other) {
        if (!indexSubset.covers(mask) || !other.indexSubset.covers(mask))
            throw new NotCoveringRequiredVariantsException();
        return IVSComputations.orderingRelationsOn(indexSubset.mapIndices(mask.stream()), markingHistories, other.indexSubset.mapIndices(mask.stream()), other.markingHistories);
    }

    private IntStream localizeIndicesOf(DenseVariantMarkingHistories other) {
        return indexSubset.mapIndices(other.indexSubset.streamIndices());
    }

    private IntStream localIndicesOf(DenseVariantMarkingHistories other) {
        return other.indexSubset.streamMappingRange();
    }

    @Override
    public boolean gt(DenseVariantMarkingHistories other) {
        return indexSubset.isSupersetOf(other.indexSubset) && IVSComputations.gtOn(localizeIndicesOf(other), markingHistories, localIndicesOf(other), other.markingHistories);
    }

    @Override
    public boolean lt(DenseVariantMarkingHistories other) {
        return indexSubset.isSupersetOf(other.indexSubset) && IVSComputations.ltOn(localizeIndicesOf(other), markingHistories, localIndicesOf(other), other.markingHistories);
    }

    @Override
    public EnumSet<OrderingRelation> orderingRelations(DenseVariantMarkingHistories other) {
        if (!indexSubset.isSupersetOf(other.indexSubset)) throw new NotCoveringRequiredVariantsException();
        return IVSComputations.orderingRelationsOn(localizeIndicesOf(other), markingHistories, localIndicesOf(other), other.markingHistories);
    }

    public IntVectorStorage getData() {
        return markingHistories;
    }

    @Override
    public IndexSubset getIndexSubset() {
        return indexSubset;
    }

    @Override
    public IntStream variantIndices() {
        return indexSubset.streamIndices();
    }

    @Override
    public BitMask computePerfectlyFitting() {
        return BitMask.of(indexSubset.unmapIndices(markingHistories.vectorwisePredicateStream(isFittingMarkingHistory)));
    }

    @Override
    public BitMask computePerfectlyFittingAmong(BitMask variantMask) {
        return BitMask.of(indexSubset.unmapIndices(markingHistories.vectorwisePredicateStream(indexSubset.mapIndices(variantMask.stream()), isFittingMarkingHistory)));
    }

    @Override
    public BitMask computeNotNonnegative() {
        return BitMask.of(indexSubset.unmapIndices(markingHistories.vectorwisePredicateStream(hasNegativeMarking)));
    }

    @Override
    public BitMask computeNotNonnegativeAmong(BitMask variantMask) {
        return BitMask.of(indexSubset.unmapIndices(markingHistories.vectorwisePredicateStream(indexSubset.mapIndices(variantMask.stream()), hasNegativeMarking)));
    }

    @Override
    public BitMask computeNonnegative() {
        return BitMask.of(indexSubset.unmapIndices(markingHistories.vectorwisePredicateStream(isNonnegativeMarkingHistory)));
    }

    @Override
    public BitMask computeNonnegativeAmong(BitMask variantMask) {
        return BitMask.of(indexSubset.unmapIndices(markingHistories.vectorwisePredicateStream(indexSubset.mapIndices(variantMask.stream()), isNonnegativeMarkingHistory)));
    }

    public Spliterator<IndexedItem<IntBuffer>> spliterator() {
        return new IndexedBitMaskSplitty(markingHistories, indexSubset.getIndices(), 0, indexSubset.getIndexCount(), IntUnaryOperator.identity());
    }

    public Spliterator<IndexedItem<IntBuffer>> spliterator(BitMask bitMask) {
        return new IndexedBitMaskSplitty(markingHistories, bitMask, 0, bitMask.cardinality(), IntUnaryOperator.identity());
    }

    public Spliterator<BasicVariantFitnessStatus> basicFitnessComputation() {
        return markingHistories.view().map(DenseVariantMarkingHistories::shortCircuitingReplay).spliterator();
    }

    public Spliterator<BasicVariantFitnessStatus> basicFitnessComputationAmong(BitMask variantMask) {
        return markingHistories.view(indexSubset.mapIndices(variantMask.stream()))
                               .map(DenseVariantMarkingHistories::shortCircuitingReplay)
                               .spliterator();
    }

    public Spliterator<IndexedItem<BasicVariantFitnessStatus>> basicIndexedFitnessComputation() {
        return markingHistories.viewIndexed()
                               .map(ii -> ii.map(DenseVariantMarkingHistories::shortCircuitingReplay))
                               .map(ii -> ii.mapIndex(indexSubset::unmapIndex))
                               .spliterator();
    }

    public Spliterator<IndexedItem<BasicVariantFitnessStatus>> basicIndexedFitnessComputationAmong(BitMask variantMask) {

        return markingHistories.viewIndexed(indexSubset.mapIndices(variantMask.stream()))
                               .map(ii -> ii.map(DenseVariantMarkingHistories::shortCircuitingReplay))
                               .map(ii -> ii.mapIndex(indexSubset::unmapIndex))
                               .spliterator();
    }

    private static BasicVariantFitnessStatus shortCircuitingReplay(IntStream expandedPostPreStream) {
        PrimitiveIterator.OfInt it = expandedPostPreStream.iterator();
        int next = 0;
        while (it.hasNext()) {
            next = it.nextInt();
            if (next < 0) return BasicVariantFitnessStatus.GOES_NEGATIVE;
            else if (next > 1) return BasicVariantFitnessStatus.NON_SAFE;
        }
        return next == 0 ? BasicVariantFitnessStatus.FITTING : BasicVariantFitnessStatus.NOT_ENDING_ON_ZERO;
    }


    @Override
    public IntStream getAt(int index) {
        if (indexSubset.contains(index)) {
            return markingHistories.viewVector(indexSubset.mapIndex(index));
        }
        throw new NotCoveringRequiredVariantsException();
    }
}
