package org.processmining.estminer.specpp.supervision;

import org.processmining.estminer.specpp.componenting.system.link.AbstractBaseClass;
import org.processmining.estminer.specpp.supervision.piping.LayingPipe;

public abstract class AbstractSupervisor extends AbstractBaseClass implements Supervisor {

    protected LayingPipe beginLaying() {
        return LayingPipe.inst();
    }

}
