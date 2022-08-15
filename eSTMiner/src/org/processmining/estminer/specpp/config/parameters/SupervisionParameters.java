package org.processmining.estminer.specpp.config.parameters;

public class SupervisionParameters implements Parameters {

    private final boolean useConsole;

    public SupervisionParameters(boolean useConsole) {
        this.useConsole = useConsole;
    }


    public static SupervisionParameters getDefault() {
        return new SupervisionParameters(true);
    }

    public boolean isUseConsole() {
        return useConsole;
    }

    @Override
    public String toString() {
        return "SupervisionParameters{" +
                "useConsole=" + useConsole +
                '}';
    }
}
