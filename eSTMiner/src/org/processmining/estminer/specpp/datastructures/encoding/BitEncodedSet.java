package org.processmining.estminer.specpp.datastructures.encoding;

import org.processmining.estminer.specpp.datastructures.BitMask;
import org.processmining.estminer.specpp.traits.Copyable;
import org.processmining.estminer.specpp.traits.ProperlyHashable;

import java.util.Iterator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class BitEncodedSet<T> implements EncodedSet<T, Integer>, ProperlyHashable, SetQueries<BitEncodedSet<T>>, MutatingSetOperations<BitEncodedSet<T>>, Copyable<BitEncodedSet<T>> {

    protected final IntEncoding<T> encoding;
    protected final BitMask set;

    public BitEncodedSet(IntEncoding<T> encoding, BitMask set) {
        this.encoding = encoding;
        this.set = set;
    }

    protected BitEncodedSet(IntEncoding<T> encoding) {
        this(encoding, new BitMask());
    }

    public static <T> BitEncodedSet<T> empty(IntEncoding<T> enc) {
        return new BitEncodedSet<>(enc);
    }

    public BitEncodedSet<T> reencode(IntEncoding<T> newEncoding) {
        BitMask mask = new BitMask();
        set.stream()
           .mapToObj(encoding::decode)
           .filter(newEncoding::isInDomain)
           .mapToInt(newEncoding::encode)
           .forEach(mask::set);
        return new BitEncodedSet<>(newEncoding, mask);
    }

    public IntEncoding<T> getEncoding() {
        return encoding;
    }


    public BitMask getBitMask() {
        return set;
    }

    private void checkEnc(BitEncodedSet<T> other) {
        if (!sameEnc(other)) throw new InconsistentEncodingException();
    }

    private boolean sameEnc(BitEncodedSet<T> other) {
        return encoding.equals(other.getEncoding());
    }

    @Override
    public void intersection(BitEncodedSet<T> other) throws InconsistentEncodingException {
        if (sameEnc(other)) set.and(other.set);
        else {
            Iterator<T> it = streamElements().iterator();
            while (it.hasNext()) {
                T next = it.next();
                if (!other.contains(next)) remove(next);
            }
        }
    }

    @Override
    public void union(BitEncodedSet<T> other) throws InconsistentEncodingException {
        if (sameEnc(other)) set.or(other.set);
        else {
            other.streamElements().filter(encoding::isInDomain).forEach(this::add);
        }
    }

    @Override
    public void setminus(BitEncodedSet<T> other) throws InconsistentEncodingException {
        if (sameEnc(other)) set.andNot(other.set);
        else {
            other.streamElements().filter(encoding::isInDomain).forEach(this::remove);
        }
    }

    public boolean containsIndex(int index) {
        return encoding.isIntInRange(index) && set.get(index);
    }

    @Override
    public boolean contains(T item) {
        return encoding.isInDomain(item) && set.get(encoding.encode(item));
    }

    public BitMask kMaxRangeMask(int k) {
        BitMask mask = new BitMask(encoding.size());
        int r = kMaxRange(k);
        if (0 < k && k <= cardinality()) mask.set(kMaxIndex(k) + 1, kMaxIndex(k - 1));
        else if (k == cardinality() + 1) mask.set(kMaxIndex(k) + 1, encoding.size());
        return mask;
    }

    public int minimalIndex() {
        return kMinIndex(1);
    }

    public int maximalIndex() {
        return kMaxIndex(1);
    }

    public T minimalElement() {
        return encoding.decode(minimalIndex());
    }

    public T maximalElement() {
        return encoding.decode(maximalIndex());
    }

    public int indexRange() {
        return encoding.size() - 1;
    }

    // this is clamped. watch out for the semantics. same for kMaxIndex
    public int kMinIndex(int k) {
        if (k <= 0) return -1;
        else if (k > cardinality()) return maxSize();
        return set.kMinIndex(k);
    }

    public int kMaxIndex(int k) {
        if (k <= 0) return maxSize();
        else if (k > cardinality()) return -1;
        return set.kMaxIndex(k);
    }

    public T kMinElement(int k) {
        int index = kMinIndex(k);
        return index >= 0 ? encoding.decode(index) : null;
    }

    public T kMaxElement(int k) {
        int index = kMaxIndex(k);
        return index >= 0 ? encoding.decode(index) : null;
    }

    public int kMinRange(int k) {
        if (k <= 0 || k > cardinality()) return 0;
        return kMinIndex(k - 1) - kMinIndex(k);
    }

    @SafeVarargs
    @Override
    public final void addAll(T... items) {
        for (T item : items) {
            add(item);
        }
    }

    /*
        example
        set = 0011001, c = |set| = 3, L = maxIndex(set) = 6
        k   kMaxRange
        0   0
        1   0
        2   2
        3   0
        4   2
        5   0
        6   0
        invariant: sum_k=0^L( kMaxRange(k) ) = L - c, i.e. #of zeros in bitset
         */
    public int kMaxRange(int k) {
        if (0 <= k || k > cardinality() + 1) return 0;
        else if (k == cardinality() + 1) return indexRange() - kMaxIndex(k);
        else return kMaxIndex(k - 1) - kMaxIndex(k) - 1;
    }

    @Override
    public boolean add(T item) {
        return encoding.isInDomain(item) && addIndex(encoding.encode(item));
    }

    @Override
    public void clear() {
        set.clear();
    }

    @Override
    public boolean isEmpty() {
        return set.isEmpty();
    }

    public int maxSize() {
        return encoding.size();
    }


    public boolean addIndex(int index) {
        if (!encoding.isIntInRange(index)) return false;
        boolean temp = set.get(index);
        set.set(index);
        return !temp;
    }

    @Override
    public boolean remove(T item) {
        return removeIndex(encoding.encode(item));
    }

    public boolean removeIndex(int index) {
        if (!encoding.isIntInRange(index)) return false;
        boolean temp = set.get(index);
        set.clear(index);
        return temp;
    }

    public int cardinality() {
        return set.cardinality();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BitEncodedSet<?> that = (BitEncodedSet<?>) o;

        if (!encoding.equals(that.encoding)) return false;
        return set.equals(that.set);
    }

    @Override
    public int hashCode() {
        int result = encoding.hashCode();
        result = 31 * result + set.hashCode();
        return result;
    }

    @Override
    public BitEncodedSet<T> copy() {
        return new BitEncodedSet<>(encoding, (BitMask) set.clone());
    }

    @Override
    public Iterator<T> iterator() {
        return streamElements().iterator();
    }

    public Stream<T> streamElements() {
        return set.stream().mapToObj(encoding::decode);
    }

    public IntStream streamIndices() {
        return set.stream();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = set.nextSetBit(0); i >= 0 && i < set.length(); i = set.nextSetBit(i + 1)) {
            sb.append(encoding.decode(i).toString());
            if (set.nextSetBit(i + 1) > 0) sb.append(", ");
        }
        sb.append("}");
        return sb.toString();
    }


    public void clearMask(BitMask bitMask) {
        if (bitMask.previousSetBit(bitMask.length()) <= maxSize()) set.setminus(bitMask);
    }

    public void retainMask(BitMask bitMask) {
        if (bitMask.previousSetBit(bitMask.length()) <= maxSize()) set.intersection(bitMask);
    }

    public void addMask(BitMask bitMask) {
        if (bitMask.previousSetBit(bitMask.length()) <= maxSize()) set.union(bitMask);
    }

    @Override
    public boolean intersects(BitEncodedSet<T> other) {
        if (sameEnc(other)) return set.intersects(other.getBitMask());
        else return streamElements().anyMatch(other::contains);
    }

    @Override
    public boolean setEquality(BitEncodedSet<T> other) {
        if (sameEnc(other)) return set.setEquality(other.getBitMask());
        else return streamElements().allMatch(other::contains) && other.streamElements().allMatch(this::contains);
    }

    @Override
    public boolean isSubsetOf(BitEncodedSet<T> other) {
        if (sameEnc(other)) return set.isSubsetOf(other.getBitMask());
        else return streamElements().allMatch(other::contains);
    }

    @Override
    public boolean isSupersetOf(BitEncodedSet<T> other) {
        if (sameEnc(other)) return set.isSupersetOf(other.getBitMask());
        else return other.streamElements().allMatch(this::contains);
    }
}
