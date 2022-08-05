package org.processmining.estminer.specpp.componenting.evaluation;

import com.google.common.collect.ImmutableList;
import org.processmining.estminer.specpp.base.Evaluable;
import org.processmining.estminer.specpp.base.Evaluation;
import org.processmining.estminer.specpp.base.Evaluator;
import org.processmining.estminer.specpp.componenting.system.AbstractComponentSystemUser;
import org.processmining.estminer.specpp.componenting.system.ComponentSystemAdapter;
import org.processmining.estminer.specpp.componenting.traits.ProvidesEvaluators;
import org.processmining.estminer.specpp.config.ComponentInitializerBuilder;
import org.processmining.estminer.specpp.config.Configuration;
import org.processmining.estminer.specpp.config.SimpleBuilder;
import org.processmining.estminer.specpp.util.Reflection;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings({"unchecked", "rawtypes"})
public class EvaluatorConfiguration extends Configuration {

    private final ImmutableList<SimpleBuilder<ProvidesEvaluators>> evaluatorProviderBuilders;

    public EvaluatorConfiguration(ComponentSystemAdapter csa, ImmutableList<SimpleBuilder<ProvidesEvaluators>> evaluatorProviderBuilders) {
        super(csa);
        this.evaluatorProviderBuilders = evaluatorProviderBuilders;
    }

    public void createEvaluators() {
        for (SimpleBuilder<ProvidesEvaluators> evaluatorProviderBuilder : evaluatorProviderBuilders) {
            createFrom(evaluatorProviderBuilder);
        }
    }

    public static class Configurator implements ComponentInitializerBuilder<EvaluatorConfiguration> {
        private final List<SimpleBuilder<ProvidesEvaluators>> evaluatorProviderBuilders;

        public Configurator() {
            evaluatorProviderBuilders = new LinkedList<>();
        }


        public <I extends Evaluable, E extends Evaluation> Configurator evaluator(Class<I> evaluableClass, Class<E> evaluationClass, Class<Evaluator<? super I, ? extends E>> evaluatorClass) {
            SimpleBuilder<ProvidesEvaluators> builder = () -> {
                class Wrap extends AbstractComponentSystemUser implements ProvidesEvaluators {
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

        public EvaluatorConfiguration build(ComponentSystemAdapter cs) {
            return new EvaluatorConfiguration(cs, ImmutableList.copyOf(evaluatorProviderBuilders));
        }
    }

}
