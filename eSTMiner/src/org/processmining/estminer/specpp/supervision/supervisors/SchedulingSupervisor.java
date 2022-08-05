package org.processmining.estminer.specpp.supervision.supervisors;

import org.processmining.estminer.specpp.supervision.AbstractSupervisor;
import org.processmining.estminer.specpp.supervision.BackgroundTaskRunner;
import org.processmining.estminer.specpp.supervision.RegularScheduler;
import org.processmining.estminer.specpp.supervision.piping.LayingPipe;
import org.processmining.estminer.specpp.traits.Joinable;

public abstract class SchedulingSupervisor extends AbstractSupervisor implements Joinable {

    private final BackgroundTaskRunner backgroundTaskRunner;
    private final RegularScheduler regularScheduler;

    public SchedulingSupervisor() {
        regularScheduler = RegularScheduler.inst();
        backgroundTaskRunner = BackgroundTaskRunner.inst();
    }

    @Override
    protected LayingPipe beginLaying() {
        return LayingPipe.inst(regularScheduler, backgroundTaskRunner);
    }

    protected RegularScheduler regularRunner() {
        return regularScheduler;
    }

    protected BackgroundTaskRunner constantRunner() {
        return backgroundTaskRunner;
    }


    @Override
    public void start() {
        backgroundTaskRunner.start();
        regularScheduler.start();
    }


    @Override
    public void stop() {
        backgroundTaskRunner.stop();
        regularScheduler.stop();
    }

    @Override
    public void join() {
        try {
            backgroundTaskRunner.join();
            regularScheduler.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
