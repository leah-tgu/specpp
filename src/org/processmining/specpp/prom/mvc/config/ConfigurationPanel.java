package org.processmining.specpp.prom.mvc.config;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import com.google.common.collect.ImmutableList;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.prom.alg.FrameworkBridge;
import org.processmining.specpp.prom.mvc.AbstractStagePanel;
import org.processmining.specpp.prom.mvc.swing.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;
import java.util.function.Predicate;

public class ConfigurationPanel extends AbstractStagePanel<ConfigurationController> {


    private final JComboBox<Preset> presetComboBox;
    private final JComboBox<SupervisionSetting> supervisionComboBox;
    private final JCheckBox trackCandidateTreeCheckBox;
    private final JComboBox<TreeExpansionSetting> expansionStrategyComboBox;
    private final JComboBox<FrameworkBridge.BridgedHeuristics> heuristicComboBox;
    private final JCheckBox permitWiringCheckBox;
    private final JCheckBox permitRestartCheckBox;
    private final JCheckBox concurrentReplayCheckBox;
    private final JCheckBox restrictToFittingSubLogCheckBox;
    private final JComboBox<FrameworkBridge.BridgedDeltaAdaptationFunctions> deltaAdaptationFunctionComboBox;
    private final JComboBox<CompositionStrategy> compositionStrategyComboBox;
    private final JCheckBox applyCIPRCheckBox;
    private final MyListModel<FrameworkBridge.AnnotatedPostProcessor> ppPipelineModel;
    private final JTextField tauField;
    private final JTextField deltaField;
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
    private final TextBasedInputField<Double> deltaInput;
    private final ActivatableTextBasedInputField<Integer> depthInput;
    private final ActivatableTextBasedInputField<Duration> discoveryTimeLimitInput;
    private final ActivatableTextBasedInputField<Duration> totalTimeLimitInput;
    private final TextBasedInputField<Double> tauInput;
    private final JButton runButton;
    private final JCheckBox permitSubtreeCutoffCheckBox;

    public ConfigurationPanel(ConfigurationController controller) {
        super(controller, new GridBagLayout());

        // ** SUPERVISION ** //

        TitledBorderPanel supervision = new TitledBorderPanel("Supervision");
        LabeledComboBox<Preset> presetLabeledComboBox = SwingFactory.labeledComboBox("Preset", Preset.values());
        presetComboBox = presetLabeledComboBox.getComboBox();
        presetComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) updatedPreset();
        });
        supervision.append(presetLabeledComboBox);
        LabeledComboBox<SupervisionSetting> supervisionSettingLabeledComboBox = SwingFactory.labeledComboBox("Level of Detail", SupervisionSetting.values());
        supervisionComboBox = supervisionSettingLabeledComboBox.getComboBox();
        supervisionComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) updatedSupervisionSettings();
        });
        supervision.append(supervisionSettingLabeledComboBox);
        trackCandidateTreeCheckBox = SwingFactory.labeledCheckBox("track candidate tree");
        trackCandidateTreeCheckBox.addChangeListener(e -> updatedSupervisionSettings());
        supervision.append(trackCandidateTreeCheckBox);
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
        permitWiringCheckBox = SwingFactory.labeledCheckBox("Permit Wiring Constraints");
        permitWiringCheckBox.addChangeListener(e -> updatedProposalSettings());
        proposal.append(permitWiringCheckBox);
        permitRestartCheckBox = SwingFactory.labeledCheckBox("Permit Restart");
        permitRestartCheckBox.addChangeListener(e -> updatedProposalSettings());
        proposal.append(permitRestartCheckBox);
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

        TitledBorderPanel parameters = new TitledBorderPanel("Parameters");
        tauInput = SwingFactory.textBasedInputField("tau", zeroOneDoubleFunc, 10);
        tauField = tauInput.getTextField();
        parameters.append(tauInput);
        deltaInput = SwingFactory.textBasedInputField("delta", zeroOneDoubleFunc, 10);
        deltaField = deltaInput.getTextField();
        deltaInput.setVisible(false);
        parameters.append(deltaInput);
        depthInput = SwingFactory.activatableTextBasedInputField("max depth", false, posIntFunc, 10);
        depthField = depthInput.getTextField();
        parameters.append(depthInput);
        parameters.completeWithWhitespace();

        // ** EXECUTION ** //

        TitledBorderPanel execution = new TitledBorderPanel("Execution");
        discoveryTimeLimitInput = SwingFactory.activatableTextBasedInputField("discovery time limit", false, durationFunc, 25);
        discoveryTimeLimitInput.getTextField()
                               .setToolTipText("<html>ISO-8601 format: P<it>x</it>DT<it>x</it>H<it>x</it>M<it>x</it>.<it>x</it>S</html>");
        execution.append(discoveryTimeLimitInput);
        totalTimeLimitInput = SwingFactory.activatableTextBasedInputField("total time limit", false, durationFunc, 25);
        totalTimeLimitInput.getTextField()
                           .setToolTipText("<html>ISO-8601 format: P<it>x</it>DT<it>x</it>H<it>x</it>M<it>x</it>.<it>x</it>S</html>");
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

        initializeFromProMConfig(ProMConfig.getDefault());
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
        permitWiringCheckBox.setSelected(pc.permitWiring);
        permitRestartCheckBox.setSelected(pc.permitRestart);
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
        depthInput.setText(pc.depth < 0 ? null : Integer.toString(pc.depth));
        if (pc.discoveryTimeLimit != null) {
            discoveryTimeLimitInput.setText(pc.discoveryTimeLimit.toString());
            discoveryTimeLimitInput.activate();
        } else discoveryTimeLimitInput.deactivate();
        if (pc.totalTimeLimit != null) {
            totalTimeLimitInput.setText(pc.totalTimeLimit.toString());
            totalTimeLimitInput.activate();
        } else totalTimeLimitInput.deactivate();
    }

    static class ProMConfig {
        SupervisionSetting supervisionSetting;
        TreeExpansionSetting treeExpansionSetting;
        boolean permitWiring, permitRestart;
        FrameworkBridge.BridgedHeuristics bridgedHeuristics;
        boolean concurrentReplay, restrictToFittingSubLog;
        FrameworkBridge.BridgedDeltaAdaptationFunctions bridgedDelta;
        CompositionStrategy compositionStrategy;
        boolean applyCIPR;
        List<FrameworkBridge.AnnotatedPostProcessor> ppPipeline;
        double tau, delta;
        int depth;
        Duration discoveryTimeLimit, totalTimeLimit;

        public static ProMConfig getDefault() {
            ProMConfig pc = new ProMConfig();
            pc.supervisionSetting = SupervisionSetting.Full;
            pc.treeExpansionSetting = TreeExpansionSetting.Heuristic;
            pc.permitWiring = false;
            pc.permitRestart = false;
            pc.bridgedHeuristics = FrameworkBridge.BridgedHeuristics.BFS_Emulation;
            pc.concurrentReplay = false;
            pc.restrictToFittingSubLog = false;
            pc.bridgedDelta = FrameworkBridge.BridgedDeltaAdaptationFunctions.Static;
            pc.compositionStrategy = CompositionStrategy.Standard;
            pc.applyCIPR = true;
            pc.ppPipeline = ImmutableList.of(FrameworkBridge.BridgedPostProcessors.ReplayBasedImplicitPlaceRemoval.getBridge(), FrameworkBridge.BridgedPostProcessors.SelfLoopPlacesMerging.getBridge(), FrameworkBridge.BridgedPostProcessors.ProMPetrinetConversion.getBridge());
            pc.tau = 1.0;
            pc.delta = -1.0;
            pc.depth = -1;
            pc.discoveryTimeLimit = null;
            pc.totalTimeLimit = null;
            return pc;
        }

        public static ProMConfig getLightweight() {
            ProMConfig pc = getDefault();
            pc.supervisionSetting = SupervisionSetting.Lightweight;
            pc.treeExpansionSetting = TreeExpansionSetting.DFS;
            return pc;
        }

        public boolean validate() {
            boolean outOfRange = tau < 0 || tau > 1.0;
            outOfRange |= compositionStrategy == CompositionStrategy.TauDelta && delta < 0;
            boolean incomplete = (supervisionSetting == null | treeExpansionSetting == null | compositionStrategy == null);
            incomplete |= treeExpansionSetting == TreeExpansionSetting.Heuristic && bridgedHeuristics == null;
            incomplete |= compositionStrategy == CompositionStrategy.TauDelta && (bridgedDelta == null || delta < 0);
            return !outOfRange && !incomplete;
        }

    }

    public ProMConfig collectConfig() {
        ProMConfig pc = new ProMConfig();

        pc.supervisionSetting = (SupervisionSetting) supervisionComboBox.getSelectedItem();
        pc.treeExpansionSetting = (TreeExpansionSetting) expansionStrategyComboBox.getSelectedItem();
        pc.permitWiring = permitWiringCheckBox.isSelected();
        pc.permitRestart = permitRestartCheckBox.isSelected();
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
        deltaAdaptationFunctionComboBox.revalidate();
        deltaInput.setVisible(compositionStrategyComboBox.getSelectedItem() == CompositionStrategy.TauDelta);
        deltaInput.revalidate();
        updateReadinessState();
    }

    private void updatedEvaluationSettings() {
        updateReadinessState();
    }

    private void updatedProposalSettings() {
        bridgedHeuristicsLabeledComboBox.setVisible(expansionStrategyComboBox.getSelectedItem() == TreeExpansionSetting.Heuristic);
        bridgedHeuristicsLabeledComboBox.revalidate();
        updateReadinessState();
    }

    private void updatedPreset() {
        Preset preset = (Preset) presetComboBox.getSelectedItem();
        if (preset != null) initializeFromProMConfig(preset.getConfig());
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
        Lightweight, PerformanceOnly, Full
    }

    public enum Preset {
        Default(ProMConfig.getDefault()), Lightweight(ProMConfig.getLightweight());

        private final ProMConfig config;

        Preset(ProMConfig config) {
            this.config = config;
        }

        public ProMConfig getConfig() {
            return config;
        }
    }

    public enum TreeExpansionSetting {
        BFS, DFS, Heuristic
    }

    public enum CompositionStrategy {
        Standard, TauDelta, Uniwired
    }

}
