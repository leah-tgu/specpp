package org.processmining.estminer.specpp.config;

import org.processmining.estminer.specpp.base.PostProcessor;
import org.processmining.estminer.specpp.base.Result;
import org.processmining.estminer.specpp.base.impls.PostProcessorPipe;
import org.processmining.estminer.specpp.componenting.system.ComponentSystemAdapter;
import org.processmining.estminer.specpp.est.InstrumentedPostProcessor;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

public class PostProcessingConfiguration<R extends Result, F extends Result> extends Configuration {
    private final Deque<SimpleBuilder<? extends PostProcessor<?, ?>>> list;

    public PostProcessingConfiguration(ComponentSystemAdapter csa, SimpleBuilder<? extends PostProcessor<R, ?>> first, SimpleBuilder<? extends PostProcessor<?, F>> last, Deque<SimpleBuilder<? extends PostProcessor<?, ?>>> list) {
        super(csa);
        this.list = list;
        assert !list.isEmpty() && first != null && last != null;
        assert list.peekFirst().equals(first);
        assert list.peekLast().equals(last);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public PostProcessor<R, F> createPostProcessorPipeline() {
        if (list.isEmpty()) return null;
        Iterator<SimpleBuilder<? extends PostProcessor<?, ?>>> it = list.iterator();

        PostProcessor curr = createFrom(it.next());
        while (it.hasNext()) {
            SimpleBuilder<? extends PostProcessor<?, ?>> next = it.next();
            curr = new PostProcessorPipe(curr, createFrom(next));
        }

        return (PostProcessor<R, F>) curr;
    }

    public static class Configurator<R extends Result, F extends Result> implements ComponentInitializerBuilder<PostProcessingConfiguration<R, F>> {

        private final SimpleBuilder<? extends PostProcessor<R, ?>> first;
        private final SimpleBuilder<? extends PostProcessor<?, F>> last;
        private final Deque<SimpleBuilder<? extends PostProcessor<?, ?>>> list;

        public Configurator(SimpleBuilder<? extends PostProcessor<R, F>> initial) {
            this.first = initial;
            this.last = initial;
            list = new LinkedList<>();
            list.add(initial);
        }

        public Configurator(SimpleBuilder<? extends PostProcessor<R, ?>> first, SimpleBuilder<? extends PostProcessor<?, F>> last, Deque<SimpleBuilder<? extends PostProcessor<?, ?>>> list) {
            this.first = first;
            this.last = last;
            this.list = list;
        }


        public <T extends Result> Configurator<R, T> instrumentedProcessor(String label, SimpleBuilder<PostProcessor<F, T>> builder) {
            return processor(new InstrumentedPostProcessor.Builder<>(label, builder));
        }

        public <T extends Result> Configurator<R, T> processor(SimpleBuilder<? extends PostProcessor<? super F, T>> builder) {
            list.add(builder);
            return new Configurator<>(first, builder, list);
        }

        public PostProcessingConfiguration<R, F> build(ComponentSystemAdapter cs) {
            return new PostProcessingConfiguration<>(cs, first, last, list);
        }

    }

}
