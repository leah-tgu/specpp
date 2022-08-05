package org.processmining.estminer.specpp.datastructures.vectorization;

import org.processmining.estminer.specpp.datastructures.BitMask;
import org.processmining.estminer.specpp.datastructures.encoding.IndexSubset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.PrimitiveIterator;
import java.util.function.*;
import java.util.stream.IntStream;

public class IVSComputations {


    public static IntVectorStorage interleave(IntVectorStorage ivs1, IntVectorStorage ivs2) {
        return interleave(ivs1, IntUnaryOperator.identity(), ivs2, IntUnaryOperator.identity());
    }

    public static IntVectorStorage interleave(IntVectorStorage ivs1, IntUnaryOperator ivs1Mapper, IntVectorStorage ivs2, IntUnaryOperator ivs2Mapper) {
        assert ivs1.startIndices.length == ivs2.startIndices.length;
        assert ivs1.storage.length == ivs2.storage.length;

        int[] originalStartIndices = ivs1.startIndices;

        int L = originalStartIndices.length;
        int[] startIndices = new int[L];
        for (int i = 0; i < L; i++) {
            startIndices[i] = originalStartIndices[i] * 2;
        }

        int T = ivs1.storage.length;
        int[] storage = new int[2 * T];
        int startIndex = 0;
        for (int i = 0; i < L - 1; ) {
            int endIndex = originalStartIndices[++i];
            for (int j = startIndex; j < endIndex; j++) {
                storage[j * 2] = ivs1Mapper.applyAsInt(ivs1.storage[j]);
                storage[j * 2 + 1] = ivs2Mapper.applyAsInt(ivs2.storage[j]);
            }
            startIndex = endIndex;
        }

        return new IntVectorStorage(storage, startIndices);
    }

    public static IntVectorStorage interleaveOn(BitMask indices, IntVectorStorage leftVectors, IntUnaryOperator leftOperator, IntVectorStorage rightVectors, IntUnaryOperator rightOperator) {
        ArrayList<Integer> startIndices = new ArrayList<>();
        ArrayList<Integer> data = new ArrayList<>();

        int acc = 0;
        startIndices.add(acc);
        for (int i = indices.nextSetBit(0); i >= 0 && i < Integer.MAX_VALUE; i = indices.nextSetBit(i + 1)) {

            PrimitiveIterator.OfInt leftIt = leftVectors.viewVector(i).iterator();
            PrimitiveIterator.OfInt rightIt = rightVectors.viewVector(i).iterator();

            int vectorLength = 0;
            while (leftIt.hasNext() && rightIt.hasNext()) {
                int x = leftIt.nextInt(), y = rightIt.nextInt();
                data.add(leftOperator.applyAsInt(x));
                data.add(rightOperator.applyAsInt(y));
                vectorLength += 2;
            }

            acc += vectorLength;
            startIndices.add(acc);
        }

        int[] dataArray = data.stream()
                              .mapToInt(i -> i)
                              .toArray();
        int[] startIndicesArray = startIndices.stream()
                                              .mapToInt(i -> i)
                                              .toArray();
        return new IntVectorSubsetStorage(IndexSubset.of(indices), dataArray, startIndicesArray);
    }

    /* OLD
    public static IntVectorStorage interleaveOn(BitMask leftIndices, IntVectorStorage leftVectors, BitMask rightIndices, IntVectorStorage rightVectors) {
        return interleaveOn(leftIndices, leftVectors, IntUnaryOperator.identity(), rightIndices, rightVectors, IntUnaryOperator.identity());
    }

    public static IntVectorStorage interleaveOn(BitMask leftIndices, IntVectorStorage leftVectors, IntUnaryOperator leftMapper, BitMask rightIndices, IntVectorStorage rightVectors, IntUnaryOperator rightMapper) {
        int[] startIndices = Stream.concat(IntStream.of(0).boxed(), Streams.zip(leftIndices.stream()
                                                                                           .mapToObj(leftVectors::getVectorLength), rightIndices.stream()
                                                                                                                                                .mapToObj(rightVectors::getVectorLength), Math::min)
                                                                           .map(i -> 2 * i)

        ).mapToInt(i -> i).toArray();
        Arrays.parallelPrefix(startIndices, Integer::sum);

        int[] leftStartIndices = leftVectors.startIndices;
        int[] rightStartIndices = rightVectors.startIndices;
        int[] data = new int[startIndices[startIndices.length - 1]];

        int index = 0;
        int startIndex = 0;
        PrimitiveIterator.OfInt left = leftIndices.stream().iterator(), right = rightIndices.stream().iterator();
        while (left.hasNext() && right.hasNext()) {
            int i = left.nextInt(), j = right.nextInt();
            int leftStartIndex = leftStartIndices[i];
            int rightStartIndex = rightStartIndices[j];
            int endIndex = startIndices[++index];
            int L = endIndex - startIndex;
            for (int k = 0; k < L / 2; k++) {
                data[startIndex + k * 2] = leftMapper.applyAsInt(leftVectors.storage[leftStartIndex + k]);
                data[startIndex + k * 2 + 1] = rightMapper.applyAsInt(rightVectors.storage[rightStartIndex + k]);
            }
            startIndex = endIndex;
        }

        return new IntVectorStorage(data, startIndices);
    }
*/

    public static IntVectorStorage basicSumConvolution(IntVectorStorage ivs) {
        return convolve(ivs, 2, () -> 0, Integer::sum);
    }

    public static IntVectorStorage basicSubConvolution(IntVectorStorage ivs) {
        return convolve(ivs, 2, () -> 0, (a, b) -> a - b);
    }

    public static IntVectorStorage basicMaxConvolution(IntVectorStorage ivs) {
        return convolve(ivs, 2, () -> Integer.MIN_VALUE, Math::max);
    }


    /*
    arraylist version which does not require pre computations which however requires extensive (un)boxing of primitive ints
    public static IntVectorStorage interleaveOn(IntStream leftIndices, IntVectorStorage leftVectors, IntUnaryOperator leftMapper, IntStream rightIndices, IntVectorStorage rightVectors, IntUnaryOperator rightMapper) {
        ArrayList<Integer> dataList = new ArrayList<>();
        ArrayList<Integer> startIndicesList = new ArrayList<>();

        int sum = 0;
        PrimitiveIterator.OfInt left = leftIndices.iterator(), right = rightIndices.iterator();
        while (left.hasNext() && right.hasNext()) {
            int i = left.nextInt(), j = right.nextInt();
            startIndicesList.add(sum);
            PrimitiveIterator.OfInt leftStream = leftVectors.viewVector(i).iterator();
            PrimitiveIterator.OfInt rightStream = rightVectors.viewVector(j).iterator();
            while (leftStream.hasNext() && rightStream.hasNext()) {
                int next = sum % 2 == 0 ? leftMapper.applyAsInt(leftStream.nextInt()) : rightMapper.applyAsInt(rightStream.nextInt());
                dataList.add(next);
                sum++;
            }
        }
        startIndicesList.add(sum);
        int[] data = dataList.stream().mapToInt(i -> i).toArray();
        int[] startIndices = startIndicesList.stream().mapToInt(i -> i).toArray();
        return new IntVectorStorage(data, startIndices);
    }
     */
    public static IntVectorStorage convolve(IntVectorStorage ivs, int stride, IntSupplier initial, IntBinaryOperator convolution) {
        assert ivs.getTotalSize() % stride == 0;
        int L = ivs.getTotalSize() / stride;
        int[] originalData = ivs.storage;
        int[] originalStartIndices = ivs.startIndices;
        int[] data = new int[L];
        int[] startIndices = new int[originalStartIndices.length];
        for (int i = 0; i < originalStartIndices.length; i++) {
            startIndices[i] = originalStartIndices[i] / stride;
        }

        int startIndex = 0, endIndex;
        for (int i = 0; i < originalStartIndices.length; i++) {
            endIndex = originalStartIndices[++i];
            for (int j = startIndex; j < endIndex; j += stride) {
                int acc = initial.getAsInt();
                for (int k = 0; k < stride; k++) {
                    acc = convolution.applyAsInt(acc, originalData[j + k]);
                }
                data[j / stride] = acc;
            }
            startIndex = endIndex;
        }

        return new IntVectorStorage(data, startIndices);
    }

    public static boolean gtOn(IntStream leftIndices, IntVectorStorage leftVectors, IntStream rightIndices, IntVectorStorage rightVectors) {
        return predicateOn((l, r) -> l < r, (l, r) -> l != r, Boolean::logicalOr, leftIndices, leftVectors, rightIndices, rightVectors);
    }

    public static boolean ltOn(IntStream leftIndices, IntVectorStorage leftVectors, IntStream rightIndices, IntVectorStorage rightVectors) {
        return predicateOn((l, r) -> l > r, (l, r) -> l != r, Boolean::logicalOr, leftIndices, leftVectors, rightIndices, rightVectors);
    }

    public static boolean predicateOn(IntBiPredicate shortCircuitingPredicate, IntBiPredicate accumulatedPredicate, BooleanBinaryOperator accumulationCombiner, IntStream leftIndices, IntVectorStorage leftVectors, IntStream rightIndices, IntVectorStorage rightVectors) {
        PrimitiveIterator.OfInt leftIds = leftIndices.iterator(), rightIds = rightIndices.iterator();
        if (!rightIds.hasNext()) return true;
        boolean acc = false;
        while (leftIds.hasNext() && rightIds.hasNext()) {
            int i = leftIds.nextInt();
            int j = rightIds.nextInt();
            PrimitiveIterator.OfInt left = leftVectors.viewVector(i).iterator();
            PrimitiveIterator.OfInt right = rightVectors.viewVector(j).iterator();
            while (left.hasNext() && right.hasNext()) {
                int l = left.nextInt(), r = right.nextInt();
                acc = accumulationCombiner.test(acc, accumulatedPredicate.test(l, r));
                if (shortCircuitingPredicate.test(l, r)) return false;
            }
            if (right.hasNext()) return false;
        }
        if (rightIds.hasNext()) return false;
        return acc;
    }

    public static BitMask vectorWiseAccumulation(IntVectorStorage ivs, IntBinaryOperator operator, IntPredicate runningCondition, IntPredicate postCondition) {
        return vectorWiseAccumulation(ivs, ivs.indexStream(), operator, runningCondition, postCondition);
    }

    public static BitMask vectorWiseAccumulation(IntVectorStorage ivs, BitMask bm, IntBinaryOperator operator, IntPredicate runningCondition, IntPredicate postCondition) {
        return vectorWiseAccumulation(ivs, bm.stream(), operator, runningCondition, postCondition);
    }

    public static BitMask vectorWiseAccumulation(IntVectorStorage ivs, IntStream indices, IntBinaryOperator operator, IntPredicate runningCondition, IntPredicate postCondition) {
        BitMask bs = new BitMask();
        PrimitiveIterator.OfInt it = indices.iterator();
        while (it.hasNext()) {
            int i = it.next();
            int startIndex = ivs.startIndices[i];
            int endIndex = ivs.startIndices[i + 1];
            int accumulator = ivs.storage[startIndex];
            for (int j = startIndex + 1; j < endIndex && runningCondition.test(accumulator); j++) {
                accumulator = operator.applyAsInt(accumulator, ivs.storage[j]);
            }
            if (postCondition.test(accumulator) && runningCondition.test(accumulator)) bs.set(i);
        }
        return bs;
    }

    public static IntVectorStorage vectorwiseCumulation(IntVectorStorage ivs) {
        return vectorwiseCumulation(ivs, Integer::sum);
    }

    public static IntVectorStorage vectorwiseCumulation(IntVectorStorage ivs, IntBinaryOperator operator) {
        int[] result = Arrays.copyOf(ivs.storage, ivs.storage.length);
        int startIndex = 0;
        for (int i = 0; i < ivs.getVectorCount(); ) {
            int endIndex = ivs.startIndices[++i];
            for (int j = startIndex + 1; j < endIndex; j++) {
                result[j] = operator.applyAsInt(result[j - 1], result[j]);
            }
            startIndex = endIndex;
        }
        return new IntVectorStorage(result, ivs.startIndices); // not copying startIndices is intentional
    }

    public static class VectorwiseAccumulatingComputation<E, A, R> {

        IntBiFunction<E> elementComputation;

        Supplier<A> newAccumulatorSupplier;
        BiFunction<A, E, A> accumulationCombiner;
        Supplier<R> newResultSupplier;
        BiFunction<R, A, R> vectorResultCombiner;

    }

    public static class EnumSetComputation<E extends Enum<E>> {
        IntBinaryOperator compute;
        IntFunction<EnumSet<E>> toAdd;
        IntFunction<EnumSet<E>> toRemove;
        EnumSet<E> baseEnumSet;

    }


    public static final EnumSetComputation<OrderingRelation> partialOrderComputation = new EnumSetComputation<OrderingRelation>() {{
        compute = Integer::compare;
        toAdd = c -> {
            if (c < 0) return EnumSet.of(OrderingRelation.lt, OrderingRelation.neq);
            else if (c > 0) return EnumSet.of(OrderingRelation.gt, OrderingRelation.neq);
            else return EnumSet.noneOf(OrderingRelation.class);
        };
        toRemove = c -> {
            if (c < 0) return EnumSet.of(OrderingRelation.gt, OrderingRelation.gtEq, OrderingRelation.eq);
            else if (c > 0) return EnumSet.of(OrderingRelation.lt, OrderingRelation.ltEq, OrderingRelation.eq);
            return EnumSet.of(OrderingRelation.gt, OrderingRelation.lt);
        };
        baseEnumSet = OrderingRelation.BASE;
    }};

    public static <E, A, R> R computationOn(final VectorwiseAccumulatingComputation<E, A, R> computation, IntStream leftIndices, IntVectorStorage leftVectors, IntStream rightIndices, IntVectorStorage rightVectors) {
        PrimitiveIterator.OfInt leftIds = leftIndices.iterator(), rightIds = rightIndices.iterator();
        R res = computation.newResultSupplier.get();
        while (leftIds.hasNext() && rightIds.hasNext()) {
            int i = leftIds.nextInt();
            int j = rightIds.nextInt();
            PrimitiveIterator.OfInt left = leftVectors.viewVector(i).iterator();
            PrimitiveIterator.OfInt right = rightVectors.viewVector(j).iterator();
            A acc = computation.newAccumulatorSupplier.get();
            while (left.hasNext() && right.hasNext()) {
                int l = left.nextInt(), r = right.nextInt();
                E v = computation.elementComputation.apply(l, r);
                acc = computation.accumulationCombiner.apply(acc, v);
            }
            res = computation.vectorResultCombiner.apply(res, acc);
        }
        return res;
    }

    public static EnumSet<OrderingRelation> orderingRelationsOn(IntStream leftIndices, IntVectorStorage leftVectors, IntStream rightIndices, IntVectorStorage rightVectors) {
        return computationOn(partialOrderComputation, leftIndices, leftVectors, rightIndices, rightVectors);
    }

    public static <E extends Enum<E>> EnumSet<E> computationOn(final EnumSetComputation<E> computation, IntStream leftIndices, IntVectorStorage leftVectors, IntStream rightIndices, IntVectorStorage rightVectors) {
        PrimitiveIterator.OfInt leftIds = leftIndices.iterator(), rightIds = rightIndices.iterator();
        EnumSet<E> res = computation.baseEnumSet;
        while (leftIds.hasNext() && rightIds.hasNext()) {
            int i = leftIds.nextInt();
            int j = rightIds.nextInt();
            PrimitiveIterator.OfInt left = leftVectors.viewVector(i).iterator();
            PrimitiveIterator.OfInt right = rightVectors.viewVector(j).iterator();
            while (left.hasNext() && right.hasNext()) {
                int c = computation.compute.applyAsInt(left.nextInt(), right.nextInt());
                res.addAll(computation.toAdd.apply(c));
                res.removeAll(computation.toRemove.apply(c));
            }
        }
        return res;
    }

}
