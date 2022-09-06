package org.processmining.specpp.prom.mvc.config;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import com.google.common.collect.ImmutableList;
import org.processmining.specpp.prom.alg.FrameworkBridge;
import org.processmining.specpp.prom.mvc.AbstractStagePanel;
import org.processmining.specpp.prom.util.*;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
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
    private final MyListModel<FrameworkBridge.BridgedPostProcessors> ppPipelineModel;
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

    public ConfigurationPanel(ConfigurationController controller) {
        super(controller, new GridBagLayout());

        // ** SUPERVISION ** //

        TitledBorderPanel supervision = new TitledBorderPanel("Supervision");
        LabeledComboBox<Preset> presetLabeledComboBox = FactoryUtils.labeledComboBox("Preset", Preset.values());
        presetComboBox = presetLabeledComboBox.getComboBox();
        presetComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) updatedPreset();
        });
        supervision.append(presetLabeledComboBox);
        LabeledComboBox<SupervisionSetting> supervisionSettingLabeledComboBox = FactoryUtils.labeledComboBox("Level of Detail", SupervisionSetting.values());
        supervisionComboBox = supervisionSettingLabeledComboBox.getComboBox();
        supervisionComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) updatedSupervisionSettings();
        });
        supervision.append(supervisionSettingLabeledComboBox);
        trackCandidateTreeCheckBox = FactoryUtils.labeledCheckBox("track candidate tree");
        trackCandidateTreeCheckBox.addChangeListener(e -> updatedSupervisionSettings());
        supervision.append(trackCandidateTreeCheckBox);
        supervision.completeWithWhitespace();

        // ** PROPOSAL ** //

        TitledBorderPanel proposal = new TitledBorderPanel("Proposal");
        LabeledComboBox<TreeExpansionSetting> candidate_enumeration = FactoryUtils.labeledComboBox("Candidate Enumeration", TreeExpansionSetting.values());
        expansionStrategyComboBox = candidate_enumeration.getComboBox();
        proposal.append(candidate_enumeration);
        bridgedHeuristicsLabeledComboBox = FactoryUtils.labeledComboBox("Heuristic", FrameworkBridge.HEURISTICS.toArray(new FrameworkBridge.BridgedHeuristics[0]));
        heuristicComboBox = bridgedHeuristicsLabeledComboBox.getComboBox();
        heuristicComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) updatedProposalSettings();
        });
        bridgedHeuristicsLabeledComboBox.setVisible(false);
        proposal.append(bridgedHeuristicsLabeledComboBox);
        permitWiringCheckBox = FactoryUtils.labeledCheckBox("Permit Wiring Constraints");
        permitWiringCheckBox.addChangeListener(e -> updatedProposalSettings());
        proposal.append(permitWiringCheckBox);
        permitRestartCheckBox = FactoryUtils.labeledCheckBox("Permit Restart");
        permitRestartCheckBox.addChangeListener(e -> updatedProposalSettings());
        proposal.append(permitRestartCheckBox);
        proposal.completeWithWhitespace();

        // ** EVALUATION ** //

        TitledBorderPanel evaluation = new TitledBorderPanel("Evaluation");
        concurrentReplayCheckBox = FactoryUtils.labeledCheckBox("use concurrent replay implementation");
        concurrentReplayCheckBox.addChangeListener(e -> updatedEvaluationSettings());
        evaluation.append(concurrentReplayCheckBox);

        restrictToFittingSubLogCheckBox = FactoryUtils.labeledCheckBox("restrict replay to fitting sub log for implicit place removal");
        restrictToFittingSubLogCheckBox.addChangeListener(e -> updatedEvaluationSettings());
        evaluation.append(restrictToFittingSubLogCheckBox);
        deltaAdaptationLabeledComboBox = FactoryUtils.labeledComboBox("Delta Adaptation Function", FrameworkBridge.DELTA_FUNCTIONS.toArray(new FrameworkBridge.BridgedDeltaAdaptationFunctions[0]));
        deltaAdaptationFunctionComboBox = deltaAdaptationLabeledComboBox.getComboBox();
        deltaAdaptationFunctionComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) updatedEvaluationSettings();
        });
        deltaAdaptationLabeledComboBox.setVisible(false);
        evaluation.append(deltaAdaptationLabeledComboBox);
        evaluation.completeWithWhitespace();

        // ** COMPOSITION ** //

        TitledBorderPanel composition = new TitledBorderPanel("Composition");
        LabeledComboBox<CompositionStrategy> compositionStrategyLabeledComboBox = FactoryUtils.labeledComboBox("Variant", CompositionStrategy.values());
        compositionStrategyComboBox = compositionStrategyLabeledComboBox.getComboBox();
        compositionStrategyComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) updatedCompositionSettings();
        });
        composition.append(compositionStrategyLabeledComboBox);
        applyCIPRCheckBox = FactoryUtils.labeledCheckBox("apply replay-based concurrent implicit place removal");
        applyCIPRCheckBox.addChangeListener(e -> updatedCompositionSettings());
        composition.append(applyCIPRCheckBox);

        // ** POST PROCESSING ** //

        TitledBorderPanel postProcessing = new TitledBorderPanel("Post Processing");
        JList<FrameworkBridge.BridgedPostProcessors> outList = new JList<>(new MyListModel<>(FrameworkBridge.POST_PROCESSORS));
        outList.setDragEnabled(true);
        outList.setDropMode(DropMode.INSERT);
        outList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        outList.setTransferHandler(new TransferHandler() {
            @Override
            protected Transferable createTransferable(JComponent c) {
                return new EnumTransferable<>(outList.getSelectedValue());
            }

            @Override
            public int getSourceActions(JComponent c) {
                return COPY;
            }

        });
        GridBagConstraints ppc = new GridBagConstraints();
        ppc.insets = new Insets(10, 15, 10, 15);
        ppc.gridx = 0;
        ppc.gridy = 0;
        ppc.fill = GridBagConstraints.BOTH;
        ppc.weightx = 1;
        ppc.weighty = 0;
        postProcessing.add(FactoryUtils.createHeader("Available Post Processors"), ppc);
        ppc.weighty = 1;
        ppc.gridy++;
        postProcessing.add(new JScrollPane(outList), ppc);

        ppPipelineModel = new MyListModel<>();
        JList<FrameworkBridge.BridgedPostProcessors> inList = new JList<>(ppPipelineModel);
        inList.setDragEnabled(true);
        inList.setDropMode(DropMode.INSERT);
        inList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inList.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    int i = inList.getSelectedIndex();
                    if (i >= 0) ppPipelineModel.remove(i);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        inList.setTransferHandler(new TransferHandler() {

            private int importedIndex;
            private int exportedIndex;

            @Override
            protected Transferable createTransferable(JComponent c) {
                Object selectedValue = inList.getSelectedValue();
                exportedIndex = inList.getSelectedIndex();
                return new EnumTransferable<>((FrameworkBridge.BridgedPostProcessors) selectedValue);
            }

            @Override
            public int getSourceActions(JComponent c) {
                return MOVE;
            }

            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDrop() && support.isDataFlavorSupported(EnumTransferable.myFlave);
            }

            @Override
            protected void exportDone(JComponent source, Transferable data, int action) {
                if (action == MOVE) {
                    ppPipelineModel.remove(importedIndex < exportedIndex ? exportedIndex + 1 : exportedIndex);
                }
            }

            @Override
            public boolean importData(TransferSupport support) {
                try {
                    FrameworkBridge.BridgedPostProcessors transferData = (FrameworkBridge.BridgedPostProcessors) support.getTransferable()
                                                                                                                        .getTransferData(EnumTransferable.myFlave);
                    JList.DropLocation dl = (JList.DropLocation) support.getDropLocation();
                    int index = dl.getIndex();
                    importedIndex = index;
                    ppPipelineModel.insert(transferData, index);
                    return true;
                } catch (UnsupportedFlavorException | IOException | ClassCastException ignored) {
                }
                return false;
            }

        });

        ppc.gridx = 1;
        ppc.gridy = 0;
        ppc.weighty = 0;
        postProcessing.add(FactoryUtils.createHeader("Post Processing Pipeline"), ppc);
        ppc.weighty = 1;
        ppc.gridy++;
        postProcessing.add(new JScrollPane(inList), ppc);
        JLabel ppTypesOkay = SlickerFactory.instance().createLabel("are types ok?");
        ppc.weighty = 0.1;
        ppc.gridx = 0;
        ppc.gridy = 2;
        ppc.gridwidth = 2;
        ppc.anchor = GridBagConstraints.CENTER;
        postProcessing.add(ppTypesOkay, ppc);
        ppPipelineModel.addListDataListener(new ListDataListener() {

            private void updateValidationStatus() {
                if (validatePostProcessingPipeline()) {
                    ppTypesOkay.setText("input & output types match");
                    ppTypesOkay.setIcon(Iconic.checkmark);
                } else {
                    ppTypesOkay.setText("input & output types are incompatible");
                    ppTypesOkay.setIcon(Iconic.cross);
                }
            }

            @Override
            public void intervalAdded(ListDataEvent e) {
                updateValidationStatus();
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                updateValidationStatus();
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                updateValidationStatus();
            }
        });

        // ** PARAMETERS ** //

        TitledBorderPanel parameters = new TitledBorderPanel("Parameters");
        tauInput = FactoryUtils.textBasedInputField("tau", zeroOneDoubleFunc);
        tauField = tauInput.getTextField();
        parameters.append(tauInput);
        deltaInput = FactoryUtils.textBasedInputField("delta", zeroOneDoubleFunc);
        deltaField = deltaInput.getTextField();
        deltaInput.setVisible(false);
        parameters.append(deltaInput);
        depthInput = FactoryUtils.activatableTextBasedInputField("max depth", false, posIntFunc);
        depthField = depthInput.getTextField();
        parameters.append(depthInput);
        parameters.completeWithWhitespace();

        // ** EXECUTION ** //

        TitledBorderPanel execution = new TitledBorderPanel("Execution");
        discoveryTimeLimitInput = FactoryUtils.activatableTextBasedInputField("discovery time limit", false, durationFunc);
        discoveryTimeLimitInput.getTextField()
                               .setToolTipText("<html>ISO-8601 format: P<it>x</it>DT<it>x</it>H<it>x</it>M<it>x</it>.<it>x</it>S</html>");
        execution.append(discoveryTimeLimitInput);
        totalTimeLimitInput = FactoryUtils.activatableTextBasedInputField("total time limit", false, durationFunc);
        totalTimeLimitInput.getTextField()
                           .setToolTipText("<html>ISO-8601 format: P<it>x</it>DT<it>x</it>H<it>x</it>M<it>x</it>.<it>x</it>S</html>");
        execution.append(totalTimeLimitInput);

        JButton run = SlickerFactory.instance().createButton("run");
        run.addActionListener(e -> tryRun());
        execution.append(run);
        evaluation.completeWithWhitespace();

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

    public boolean validatePostProcessingPipeline() {
        ListIterator<FrameworkBridge.BridgedPostProcessors> it = ppPipelineModel.iterator();
        if (!it.hasNext()) return true;
        FrameworkBridge.BridgedPostProcessor prev = FrameworkBridge.BridgedPostProcessors.Identity.getBridge();
        while (it.hasNext()) {
            FrameworkBridge.BridgedPostProcessor next = it.next().getBridge();
            if (!next.getInType().isAssignableFrom(prev.getOutType())) return false;
            prev = next;
        }
        return true;
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
        List<FrameworkBridge.BridgedPostProcessors> ppPipeline;
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
            pc.ppPipeline = ImmutableList.of(FrameworkBridge.BridgedPostProcessors.ReplayBasedImplicitPlaceRemoval, FrameworkBridge.BridgedPostProcessors.SelfLoopPlacesMerging);
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
        if (!validatePostProcessingPipeline()) return null;
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

    }

    private void updatedCompositionSettings() {
        deltaAdaptationLabeledComboBox.setVisible(compositionStrategyComboBox.getSelectedItem() == CompositionStrategy.TauDelta);
        deltaInput.setVisible(compositionStrategyComboBox.getSelectedItem() == CompositionStrategy.TauDelta);
    }

    private void updatedEvaluationSettings() {

    }

    private void updatedProposalSettings() {
        bridgedHeuristicsLabeledComboBox.setVisible(expansionStrategyComboBox.getSelectedItem() == TreeExpansionSetting.Heuristic);
    }

    private void updatedPreset() {
        Preset preset = (Preset) presetComboBox.getSelectedItem();
        if (preset != null) initializeFromProMConfig(preset.getConfig());
    }

    private void updatedSupervisionSettings() {

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
