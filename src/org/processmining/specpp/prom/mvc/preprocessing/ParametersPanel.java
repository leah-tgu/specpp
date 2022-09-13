package org.processmining.specpp.prom.mvc.preprocessing;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import com.google.common.collect.ImmutableList;
import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.specpp.orchestra.PreProcessingParameters;
import org.processmining.specpp.preprocessing.orderings.ActivityOrderingBuilder;
import org.processmining.specpp.prom.mvc.swing.LabeledComboBox;
import org.processmining.specpp.prom.mvc.swing.SwingFactory;
import org.processmining.specpp.supervision.observations.ClassKey;

import javax.swing.*;
import java.awt.*;
import java.util.List;

@SuppressWarnings("unchecked")
public class ParametersPanel extends JPanel {

    private final PreProcessingController controller;
    private final JComboBox<XEventClassifier> classifierComboBox;
    private final JComboBox<ClassKey<? extends ActivityOrderingBuilder>> orderingComboBox;
    private final JCheckBox artificialTransitionsCheckBox;
    private final List<Class<? extends ActivityOrderingBuilder>> availableOrderings;
    private final ImmutableList<XEventClassifier> availableEventClassifiers;
    private final JButton previewButton;

    public ParametersPanel(PreProcessingController controller, List<XEventClassifier> eventClassifiers) {
        super(new GridBagLayout());
        this.controller = controller;
        PreProcessingParameters defaultParameters = PreProcessingParameters.getDefault();

        availableEventClassifiers = ImmutableList.copyOf(eventClassifiers);
        LabeledComboBox<XEventClassifier> eventClassifierBox = SwingFactory.labeledComboBox("Event Classifier", availableEventClassifiers.toArray(new XEventClassifier[0]));
        classifierComboBox = eventClassifierBox.getComboBox();
        classifierComboBox.setMinimumSize(new Dimension(175, 25));
        classifierComboBox.setSelectedItem(defaultParameters.getEventClassifier());

        availableOrderings = PreProcessingParameters.getAvailableTransitionEncodingsBuilders();
        ClassKey<? extends ActivityOrderingBuilder>[] tebOptions = availableOrderings.stream()
                                                                                     .map(ClassKey::new)
                                                                                     .toArray(ClassKey[]::new);
        ClassKey<? extends ActivityOrderingBuilder> selected = new ClassKey<>(defaultParameters.getTransitionEncodingsBuilderClass());


        LabeledComboBox<ClassKey<? extends ActivityOrderingBuilder>> orderingStrategyBox = SwingFactory.labeledComboBox("Ordering Strategy", tebOptions);
        orderingStrategyBox.setMinimumSize(new Dimension(250, 25));
        orderingComboBox = orderingStrategyBox.getComboBox();
        orderingComboBox.setMinimumSize(new Dimension(250, 25));
        orderingComboBox.setSelectedItem(selected);

        artificialTransitionsCheckBox = SlickerFactory.instance()
                                                      .createCheckBox("introduce artificial start & end transitions", defaultParameters.isAddStartEndTransitions());


        previewButton = SlickerFactory.instance().createButton("preview");
        previewButton.addActionListener(e -> {
            controller.preview(collectParameters());
        });

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0.2;
        add(eventClassifierBox, c);
        c.gridy++;
        add(orderingStrategyBox, c);
        c.gridy++;
        add(artificialTransitionsCheckBox, c);
        c.gridy++;
        c.weighty = 0.2;
        add(previewButton, c);


        tryInstantiatingFromLastOrLoaded();
    }

    private void tryInstantiatingFromLastOrLoaded() {
        PreProcessingParameters preProcessingParameters = controller.getParentController().getPreProcessingParameters();
        if (preProcessingParameters != null) {
            instantiateFrom(preProcessingParameters);
            return;
        }
        PreProcessingParameters loadedPreProcessingParameters = controller.getParentController()
                                                                          .getLoadedPreProcessingParameters();
        if (loadedPreProcessingParameters != null) {
            instantiateFrom(loadedPreProcessingParameters);
        }
    }

    private void instantiateFrom(PreProcessingParameters preProcessingParameters) {
        XEventClassifier eventClassifier = preProcessingParameters.getEventClassifier();
        if (availableEventClassifiers.contains(eventClassifier))
            classifierComboBox.setSelectedItem(eventClassifier);
        Class<? extends ActivityOrderingBuilder> teb = preProcessingParameters.getTransitionEncodingsBuilderClass();
        if (availableOrderings.contains(teb))
            orderingComboBox.setSelectedItem(teb);
        artificialTransitionsCheckBox.setSelected(preProcessingParameters.isAddStartEndTransitions());
    }

    public PreProcessingParameters collectParameters() {
        XEventClassifier eventClassifier = availableEventClassifiers.get(classifierComboBox.getSelectedIndex());
        Class<? extends ActivityOrderingBuilder> orderingStrategy = availableOrderings.get(orderingComboBox.getSelectedIndex());
        boolean introduceArtificialTransitions = artificialTransitionsCheckBox.isSelected();
        return new PreProcessingParameters(eventClassifier, introduceArtificialTransitions, orderingStrategy);
    }

    public void disableButton() {
        SwingUtilities.invokeLater(() -> previewButton.setEnabled(false));
    }

    public void enableButton() {
        SwingUtilities.invokeLater(() -> previewButton.setEnabled(true));
    }
}
