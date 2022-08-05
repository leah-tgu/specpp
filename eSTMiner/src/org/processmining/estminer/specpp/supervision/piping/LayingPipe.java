package org.processmining.estminer.specpp.supervision.piping;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.processmining.estminer.specpp.supervision.BackgroundTaskRunner;
import org.processmining.estminer.specpp.supervision.RegularScheduler;
import org.processmining.estminer.specpp.supervision.observations.Observation;
import org.processmining.estminer.specpp.supervision.traits.*;
import org.processmining.estminer.specpp.traits.Triggerable;
import org.processmining.estminer.specpp.util.datastructures.Tuple2;

import java.time.Duration;
import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Consumer;

public class LayingPipe {

    private Observable<?> source;
    private Object lastAddition;

    private RegularScheduler regularScheduler;
    private BackgroundTaskRunner backgroundTaskRunner;
    private final LinkedList<Observable<?>> observables;
    private final MultiValuedMap<Integer, Observer<?>> observers;
    private final Deque<Tuple2<Runnable, Duration>> prospectiveScheduledRunnables;
    private final Deque<Runnable> prospectiveSubTaskedRunnables;

    private LayingPipe() {
        observables = new LinkedList<>();
        observers = new ArrayListValuedHashMap<>();
        prospectiveScheduledRunnables = new LinkedList<>();
        prospectiveSubTaskedRunnables = new LinkedList<>();
    }


    public static LayingPipe inst() {
        return new LayingPipe();
    }

    public static LayingPipe inst(RegularScheduler regularScheduler, BackgroundTaskRunner backgroundTaskRunner) {
        return inst().setRegularRunner(regularScheduler).setConstantRunner(backgroundTaskRunner);
    }

    public static void link(Observable<?> source, Observer<?> sink) {
        inst().source(source).sink(sink).apply();
    }

    public LayingPipe setRegularRunner(RegularScheduler regularScheduler) {
        this.regularScheduler = regularScheduler;
        return this;
    }

    public LayingPipe setConstantRunner(BackgroundTaskRunner backgroundTaskRunner) {
        this.backgroundTaskRunner = backgroundTaskRunner;
        return this;
    }

    public LayingPipe source(Observable<?> source) {
        if (!observables.isEmpty() || !observers.isEmpty()) throw new DisorderedPipeLaying();
        this.source = source;
        appendObservable(source);
        return this;
    }


    public LayingPipe pipe(ObservationPipe<?, ?> pipe) {
        appendPipe(pipe);
        return this;
    }


    public LayingPipe split(Consumer<LayingPipe> splitPath) {
        LayingPipe lp = LayingPipe.inst(regularScheduler, backgroundTaskRunner);
        Observable<?> observable = lastObservable();
        if (observable == null) throw new DisorderedPipeLaying();
        lp.source(observable);
        splitPath.accept(lp);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <O extends Observation> LayingPipe export(Consumer<Observable<O>> consumer) {
        Observable<?> observable = lastObservable();
        if (observable == null) throw new DisorderedPipeLaying();
        consumer.accept((Observable<O>) observable);
        return this;
    }

    private Observable<?> lastObservable() {
        return observables.peekLast();
    }

    private void appendObservable(Observable<?> source) {
        observables.add(source);
        lastAddition = source;
    }

    private void appendPipe(ObservationPipe<?, ?> pipe) {
        ensureFittingDimensionality(lastObservable(), pipe);
        appendObserver(pipe);
        appendObservable(pipe);
    }

    private void appendObserver(Observer<?> observer) {
        ensureFittingDimensionality(lastObservable(), observer);
        if (observables.isEmpty()) throw new DisorderedPipeLaying();
        int i = observables.size() - 1;
        observers.put(i, observer);
        lastAddition = observer;
    }

    private static void ensureFittingDimensionality(Object producer, Object consumer) {
        if (producer == null || consumer == null) return;
        if ((producer instanceof ToOne && consumer instanceof FromMany && !(consumer instanceof FromOne)))
            throw new IncompatiblePipeLaying(IncompatiblePipeLaying.WrongRelationship.ToOneIntoFromMany, producer, consumer);
        else if (producer instanceof ToMany && consumer instanceof FromOne && !(consumer instanceof FromMany))
            throw new IncompatiblePipeLaying(IncompatiblePipeLaying.WrongRelationship.ToManyIntoFromOne, producer, consumer);
    }

    public LayingPipe sink(Observer<?> sink) {
        ensureFittingDimensionality(lastObservable(), sink);
        appendObserver(sink);
        return this;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void connectAllObservers(int index) {
        if (index >= observables.size()) throw new DisorderedPipeLaying();
        Observable<?> obs = observables.get(index);
        if (observers.containsKey(index)) observers.get(index).forEach(s -> obs.addObserver((Observer) s));
    }

    public void apply() throws IncompletePipeLaying {
        for (int i = 0; i < observables.size(); i++) {
            connectAllObservers(i);
        }

        if (regularScheduler != null && !prospectiveScheduledRunnables.isEmpty()) {
            for (Tuple2<Runnable, Duration> tuple2 : prospectiveScheduledRunnables) {
                regularScheduler.schedule(tuple2.getT1(), tuple2.getT2());
            }
        } else if (!prospectiveScheduledRunnables.isEmpty())
            throw new IncompletePipeLaying("Missing Regular Scheduler");

        if (backgroundTaskRunner != null && !prospectiveSubTaskedRunnables.isEmpty()) {
            for (Runnable r : prospectiveSubTaskedRunnables) {
                backgroundTaskRunner.register(r);
            }
        } else if (!prospectiveSubTaskedRunnables.isEmpty()) throw new IncompletePipeLaying("Missing SubTasker");
    }

    public LayingPipe schedule(Duration interval) {
        if (lastAddition instanceof Triggerable) {
            Triggerable tr = ((Triggerable) lastAddition);
            prospectiveScheduledRunnables.add(new Tuple2<>(tr::trigger, interval));
        }
        return this;
    }

    public LayingPipe giveBackgroundThread() {
        if (lastAddition instanceof RequiresSupportingTask) {
            RequiresSupportingTask rst = (RequiresSupportingTask) lastAddition;
            prospectiveSubTaskedRunnables.add(rst.getSupportingTask());
        }
        return this;
    }

    public static class IncompletePipeLaying extends RuntimeException {

        public IncompletePipeLaying() {
        }

        public IncompletePipeLaying(String message) {
            super(message);
        }
    }


    public static class DisorderedPipeLaying extends RuntimeException {
    }

    public static class IncompatiblePipeLaying extends RuntimeException {

        public enum WrongRelationship {
            ToOneIntoFromMany, ToManyIntoFromOne
        }

        public IncompatiblePipeLaying(WrongRelationship mistake, Object producer, Object consumer) {
            super(mistake.toString() + " caused by " + producer.getClass()
                                                               .getSimpleName() + " connecting into " + consumer.getClass()
                                                                                                                .getSimpleName());
        }

    }

}
