package org.processmining.estminer.specpp.config;

import com.google.common.collect.ImmutableList;
import org.processmining.estminer.specpp.componenting.system.ComponentCollection;
import org.processmining.estminer.specpp.supervision.Supervisor;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SupervisionConfiguration extends Configuration {

    private final List<SimpleBuilder<Supervisor>> supervisorBuilders;

    public SupervisionConfiguration(ComponentCollection csa, List<SimpleBuilder<Supervisor>> supervisorBuilders) {
        super(csa);
        this.supervisorBuilders = supervisorBuilders;
    }

    public List<Supervisor> createSupervisors() {
        return supervisorBuilders.stream().map(this::createFrom).collect(Collectors.toList());
    }


    public static class Configurator implements ComponentInitializerBuilder<SupervisionConfiguration> {

        private final List<SimpleBuilder<Supervisor>> supervisorBuilders;

        public Configurator() {
            supervisorBuilders = new LinkedList<>();
        }

        public Configurator supervisor(SimpleBuilder<Supervisor> builder) {
            supervisorBuilders.add(builder);
            return this;
        }

        @Override
        public SupervisionConfiguration build(ComponentCollection csa) {
            return new SupervisionConfiguration(csa, ImmutableList.copyOf(supervisorBuilders));
        }
    }

}
