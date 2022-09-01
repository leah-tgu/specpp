package org.processmining.specpp.prom.util;

import javax.swing.*;
import java.awt.*;

public class TitledBorderPanel extends JPanel {

    public TitledBorderPanel(String title) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), title));
    }

}
