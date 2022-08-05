package org.processmining.estminer.specpp.representations.log.impls;

import org.processmining.estminer.specpp.representations.log.Variant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LogBuilderImpl implements LogBuilder<LogImpl> {

    private List<Variant> variants;
    private List<Integer> frequencies;

    public LogBuilderImpl() {
        variants = new ArrayList<>();
        frequencies = new ArrayList<>();
    }

    @Override
    public LogBuilder<LogImpl> appendVariant(Variant v, int f) {
        if (f > 0) {
            variants.add(v);
            frequencies.add(f);
        }
        return this;
    }

    @Override
    public void setVariants(Variant... variants) {
        this.variants = Arrays.asList(variants);
    }

    @Override
    public void setFrequencies(int... frequencies) {
        this.frequencies = Arrays.stream(frequencies).boxed().collect(Collectors.toList());
    }

    public LogImpl build() {
        int size = variants.size();
        Variant[] arr = new Variant[size];
        for (int i = 0; i < variants.size(); i++) {
            arr[i] = variants.get(i);
        }
        return new LogImpl(arr, frequencies.stream().mapToInt(i -> i).toArray());
    }

}
