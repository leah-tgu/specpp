package org.processmining.estminer.specpp.config;

import org.processmining.estminer.specpp.base.Candidate;
import org.processmining.estminer.specpp.base.Result;
import org.processmining.estminer.specpp.componenting.system.ComponentCollection;
import org.processmining.estminer.specpp.componenting.system.link.ComposerComponent;
import org.processmining.estminer.specpp.componenting.system.link.CompositionComponent;
import org.processmining.estminer.specpp.componenting.system.link.ProposerComponent;

public class ProposerComposerConfiguration<C extends Candidate, I extends CompositionComponent<C>, R extends Result> extends Configuration {
    private final SimpleBuilder<? extends ProposerComponent<C>> proposerBuilder;
    private final SimpleBuilder<? extends I> compositionBuilder;
    private final InitializingBuilder<? extends ComposerComponent<C, I, R>, ? super I> terminalComposerBuilder;
    private final InitializingBuilder<? extends ComposerComponent<C, I, R>, ComposerComponent<C, I, R>>[] composerBuilderChain;

    public ProposerComposerConfiguration(ComponentCollection componentSystemAdapter, SimpleBuilder<? extends ProposerComponent<C>> proposerBuilder, SimpleBuilder<? extends I> compositionBuilder, InitializingBuilder<? extends ComposerComponent<C, I, R>, ? super I> terminalComposerBuilder, InitializingBuilder<? extends ComposerComponent<C, I, R>, ComposerComponent<C, I, R>>[] composerBuilderChain) {
        super(componentSystemAdapter);
        this.proposerBuilder = proposerBuilder;
        this.compositionBuilder = compositionBuilder;
        this.terminalComposerBuilder = terminalComposerBuilder;
        this.composerBuilderChain = composerBuilderChain;
    }

    public ProposerComponent<C> createProposer() {
        return createFrom(proposerBuilder);
    }

    public I createComposition() {
        return createFrom(compositionBuilder);
    }

    public ComposerComponent<C, I, R> createTerminalComposer() {
        return createFrom(terminalComposerBuilder, createComposition());
    }

    public ComposerComponent<C, I, R> createComposerChain() {
        ComposerComponent<C, I, R> prev = createTerminalComposer();
        for (int i = composerBuilderChain.length - 1; i >= 0; i--) {
            prev = createFrom(composerBuilderChain[i], prev);
        }
        return prev;
    }

    public ComposerComponent<C, I, R> createComposer() {
        if (composerBuilderChain == null || composerBuilderChain.length < 1) return createTerminalComposer();
        else return createComposerChain();
    }


    public static class Configurator<C extends Candidate, I extends CompositionComponent<C>, R extends Result> implements ComponentInitializerBuilder<ProposerComposerConfiguration<C, I, R>> {

        private SimpleBuilder<? extends ProposerComponent<C>> proposerBuilder;
        private SimpleBuilder<? extends I> compositionBuilder;
        private InitializingBuilder<? extends ComposerComponent<C, I, R>, ? super I> terminalComposerBuilder;
        private InitializingBuilder<? extends ComposerComponent<C, I, R>, ComposerComponent<C, I, R>>[] composerBuilderChain;

        public Configurator<C, I, R> proposer(SimpleBuilder<? extends ProposerComponent<C>> proposerBuilder) {
            this.proposerBuilder = proposerBuilder;
            return this;
        }

        public Configurator<C, I, R> composition(SimpleBuilder<? extends I> compositionBuilder) {
            this.compositionBuilder = compositionBuilder;
            return this;

        }

        public Configurator<C, I, R> composer(InitializingBuilder<? extends ComposerComponent<C, I, R>, ? super I> composerBuilder) {
            return terminalComposer(composerBuilder);
        }

        public Configurator<C, I, R> terminalComposer(InitializingBuilder<? extends ComposerComponent<C, I, R>, ? super I> composerBuilder) {
            this.terminalComposerBuilder = composerBuilder;
            return this;
        }

        public Configurator<C, I, R> composerChain(InitializingBuilder<? extends ComposerComponent<C, I, R>, ComposerComponent<C, I, R>>... composerBuilderChain) {
            this.composerBuilderChain = composerBuilderChain;
            return this;
        }

        public ProposerComposerConfiguration<C, I, R> build(ComponentCollection cs) {
            return new ProposerComposerConfiguration<>(cs, proposerBuilder, compositionBuilder, terminalComposerBuilder, composerBuilderChain);
        }

    }


}
