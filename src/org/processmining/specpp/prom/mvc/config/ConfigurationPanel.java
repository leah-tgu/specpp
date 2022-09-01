package org.processmining.specpp.prom.mvc.config;

import org.processmining.framework.util.ui.widgets.ProMCheckBoxWithTextField;
import org.processmining.framework.util.ui.widgets.ProMComboBoxWithTextField;
import org.processmining.framework.util.ui.widgets.ProMList;
import org.processmining.specpp.prom.alg.FrameworkBridge;
import org.processmining.specpp.prom.util.EnumTransferable;
import org.processmining.specpp.prom.util.FactoryUtils;
import org.processmining.specpp.prom.util.MyListModel;
import org.processmining.specpp.prom.util.TitledBorderPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.event.ItemEvent;

public class ConfigurationPanel extends JPanel {


    public ConfigurationPanel() {
        super(new GridBagLayout());

        ProMComboBoxWithTextField presetComboBox = FactoryUtils.labeledPromComboBox("Preset", Preset.values());
        presetComboBox.getComboBox().addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) updatedPreset();
        });

        TitledBorderPanel supervision = new TitledBorderPanel("Supervision");
        ProMComboBoxWithTextField supervisionComboBox = FactoryUtils.labeledPromComboBox("Level of Detail", SupervisionSetting.values());
        supervisionComboBox.getComboBox().addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) updatedSupervisionSettings();
        });
        supervision.add(supervisionComboBox);
        ProMCheckBoxWithTextField trackCandidateTreeCheckBox = FactoryUtils.labeledCheckBox("track candidate tree");
        trackCandidateTreeCheckBox.getCheckBox().addChangeListener(e -> updatedSupervisionSettings());
        supervision.add(trackCandidateTreeCheckBox);

        TitledBorderPanel proposal = new TitledBorderPanel("Proposal");
        ProMComboBoxWithTextField enumerationStrategyComboBox = FactoryUtils.labeledPromComboBox("Candidate Enumeration", TreeExpansionSetting.values());
        proposal.add(enumerationStrategyComboBox);
        ProMComboBoxWithTextField heuristicComboBox = FactoryUtils.labeledPromComboBox("Heuristic", FrameworkBridge.HEURISTICS.toArray());
        heuristicComboBox.getComboBox().addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) updatedProposalSettings();
        });
        heuristicComboBox.setVisible(false);
        proposal.add(heuristicComboBox);
        ProMCheckBoxWithTextField permitWiringCheckBox = FactoryUtils.labeledCheckBox("Permit Constraints");
        permitWiringCheckBox.getCheckBox().addChangeListener(e -> updatedProposalSettings());
        proposal.add(permitWiringCheckBox);
        ProMCheckBoxWithTextField permitRestartCheckBox = FactoryUtils.labeledCheckBox("Permit Restart");
        permitRestartCheckBox.getCheckBox().addChangeListener(e -> updatedProposalSettings());
        proposal.add(permitRestartCheckBox);

        TitledBorderPanel evaluation = new TitledBorderPanel("Evaluation");
        ProMCheckBoxWithTextField concurrentReplayCheckBox = FactoryUtils.labeledCheckBox("use concurrent replay implementation");
        concurrentReplayCheckBox.getCheckBox().addChangeListener(e -> updatedEvaluationSettings());
        evaluation.add(concurrentReplayCheckBox);

        ProMCheckBoxWithTextField fittingSublogCheckBox = FactoryUtils.labeledCheckBox("restrict replay to fitting sub log for implicit place removal");
        fittingSublogCheckBox.getCheckBox().addChangeListener(e -> updatedEvaluationSettings());
        evaluation.add(fittingSublogCheckBox);
        ProMComboBoxWithTextField deltaAdaptationFunctionComboBox = FactoryUtils.labeledPromComboBox("Delta Adaptation Function", FrameworkBridge.DELTA_FUNCTIONS.toArray());
        deltaAdaptationFunctionComboBox.getComboBox().addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) updatedEvaluationSettings();
        });
        deltaAdaptationFunctionComboBox.setVisible(false);
        evaluation.add(deltaAdaptationFunctionComboBox);


        TitledBorderPanel composition = new TitledBorderPanel("Composition");
        ProMComboBoxWithTextField compositionVariantComboBox = FactoryUtils.labeledPromComboBox("Variant", CompositionStrategy.values());
        compositionVariantComboBox.getComboBox().addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) updatedCompositionSettings();
        });
        composition.add(compositionVariantComboBox);
        ProMCheckBoxWithTextField ciprCheckBox = FactoryUtils.labeledCheckBox("apply replay-based concurrent implicit place removal");
        ciprCheckBox.getCheckBox().addChangeListener(e -> updatedCompositionSettings());
        composition.add(ciprCheckBox);


        TitledBorderPanel postProcessing = new TitledBorderPanel("Post Processing");
        postProcessing.setLayout(new GridBagLayout());
        ProMList<FrameworkBridge.BridgedPostProcessors> availablePostProcessors = new ProMList<>("Available Post Processors", new MyListModel<>(FrameworkBridge.POST_PROCESSORS));
        JList outList = availablePostProcessors.getList();
        outList.setDragEnabled(true);
        outList.setDropMode(DropMode.INSERT);
        outList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        outList.setTransferHandler(new TransferHandler() {
            @Override
            protected Transferable createTransferable(JComponent c) {
                return new EnumTransferable<>((FrameworkBridge.BridgedPostProcessors) outList.getSelectedValue());
            }

            @Override
            public int getSourceActions(JComponent c) {
                return COPY;
            }

        });

        GridBagConstraints ppc = new GridBagConstraints();
        postProcessing.add(availablePostProcessors, ppc);
        ProMList<FrameworkBridge.BridgedPostProcessors> postProcessingPipeline = new ProMList<>("Post Processing Pipeline", new MyListModel<>());

        ppc.gridx++;
        postProcessing.add(postProcessingPipeline, ppc);

        GridBagConstraints c = new GridBagConstraints();
        add(presetComboBox, c);
        c.gridy++;
        add(new JSeparator(JSeparator.HORIZONTAL), c);
        c.gridy++;
        add(supervision, c);
        c.gridy++;
        add(proposal, c);
        c.gridy++;
        add(evaluation, c);
        c.gridy++;
        add(composition, c);
        c.gridy = 0;
        c.gridx = 1;


    }

    private void updatedCompositionSettings() {

    }

    private void updatedEvaluationSettings() {

    }

    private void updatedProposalSettings() {


    }

    private void updatedPreset() {

    }

    private void updatedSupervisionSettings() {

    }

    public enum SupervisionSetting {
        Lightweight, PerformanceOnly, Full
    }

    public enum Preset {
        Default
    }

    public enum TreeExpansionSetting {
        BFS, DFS, Heuristic
    }

    public enum CompositionStrategy {
        Standard, TauDelta, Uniwired
    }

}
