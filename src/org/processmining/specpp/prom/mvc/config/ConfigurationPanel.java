package org.processmining.specpp.prom.mvc.config;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import com.google.common.collect.ImmutableList;
import org.processmining.specpp.componenting.data.DataSource;
import org.processmining.specpp.config.parameters.OutputPathParameters;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.prom.alg.FrameworkBridge;
import org.processmining.specpp.prom.mvc.AbstractStagePanel;
import org.processmining.specpp.prom.mvc.swing.*;
import org.processmining.specpp.util.PathTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.ListIterator;
import java.util.function.Function;
import java.util.function.Predicate;

public class ConfigurationPanel extends AbstractStagePanel<ConfigurationController> {


    private final JComboBox<Preset> presetComboBox;
    private final JComboBox<SupervisionSetting> supervisionComboBox;
    private final JCheckBox trackCandidateTreeCheckBox;
    private final JComboBox<TreeExpansionSetting> expansionStrategyComboBox;
    private final JComboBox<FrameworkBridge.BridgedHeuristics> heuristicComboBox;
    private final JCheckBox respectWiringCheckBox;
    private final JCheckBox supportRestartCheckBox;
    private final JCheckBox concurrentReplayCheckBox;
    private final JCheckBox restrictToFittingSubLogCheckBox;
    private final JComboBox<FrameworkBridge.BridgedDeltaAdaptationFunctions> deltaAdaptationFunctionComboBox;
    private final JComboBox<CompositionStrategy> compositionStrategyComboBox;
    private final JCheckBox applyCIPRCheckBox;
    private final MyListModel<FrameworkBridge.AnnotatedPostProcessor> ppPipelineModel;
    private final JTextField tauField;
    private final JTextField deltaField;
    private final JTextField steepnessField;
    private final JTextField depthField;
    private final LabeledComboBox<FrameworkBridge.BridgedHeuristics> bridgedHeuristicsLabeledComboBox;
    private final LabeledComboBox<FrameworkBridge.BridgedDeltaAdaptationFunctions> deltaAdaptationLabeledComboBox;
    private static final Predicate<JTextField> zeroOneDoublePredicate = input -> {
        try {
            double v = Double.parseDouble(input.getText());
            return 0.0 <= v && v <= 1.0;
        } catch (NumberFormatException e) {
            return false;
        }
    };

    private static final Function<String, Double> zeroOneDoubleFunc = s -> {
        double v = Double.parseDouble(s);
        return (0.0 <= v && v <= 1.0) ? v : null;
    };
    private static final Function<String, Integer> posIntFunc = s -> {
        int v = Integer.parseInt(s);
        return (v > 0) ? v : null;
    };
    private static final Function<String, Duration> durationFunc = Duration::parse;

    private static final Predicate<JTextField> posIntPredicate = input -> {
        if (!input.isEnabled()) return true;
        try {
            double v = Integer.parseInt(input.getText());
            return 0 < v;
        } catch (NumberFormatException e) {
            return false;
        }
    };
    private static final Predicate<JTextField> durationStringPredicate = input -> {
        if (!input.isEnabled()) return true;
        try {
            Duration.parse(input.getText());
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    };
    private final TextBasedInputField<Double> tauInput;
    private final TextBasedInputField<Double> deltaInput;
    private final TextBasedInputField<Integer> steepnessInput;
    private final ActivatableTextBasedInputField<Integer> depthInput;
    private final ActivatableTextBasedInputField<Duration> discoveryTimeLimitInput;
    private final ActivatableTextBasedInputField<Duration> totalTimeLimitInput;
    private final JButton runButton;
    private final JCheckBox permitSubtreeCutoffCheckBox;
    private final JCheckBox logToFileCheckBox;

    public ConfigurationPanel(ConfigurationController controller) {
        super(controller, new GridBagLayout());

        // ** SUPERVISION ** //

        TitledBorderPanel supervision = new TitledBorderPanel("Preset & Supervision");
        LabeledComboBox<Preset> presetLabeledComboBox = SwingFactory.labeledComboBox("Configuration Preset", Preset.values());
        presetComboBox = presetLabeledComboBox.getComboBox();
        presetComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) updatedPreset();
        });
        supervision.append(presetLabeledComboBox);
        LabeledComboBox<SupervisionSetting> supervisionSettingLabeledComboBox = SwingFactory.labeledComboBox("Level of Detail of Supervision", SupervisionSetting.values());
        supervisionComboBox = supervisionSettingLabeledComboBox.getComboBox();
        supervisionComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) updatedSupervisionSettings();
        });
        supervision.append(supervisionSettingLabeledComboBox);
        logToFileCheckBox = SwingFactory.labeledCheckBox("log to file");

        String s = OutputPathParameters.getDefault().getFilePath(PathTools.OutputFileType.MAIN_LOG, "main");
        logToFileCheckBox.setToolTipText(String.format("Whether to setup a file logger to \"%s\"", s));
        logToFileCheckBox.addChangeListener(e -> updatedSupervisionSettings());
        supervision.append(logToFileCheckBox);
        trackCandidateTreeCheckBox = SwingFactory.labeledCheckBox("track candidate tree");
        trackCandidateTreeCheckBox.addChangeListener(e -> updatedSupervisionSettings());
        //supervision.append(trackCandidateTreeCheckBox);
        supervision.completeWithWhitespace();

        // ** PROPOSAL ** //

        TitledBorderPanel proposal = new TitledBorderPanel("Proposal");
        LabeledComboBox<TreeExpansionSetting> candidate_enumeration = SwingFactory.labeledComboBox("Candidate Enumeration", TreeExpansionSetting.values());
        expansionStrategyComboBox = candidate_enumeration.getComboBox();
        proposal.append(candidate_enumeration);
        bridgedHeuristicsLabeledComboBox = SwingFactory.labeledComboBox("Heuristic", FrameworkBridge.HEURISTICS.toArray(new FrameworkBridge.BridgedHeuristics[0]));
        heuristicComboBox = bridgedHeuristicsLabeledComboBox.getComboBox();
        heuristicComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) updatedProposalSettings();
        });
        bridgedHeuristicsLabeledComboBox.setVisible(false);
        proposal.append(bridgedHeuristicsLabeledComboBox);
        permitSubtreeCutoffCheckBox = SwingFactory.labeledCheckBox("Permit over/underfed subtree cutoff");
        permitSubtreeCutoffCheckBox.addChangeListener(e -> updatedProposalSettings());
        // proposal.append(permitSubtreeCutoffCheckBox)
        respectWiringCheckBox = SwingFactory.labeledCheckBox("respect wiring constraints");
        respectWiringCheckBox.addChangeListener(e -> updatedProposalSettings());
        proposal.append(respectWiringCheckBox);
        supportRestartCheckBox = SwingFactory.labeledCheckBox("use restartable implementation");
        supportRestartCheckBox.addChangeListener(e -> updatedProposalSettings());
        proposal.append(supportRestartCheckBox);
        proposal.completeWithWhitespace();

        // ** EVALUATION ** //

        TitledBorderPanel evaluation = new TitledBorderPanel("Evaluation");
        concurrentReplayCheckBox = SwingFactory.labeledCheckBox("use concurrent replay implementation");
        concurrentReplayCheckBox.addChangeListener(e -> updatedEvaluationSettings());
        evaluation.append(concurrentReplayCheckBox);

        restrictToFittingSubLogCheckBox = SwingFactory.labeledCheckBox("restrict replay to fitting sub log for implicit place removal");
        restrictToFittingSubLogCheckBox.addChangeListener(e -> updatedEvaluationSettings());
        evaluation.append(restrictToFittingSubLogCheckBox);
        deltaAdaptationLabeledComboBox = SwingFactory.labeledComboBox("Delta Adaptation Function", FrameworkBridge.DELTA_FUNCTIONS.toArray(new FrameworkBridge.BridgedDeltaAdaptationFunctions[0]));
        deltaAdaptationFunctionComboBox = deltaAdaptationLabeledComboBox.getComboBox();
        deltaAdaptationFunctionComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) updatedEvaluationSettings();
        });
        deltaAdaptationLabeledComboBox.setVisible(false);
        evaluation.append(deltaAdaptationLabeledComboBox);
        evaluation.completeWithWhitespace();

        // ** COMPOSITION ** //

        TitledBorderPanel composition = new TitledBorderPanel("Composition");
        LabeledComboBox<CompositionStrategy> compositionStrategyLabeledComboBox = SwingFactory.labeledComboBox("Variant", CompositionStrategy.values());
        compositionStrategyComboBox = compositionStrategyLabeledComboBox.getComboBox();
        compositionStrategyComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) updatedCompositionSettings();
        });
        composition.append(compositionStrategyLabeledComboBox);
        applyCIPRCheckBox = SwingFactory.labeledCheckBox("apply replay-based concurrent implicit place removal");
        applyCIPRCheckBox.addChangeListener(e -> updatedCompositionSettings());
        composition.append(applyCIPRCheckBox);
        composition.completeWithWhitespace();

        // ** POST PROCESSING ** //

        TitledBorderPanel postProcessing = new TitledBorderPanel("Post Processing", new BorderLayout());
        ppPipelineModel = new MyListModel<>();
        postProcessing.add(new PostProcessingConfigPanel(controller.getContext(), ppPipelineModel));
        // ** PARAMETERS ** //

        FocusAdapter fa = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                updatedParameters();
            }
        };
        TitledBorderPanel parameters = new TitledBorderPanel("Parameters");
        parameters.setFocusable(true); // focus is still not lost on click outside
        tauInput = SwingFactory.textBasedInputField("tau", zeroOneDoubleFunc, 10);
        tauField = tauInput.getTextField();
        tauField.addFocusListener(fa);
        parameters.append(tauInput);
        deltaInput = SwingFactory.textBasedInputField("delta", zeroOneDoubleFunc, 10);
        deltaField = deltaInput.getTextField();
        deltaField.addFocusListener(fa);
        deltaInput.setVisible(false);
        parameters.append(deltaInput);
        steepnessInput = SwingFactory.textBasedInputField("steepness", posIntFunc, 10);
        steepnessField = steepnessInput.getTextField();
        deltaField.addFocusListener(fa);
        steepnessInput.setVisible(false);
        parameters.append(steepnessInput);
        depthInput = SwingFactory.activatableTextBasedInputField("max depth", false, posIntFunc, 10);
        depthField = depthInput.getTextField();
        depthField.addFocusListener(fa);
        parameters.append(depthInput);
        parameters.completeWithWhitespace();

        // ** EXECUTION ** //

        TitledBorderPanel execution = new TitledBorderPanel("Execution");
        execution.setFocusable(true);
        discoveryTimeLimitInput = SwingFactory.activatableTextBasedInputField("discovery time limit", false, durationFunc, 25);
        discoveryTimeLimitInput.getTextField()
                               .setToolTipText("<html>ISO-8601 format: P<it>x</it>DT<it>x</it>H<it>x</it>M<it>x</it>.<it>x</it>S</html>");
        discoveryTimeLimitInput.getTextField().addFocusListener(fa);
        execution.append(discoveryTimeLimitInput);
        totalTimeLimitInput = SwingFactory.activatableTextBasedInputField("total time limit", false, durationFunc, 25);
        totalTimeLimitInput.getTextField()
                           .setToolTipText("<html>ISO-8601 format: P<it>x</it>DT<it>x</it>H<it>x</it>M<it>x</it>.<it>x</it>S</html>");
        totalTimeLimitInput.getTextField().addFocusListener(fa);
        execution.append(totalTimeLimitInput);

        runButton = SlickerFactory.instance().createButton("run");
        runButton.addActionListener(e -> tryRun());
        execution.append(runButton);
        execution.completeWithWhitespace();

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridy = 0;
        c.gridx = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        add(supervision, c);
        c.gridy++;
        add(proposal, c);
        c.gridy++;
        add(evaluation, c);
        c.gridy++;
        add(composition, c);
        c.gridy = 0;
        c.gridx = 1;
        c.gridheight = 2;
        add(postProcessing, c);
        c.gridheight = 1;
        c.gridy += 2;
        add(parameters, c);
        c.gridy++;
        add(execution, c);

        if (controller.getParentController().getProMConfig() != null) presetComboBox.setSelectedItem(Preset.Last);
        else if (controller.getParentController().getLoadedProMConfig() != null)
            presetComboBox.setSelectedItem(Preset.Loaded);
        else {
            presetComboBox.setSelectedItem(Preset.Default);
            updatedPreset();
        }

    }

    public static boolean validatePostProcessingPipeline(MyListModel<FrameworkBridge.AnnotatedPostProcessor> ppPipelineModel) {
        ListIterator<FrameworkBridge.AnnotatedPostProcessor> it = ppPipelineModel.iterator();
        if (!it.hasNext()) return false;
        FrameworkBridge.AnnotatedPostProcessor prev = FrameworkBridge.BridgedPostProcessors.Identity.getBridge();
        while (it.hasNext()) {
            FrameworkBridge.AnnotatedPostProcessor next = it.next();
            if (!next.getInType().isAssignableFrom(prev.getOutType())) return false;
            prev = next;
        }
        return ProMPetrinetWrapper.class.isAssignableFrom(prev.getOutType());
    }

    private void initializeFromProMConfig(ProMConfig pc) {
        supervisionComboBox.setSelectedItem(pc.supervisionSetting);
        expansionStrategyComboBox.setSelectedItem(pc.treeExpansionSetting);
        respectWiringCheckBox.setSelected(pc.respectWiring);
        supportRestartCheckBox.setSelected(pc.supportRestart);
        heuristicComboBox.setSelectedItem(pc.bridgedHeuristics);
        concurrentReplayCheckBox.setSelected(pc.concurrentReplay);
        restrictToFittingSubLogCheckBox.setSelected(pc.restrictToFittingSubLog);
        bridgedHeuristicsLabeledComboBox.getComboBox().setSelectedItem(pc.bridgedHeuristics);
        compositionStrategyComboBox.setSelectedItem(pc.compositionStrategy);
        applyCIPRCheckBox.setSelected(pc.applyCIPR);
        ppPipelineModel.clear();
        pc.ppPipeline.forEach(ppPipelineModel::append);
        tauInput.setText(Double.toString(pc.tau));
        deltaInput.setText(pc.delta < 0 ? null : Double.toString(pc.delta));
        steepnessInput.setText(pc.steepness < 0 ? null : Integer.toString(pc.steepness));
        if (pc.depth >= 0) {
            depthInput.setText(Integer.toString(pc.depth));
            depthInput.activate();
        } depthInput.deactivate();
        if (pc.discoveryTimeLimit != null) {
            discoveryTimeLimitInput.setText(pc.discoveryTimeLimit.toString());
            discoveryTimeLimitInput.activate();
        } else discoveryTimeLimitInput.deactivate();
        if (pc.totalTimeLimit != null) {
            totalTimeLimitInput.setText(pc.totalTimeLimit.toString());
            totalTimeLimitInput.activate();
        } else totalTimeLimitInput.deactivate();
        updateReadinessState();
    }

    public ProMConfig collectConfig() {
        ProMConfig pc = new ProMConfig();

        pc.supervisionSetting = (SupervisionSetting) supervisionComboBox.getSelectedItem();
        pc.logToFile = logToFileCheckBox.isSelected();
        pc.treeExpansionSetting = (TreeExpansionSetting) expansionStrategyComboBox.getSelectedItem();
        pc.respectWiring = respectWiringCheckBox.isSelected();
        pc.supportRestart = supportRestartCheckBox.isSelected();
        pc.bridgedHeuristics = (FrameworkBridge.BridgedHeuristics) heuristicComboBox.getSelectedItem();
        pc.concurrentReplay = concurrentReplayCheckBox.isSelected();
        pc.restrictToFittingSubLog = restrictToFittingSubLogCheckBox.isSelected();
        pc.bridgedDelta = (FrameworkBridge.BridgedDeltaAdaptationFunctions) deltaAdaptationFunctionComboBox.getSelectedItem();
        pc.compositionStrategy = (CompositionStrategy) compositionStrategyComboBox.getSelectedItem();
        pc.applyCIPR = applyCIPRCheckBox.isSelected();
        if (!validatePostProcessingPipeline(ppPipelineModel)) return null;
        pc.ppPipeline = ImmutableList.copyOf(ppPipelineModel.iterator());
        Double rawTau = tauInput.getInput();
        pc.tau = rawTau != null ? rawTau : -1;
        Double rawDelta = deltaInput.getInput();
        pc.delta = rawDelta != null ? rawDelta : -1;
        Integer rawSteepness = steepnessInput.getInput();
        pc.steepness = rawSteepness != null ? rawSteepness : -1;
        Integer rawDepthLimit = depthInput.getInput();
        pc.depth = rawDepthLimit != null ? rawDepthLimit : -1;
        pc.discoveryTimeLimit = discoveryTimeLimitInput.getInput();
        pc.totalTimeLimit = totalTimeLimitInput.getInput();

        // COLLECTION COMPLETE

        // VALIDATING COMPLETENESS
        if (!pc.validate()) return null;

        return pc;
    }


    private void tryRun() {
        ProMConfig collectedConfig = collectConfig();
        if (collectedConfig != null) controller.basicConfigCompleted(collectedConfig);
    }

    private void updatedParameters() {
        updateReadinessState();
    }

    private void updatedCompositionSettings() {
        deltaAdaptationLabeledComboBox.setVisible(compositionStrategyComboBox.getSelectedItem() == CompositionStrategy.TauDelta);
        deltaInput.setVisible(compositionStrategyComboBox.getSelectedItem() == CompositionStrategy.TauDelta);
        steepnessInput.setVisible(compositionStrategyComboBox.getSelectedItem() == CompositionStrategy.TauDelta);
        revalidate();
        updateReadinessState();
    }

    private void updatedEvaluationSettings() {
        updateReadinessState();
    }

    private void updatedProposalSettings() {
        bridgedHeuristicsLabeledComboBox.setVisible(expansionStrategyComboBox.getSelectedItem() == TreeExpansionSetting.Heuristic);
        revalidate();
        updateReadinessState();
    }

    private void updatedPreset() {
        Preset preset = (Preset) presetComboBox.getSelectedItem();
        if (preset != null) {
            ProMConfig cfg;
            if (preset == Preset.Last) {
                cfg = controller.getParentController().getProMConfig();
            } else if (preset == Preset.Loaded) {
                cfg = controller.getParentController().getLoadedProMConfig();
            } else cfg = preset.getConfig();
            initializeFromProMConfig(cfg == null ? Preset.Default.getConfig() : cfg);
        }
        updateReadinessState();
    }

    private void updatedSupervisionSettings() {
        updateReadinessState();
    }

    private void updateReadinessState() {
        SwingUtilities.invokeLater(() -> {
            ProMConfig pc = collectConfig();
            //System.out.println("ConfigurationPanel.updateReadinessState: " + pc != null);
            runButton.setEnabled(pc != null);
        });
    }

    public enum SupervisionSetting {
        None, PerformanceOnly, Full
    }

    public enum Preset {
        Default(ProMConfig::getDefault), Lightweight(ProMConfig::getLightweight), Last(null), Loaded(null);

        private final DataSource<ProMConfig> configSource;

        Preset(DataSource<ProMConfig> config) {
            this.configSource = config;
        }

        public ProMConfig getConfig() {
            return configSource.getData();
        }
    }

    public enum TreeExpansionSetting {
        BFS, DFS, Heuristic
    }

    public enum CompositionStrategy {
        Standard("Standard", ""), TauDelta("Tau-Delta", ""), Uniwired("Uniwired", "");

        private final String printableName;
        private final String description;

        CompositionStrategy(String printableName, String description) {
            this.printableName = printableName;
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return printableName;
        }
    }

}
