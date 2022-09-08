package org.processmining.specpp.prom.mvc.swing;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.function.Function;

public class ActivatableTextBasedInputField<T> extends LabeledCheckboxedTextField {
    private final Function<String, T> parseInput;
    private final InputVerifier iv;
    private final Border ogBorder;
    private boolean isActivated;

    public ActivatableTextBasedInputField(String label, Function<String, T> parseInput, boolean activatedByDefault, int inputTextColumns) {
        super(label, activatedByDefault, inputTextColumns);
        this.parseInput = parseInput;

        ogBorder =
                field.getBorder();

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
        HashSet<AWTKeyStroke> awtKeyStrokes = new HashSet<>(KeyboardFocusManager.getCurrentKeyboardFocusManager()
                                                                                .getDefaultFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        awtKeyStrokes.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_ENTER, 0));
        field.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, awtKeyStrokes);

        checkBox.addChangeListener(c -> setInternalActivationStatus(checkBox.isSelected()));
        if (isActivated) showVerificationStatus();
    }

    public void showVerificationStatus() {
        field.setBorder(iv.verify(field) ? ogBorder : BorderFactory.createLineBorder(Color.red, 2, false));
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
        SwingUtilities.invokeLater(this::showVerificationStatus);
        return tryParse();
    }

    public void setText(String text) {
        SwingUtilities.invokeLater(() -> {
            field.setText(text);
            showVerificationStatus();
        });
    }

    private void setInternalActivationStatus(boolean newState) {
        isActivated = newState;
        showVerificationStatus();
    }

    public void activate() {
        SwingUtilities.invokeLater(() -> checkBox.setSelected(true));
    }

    public void deactivate() {
        SwingUtilities.invokeLater(() -> checkBox.setSelected(false));
    }

}
