package org.processmining.estminer.specpp.config;

import org.processmining.estminer.specpp.componenting.system.ComponentCollection;
import org.processmining.estminer.specpp.componenting.system.ComponentInitializer;

public interface ComponentInitializerBuilder<T extends ComponentInitializer> extends InitializingBuilder<T, ComponentCollection> {
}
