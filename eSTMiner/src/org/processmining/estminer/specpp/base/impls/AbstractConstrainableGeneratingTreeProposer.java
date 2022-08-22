package org.processmining.estminer.specpp.base.impls;

import org.processmining.estminer.specpp.base.Candidate;
import org.processmining.estminer.specpp.base.ConstrainableProposer;
import org.processmining.estminer.specpp.base.Constrainer;
import org.processmining.estminer.specpp.componenting.delegators.AbstractLocalComponentSystemAwareDelegator;
import org.processmining.estminer.specpp.componenting.system.ComponentCollection;
import org.processmining.estminer.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.estminer.specpp.componenting.system.LocalComponentRepository;
import org.processmining.estminer.specpp.componenting.traits.UsesGlobalComponentSystem;
import org.processmining.estminer.specpp.componenting.traits.UsesLocalComponentSystem;
import org.processmining.estminer.specpp.datastructures.tree.base.ConstrainableLocalNodeGenerator;
import org.processmining.estminer.specpp.datastructures.tree.base.GenerationConstraint;
import org.processmining.estminer.specpp.datastructures.tree.base.NodeProperties;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.GeneratingLocalNode;
import org.processmining.estminer.specpp.supervision.EventSupervision;
import org.processmining.estminer.specpp.supervision.piping.Observable;
import org.processmining.estminer.specpp.supervision.piping.PipeWorks;
import org.processmining.estminer.specpp.traits.Initializable;

public abstract class AbstractConstrainableGeneratingTreeProposer<C extends Candidate & NodeProperties, N extends GeneratingLocalNode<C, ?, N>, L extends CandidateConstraint<C>, K extends GenerationConstraint> extends AbstractLocalComponentSystemAwareDelegator<GeneratingTreeProposer<C, N, ? extends ConstrainableLocalNodeGenerator<C, ?, N, K>>> implements ConstrainableProposer<C, L>, Constrainer<K>, UsesGlobalComponentSystem, UsesLocalComponentSystem, Initializable {
    protected final GlobalComponentRepository gcr = new GlobalComponentRepository();

    protected final EventSupervision<K> constraintOutput = PipeWorks.eventSupervision();

    public AbstractConstrainableGeneratingTreeProposer(GeneratingTreeProposer<C, N, ? extends ConstrainableLocalNodeGenerator<C, ?, N, K>> delegate) {
        super(delegate);
        ConstrainableLocalNodeGenerator<C, ?, ?, K> generator = delegate.getGenerator();
        //constraintOutput.addObserver(generator);
    }


    @Override
    public Observable<K> getConstraintPublisher() {
        return constraintOutput;
    }

    @Override
    public abstract void acceptConstraint(L constraint);

    public C proposeCandidate() {
        return delegate.proposeCandidate();
    }

    @Override
    public ComponentCollection componentSystemAdapter() {
        return gcr;
    }

    @Override
    public ComponentCollection getComponentCollection() {
        return gcr;
    }
}
