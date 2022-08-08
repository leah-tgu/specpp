package org.processmining.estminer.specpp.base.impls;

import org.processmining.estminer.specpp.base.Candidate;
import org.processmining.estminer.specpp.base.ConstrainingComposer;
import org.processmining.estminer.specpp.base.MutableCappedComposition;
import org.processmining.estminer.specpp.base.Result;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.componenting.system.ComponentSystemAdapter;
import org.processmining.estminer.specpp.componenting.traits.UsesComponentSystem;
import org.processmining.estminer.specpp.supervision.EventSupervision;
import org.processmining.estminer.specpp.supervision.piping.Observable;
import org.processmining.estminer.specpp.supervision.piping.PipeWorks;
import org.processmining.estminer.specpp.util.JavaTypingUtils;

import java.util.function.Function;

/**
 * The abstract base class of a {@code ConstrainingComposer} for candidates of type {@code C}.
 * It internally employs a candidate collection of type {@code I} which serves as its growing intermediate result.
 * The final result of type {@code R} can be computed on demand.
 * <p>
 * This class participates in the componenting system to provide {@code CandidateConstraint} events.
 *
 * @param <C>
 * @param <I>
 * @param <R>
 * @see ConstrainingComposer
 * @see CandidateConstraint
 */
public abstract class AbstractConstrainingComposer<C extends Candidate, I extends MutableCappedComposition<C>, R extends Result> extends AbstractComposer<C, I, R> implements ConstrainingComposer<C, I, R, CandidateConstraint<C>>, UsesComponentSystem {

    protected final ComponentSystemAdapter componentSystemAdapter = new ComponentSystemAdapter();

    protected final EventSupervision<CandidateConstraint<C>> evs;

    public AbstractConstrainingComposer(I composition, Function<? super I, R> assembleResult) {
        super(composition, assembleResult);
        evs = PipeWorks.eventSupervision();
        componentSystemAdapter().provide(SupervisionRequirements.observable("composer.constraints", JavaTypingUtils.castClass(CandidateConstraint.class), evs));
    }

    protected void publishConstraint(CandidateConstraint<C> constraint) {
        evs.publish(constraint);
    }

    @Override
    public Observable<CandidateConstraint<C>> getConstraintPublisher() {
        return evs;
    }

    @Override
    public ComponentSystemAdapter componentSystemAdapter() {
        return componentSystemAdapter;
    }

}
