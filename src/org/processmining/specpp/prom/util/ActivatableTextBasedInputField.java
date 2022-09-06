package org.processmining.specpp.prom.util;

import javax.swing.*;
import java.awt.*;
import java.util.function.Function;

import static org.processmining.specpp.prom.util.TextBasedInputField.HIGHLIGHT_COLOR;

public class ActivatableTextBasedInputField<T> extends LabeledCheckboxedTextField {
    private final Function<String, T> parseInput;
    private final Color ogBackground;
    private final InputVerifier iv;
    private boolean isActivated;

    public ActivatableTextBasedInputField(String label, boolean activatedByDefault, Function<String, T> parseInput) {
        super(label, activatedByDefault);
        this.parseInput = parseInput;

        ogBackground = field.getBackground();

        iv = new InputVerifier() {
            @Override
            public boolean shouldYieldFocus(JComponent input) {
                showVerificationStatus();
                return true;
            }

            @Override
            public boolean verify(JComponent input) {
                if (permittedToBeWrong()) return true;
                T t = tryParse();
                return t != null;
            }
        };

        field.setInputVerifier(iv);

        checkBox.addChangeListener(c -> setInternalActivationStatus(checkBox.isSelected()));
        if (isActivated) showVerificationStatus();
    }

    public void showVerificationStatus() {
        field.setBackground(iv.verify(field) ? ogBackground : HIGHLIGHT_COLOR);
    }

    private boolean permittedToBeWrong() {
        return !isActivated;
    }

    protected T tryParse() {
        try {
            return parseInput.apply(field.getText());
        } catch (Exception ignored) {
            return null;
        }
    }

    public T getInput() {
        showVerificationStatus();
        return tryParse();
    }

    public void setText(String text) {
        field.setText(text);
        showVerificationStatus();
    }

    private void setInternalActivationStatus(boolean newState) {
        isActivated = newState;
        showVerificationStatus();
    }

    public void activate() {
        checkBox.setSelected(true);
    }

    public void deactivate() {
        checkBox.setSelected(false);
    }

}
