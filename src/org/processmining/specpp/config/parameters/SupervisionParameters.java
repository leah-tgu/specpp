package org.processmining.specpp.config.parameters;

import org.processmining.specpp.base.impls.SPECpp;
import org.processmining.specpp.componenting.system.link.*;

import java.util.HashSet;
import java.util.Set;

public class SupervisionParameters implements Parameters {

    private final boolean useConsole;
    private final Set<Class<?>> classesToInstrument;

    public SupervisionParameters(boolean useConsole) {
        this.useConsole = useConsole;
        classesToInstrument = new HashSet<>();
    }

    public SupervisionParameters(boolean useConsole, Set<Class<?>> classesToInstrument) {
        this.useConsole = useConsole;
        this.classesToInstrument = classesToInstrument;
    }


    public static SupervisionParameters instrumentNone(boolean useConsole) {
        return new SupervisionParameters(useConsole);
    }

    public static SupervisionParameters instrumentAll(boolean useConsole) {
        HashSet<Class<?>> s = new HashSet<>();
        s.add(ProposerComponent.class);
        s.add(ComposerComponent.class);
        s.add(CompositionComponent.class);
        s.add(PostProcessorComponent.class);
        s.add(ExpansionStrategyComponent.class);
        s.add(EfficientTreeComponent.class);
        s.add(ChildGenerationLogicComponent.class);
        s.add(SPECpp.class);
        return new SupervisionParameters(true, s);
    }

    public static SupervisionParameters getDefault() {
        return instrumentAll(true);
    }

    public Set<Class<?>> getClassesToInstrument() {
        return classesToInstrument;
    }

    public boolean shouldObjBeInstrumented(Object o) {
        return shouldClassBeInstrumented(o.getClass());
    }

    public boolean shouldClassBeInstrumented(Class<?> oClass) {
        for (Class<?> aClass : classesToInstrument) {
            if (aClass.isAssignableFrom(oClass)) {
                return true;
            }
        }
        return classesToInstrument.contains(oClass);
    }


    public boolean isUseConsole() {
        return useConsole;
    }

    @Override
    public String toString() {
        return "SupervisionParameters{" + "useConsole=" + useConsole + ", classesToInstrument=" + classesToInstrument + '}';
    }
}
