package org.processmining.specpp.prom.util;

import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.util.function.Function;

public class TextBasedInputField<T> extends LabeledTextField {
    private final Function<String, T> parseInput;
    private final Color ogBackground;
    private final InputVerifier iv;
    public static final Color HIGHLIGHT_COLOR = new Color(ColorSpace.getInstance(ColorSpace.CS_sRGB), Color.red.getColorComponents(new float[3]), 0.5f);

    public TextBasedInputField(String label, Function<String, T> parseInput) {
        super(label);
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

        showVerificationStatus();
    }

    public void showVerificationStatus() {
        field.setBackground(iv.verify(field) ? ogBackground : HIGHLIGHT_COLOR);
    }

    protected T tryParse() {
        try {
            return parseInput.apply(field.getText());
        } catch (Exception ignored) {
            return null;
        }
    }

    protected boolean permittedToBeWrong() {
        return !isVisible() || !isEnabled();
    }

    public T getInput() {
        showVerificationStatus();
        return tryParse();
    }

    public void setText(String text) {
        field.setText(text);
        showVerificationStatus();
    }
}
