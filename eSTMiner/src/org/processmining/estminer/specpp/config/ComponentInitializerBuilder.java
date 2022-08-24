package org.processmining.estminer.specpp.config;

import org.processmining.estminer.specpp.componenting.system.ComponentInitializer;
import org.processmining.estminer.specpp.componenting.system.GlobalComponentRepository;

public interface ComponentInitializerBuilder<T extends ComponentInitializer> extends InitializingBuilder<T, GlobalComponentRepository> {
}
