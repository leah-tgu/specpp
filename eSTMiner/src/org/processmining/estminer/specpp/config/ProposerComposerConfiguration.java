package org.processmining.estminer.specpp.config;

import org.processmining.estminer.specpp.base.*;
import org.processmining.estminer.specpp.componenting.system.ComponentCollection;

public class ProposerComposerConfiguration<C extends Candidate, I extends Composition<C>, R extends Result> extends Configuration {
    private final SimpleBuilder<? extends Proposer<C>> proposerBuilder;
    private final SimpleBuilder<? extends I> compositionBuilder;
    private final InitializingBuilder<? extends Composer<C, I, R>, ? super I> composerBuilder;

    public ProposerComposerConfiguration(ComponentCollection cs, SimpleBuilder<? extends Proposer<C>> proposerBuilder, SimpleBuilder<? extends I> compositionBuilder, InitializingBuilder<? extends Composer<C, I, R>, ? super I> composerBuilder) {
        super(cs);
        this.compositionBuilder = compositionBuilder;
        this.composerBuilder = composerBuilder;
        this.proposerBuilder = proposerBuilder;
    }

    public static <C extends Candidate, I extends Composition<C>, R extends Result> ProposerComposerConfiguration.Configurator<C, I, R> configure() {
        return new ProposerComposerConfiguration.Configurator<>();
    }

    public Proposer<C> createProposer() {
        return createFrom(proposerBuilder);
    }

    public I createComposition() {
        return createFrom(compositionBuilder);
    }

    public Composer<C, I, R> createComposer() {
        return createFrom(composerBuilder, createComposition());
    }

    public static class Configurator<C extends Candidate, I extends Composition<C>, R extends Result> implements ComponentInitializerBuilder<ProposerComposerConfiguration<C, I, R>> {

        private SimpleBuilder<? extends Proposer<C>> proposerBuilder;
        private SimpleBuilder<? extends I> compositionBuilder;
        private InitializingBuilder<? extends Composer<C, I, R>, ? super I> composerBuilder;

        public Configurator<C, I, R> proposer(SimpleBuilder<? extends Proposer<C>> proposerBuilder) {
            this.proposerBuilder = proposerBuilder;
            return this;
        }

        public Configurator<C, I, R> composition(SimpleBuilder<? extends I> compositionBuilder) {
            this.compositionBuilder = compositionBuilder;
            return this;

        }

        public Configurator<C, I, R> composer(InitializingBuilder<? extends Composer<C, I, R>, ? super I> composerBuilder) {
            this.composerBuilder = composerBuilder;
            return this;

        }

        public ProposerComposerConfiguration<C, I, R> build(ComponentCollection cs) {
            return new ProposerComposerConfiguration<>(cs, proposerBuilder, compositionBuilder, composerBuilder);
        }

    }


}
