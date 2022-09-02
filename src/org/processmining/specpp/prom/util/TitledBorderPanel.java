package org.processmining.specpp.prom.util;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class TitledBorderPanel extends JPanel {

    private GridBagConstraints c;

    public TitledBorderPanel(String title, LayoutManager layout) {
        super(layout);
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), title));
    }

    public TitledBorderPanel(String title) {
        super(new GridBagLayout());
        c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 0;
        c.gridy = 0;
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), title, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, getFont().deriveFont(18f)));
    }

    public void append(JComponent component) {
        add(component, c);
        c.gridy++;
    }

    public void completeWithWhitespace() {
        add(Box.createVerticalGlue(), c);
    }

}
