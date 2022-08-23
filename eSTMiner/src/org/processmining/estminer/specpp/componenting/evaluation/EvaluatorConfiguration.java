package org.processmining.estminer.specpp.componenting.evaluation;

import com.google.common.collect.ImmutableList;
import org.processmining.estminer.specpp.base.Evaluable;
import org.processmining.estminer.specpp.base.Evaluation;
import org.processmining.estminer.specpp.base.Evaluator;
import org.processmining.estminer.specpp.componenting.system.AbstractGlobalComponentSystemUser;
import org.processmining.estminer.specpp.componenting.system.ComponentCollection;
import org.processmining.estminer.specpp.componenting.traits.ProvidesEvaluators;
import org.processmining.estminer.specpp.config.ComponentInitializerBuilder;
import org.processmining.estminer.specpp.config.Configuration;
import org.processmining.estminer.specpp.config.SimpleBuilder;
import org.processmining.estminer.specpp.util.Reflection;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class EvaluatorConfiguration extends Configuration {

    private final ImmutableList<SimpleBuilder<ProvidesEvaluators>> evaluatorProviderBuilders;

    public EvaluatorConfiguration(ComponentCollection cc, ImmutableList<SimpleBuilder<ProvidesEvaluators>> evaluatorProviderBuilders) {
        super(cc);
        this.evaluatorProviderBuilders = evaluatorProviderBuilders;
    }

    public List<ProvidesEvaluators> createEvaluators() {
        return evaluatorProviderBuilders.stream().map(this::createFrom).collect(Collectors.toList());
    }

    public static class Configurator implements ComponentInitializerBuilder<EvaluatorConfiguration> {
        private final List<SimpleBuilder<ProvidesEvaluators>> evaluatorProviderBuilders;

        public Configurator() {
            evaluatorProviderBuilders = new LinkedList<>();
        }


        public <I extends Evaluable, E extends Evaluation> Configurator evaluator(Class<I> evaluableClass, Class<E> evaluationClass, Class<Evaluator<? super I, ? extends E>> evaluatorClass) {
            SimpleBuilder<ProvidesEvaluators> builder = () -> {
                class Wrap extends AbstractGlobalComponentSystemUser implements ProvidesEvaluators {
                    public Wrap() {
                        componentSystemAdapter().provide(EvaluationRequirements.evaluator(evaluableClass, evaluationClass, Reflection.instance(evaluatorClass)::eval));
                    }
                }
                return new Wrap();
            };
            evaluatorProviderBuilders.add(builder);
            return this;
        }

        public Configurator evaluatorProvider(SimpleBuilder<ProvidesEvaluators> builder) {
            evaluatorProviderBuilders.add(builder);
            return this;
        }

        public EvaluatorConfiguration build(ComponentCollection cc) {
            return new EvaluatorConfiguration(cc, ImmutableList.copyOf(evaluatorProviderBuilders));
        }
    }

}
