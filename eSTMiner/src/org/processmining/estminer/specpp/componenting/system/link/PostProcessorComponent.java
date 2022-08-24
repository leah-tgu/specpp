package org.processmining.estminer.specpp.componenting.system.link;

import org.processmining.estminer.specpp.base.PostProcessor;
import org.processmining.estminer.specpp.base.Result;
import org.processmining.estminer.specpp.componenting.system.FullComponentSystemUser;

public interface PostProcessorComponent<S extends Result, T extends Result> extends PostProcessor<S, T>, FullComponentSystemUser {
}
