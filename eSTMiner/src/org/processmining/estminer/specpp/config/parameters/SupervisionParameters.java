package org.processmining.estminer.specpp.config.parameters;

import org.processmining.estminer.specpp.base.impls.SPECpp;
import org.processmining.estminer.specpp.componenting.system.link.*;

import java.util.HashSet;
import java.util.Set;

public class SupervisionParameters implements Parameters {

    private final boolean useConsole;
    private final Set<Class<?>> classesToInstrument;

    public SupervisionParameters(boolean useConsole) {
        this.useConsole = useConsole;
        classesToInstrument = new HashSet<>();
    }


    public static SupervisionParameters getDefault() {
        SupervisionParameters p = new SupervisionParameters(true);
        p.classesToInstrument.add(ProposerComponent.class);
        p.classesToInstrument.add(ComposerComponent.class);
        p.classesToInstrument.add(CompositionComponent.class);
        p.classesToInstrument.add(PostProcessorComponent.class);
        p.classesToInstrument.add(ExpansionStrategyComponent.class);
        p.classesToInstrument.add(EfficientTreeComponent.class);
        p.classesToInstrument.add(ChildGenerationLogicComponent.class);
        p.classesToInstrument.add(SPECpp.class);
        return p;
    }

    public Set<Class<?>> getClassesToInstrument() {
        return classesToInstrument;
    }

    public boolean shouldBeInstrumented(Object o) {
        Class<?> oClass = o.getClass();
        for (Class<?> aClass : classesToInstrument) {
            if (aClass.isAssignableFrom(oClass)) {
                return true;
            }
        }
        return classesToInstrument.contains(oClass);
        //return classesToInstrument.stream().anyMatch(c -> c.isAssignableFrom(o.getClass()));
    }

    public boolean isUseConsole() {
        return useConsole;
    }

    @Override
    public String toString() {
        return "SupervisionParameters{" +
                "useConsole=" + useConsole +
                ", classesToInstrument=" + classesToInstrument +
                '}';
    }
}
