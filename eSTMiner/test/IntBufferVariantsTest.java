import org.junit.Test;
import org.processmining.estminer.specpp.datastructures.BitMask;
import org.processmining.estminer.specpp.datastructures.encoding.IndexSubset;
import org.processmining.estminer.specpp.datastructures.vectorization.IntVectorSubsetStorage;

import java.nio.IntBuffer;

public class IntBufferVariantsTest {

    @Test
    public void wrapping(){
        BitMask m = new BitMask();
        m.set(2, 4);
        m.set(8);
        m.set(10);
        m.set(13);
        Integer[] indexArr = m.stream().boxed().toArray(Integer[]::new);
        IndexSubset subset = IndexSubset.of(m);
        IntVectorSubsetStorage ivss = IntVectorSubsetStorage.zeros(subset, m.stream().toArray());
        System.out.println(ivss);
        System.out.println(ivss.getVector(3).estimateSize());

        IntBuffer intBuffer = ivss.vectorBuffer(3);
        while (intBuffer.hasRemaining()) {
            int i = intBuffer.get();
            System.out.println(i);
        }

    }

}
