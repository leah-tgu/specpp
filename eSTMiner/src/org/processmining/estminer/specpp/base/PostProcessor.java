package org.processmining.estminer.specpp.base;

import org.processmining.estminer.specpp.base.impls.PostProcessorPipe;

import java.util.function.Function;

public interface PostProcessor<S extends Result, T extends Result> extends Function<S, T> {

    T postProcess(S result);

    default <V extends Result> PostProcessor<V, T> compose(PostProcessor<V, ? extends S> before) {
        return new PostProcessorPipe<>(before, this);
    }

    @Override
    default T apply(S s) {
        return postProcess(s);
    }
}
