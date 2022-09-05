package org.processmining.specpp.prom.mvc.config;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import com.google.common.collect.ImmutableList;
import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.base.IdentityPostProcessor;
import org.processmining.specpp.base.impls.*;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.data.StaticDataSource;
import org.processmining.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.specpp.componenting.system.AbstractGlobalComponentSystemUser;
import org.processmining.specpp.componenting.traits.ProvidesParameters;
import org.processmining.specpp.composition.ConstrainingPlaceCollection;
import org.processmining.specpp.composition.PlaceCollection;
import org.processmining.specpp.config.*;
import org.processmining.specpp.config.parameters.DeltaParameters;
import org.processmining.specpp.config.parameters.PlaceGeneratorParameters;
import org.processmining.specpp.config.parameters.SupervisionParameters;
import org.processmining.specpp.config.parameters.TauFitnessThresholds;
import org.processmining.specpp.datastructures.petri.PetriNet;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.datastructures.tree.base.impls.EnumeratingTree;
import org.processmining.specpp.datastructures.tree.base.impls.EventingEnumeratingTree;
import org.processmining.specpp.datastructures.tree.base.impls.VariableExpansion;
import org.processmining.specpp.datastructures.tree.heuristic.DoubleScore;
import org.processmining.specpp.datastructures.tree.heuristic.EventingHeuristicTreeExpansion;
import org.processmining.specpp.datastructures.tree.heuristic.HeuristicTreeExpansion;
import org.processmining.specpp.datastructures.tree.nodegen.MonotonousPlaceGenerationLogic;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceState;
import org.processmining.specpp.evaluation.fitness.AbsolutelyNoFrillsFitnessEvaluator;
import org.processmining.specpp.evaluation.fitness.ForkJoinFitnessEvaluator;
import org.processmining.specpp.evaluation.markings.LogHistoryMaker;
import org.processmining.specpp.orchestra.AdaptedAlgorithmParameterConfig;
import org.processmining.specpp.postprocessing.ProMConverter;
import org.processmining.specpp.prom.alg.FrameworkBridge;
import org.processmining.specpp.prom.mvc.AbstractStagePanel;
import org.processmining.specpp.prom.util.*;
import org.processmining.specpp.proposal.ConstrainablePlaceProposer;
import org.processmining.specpp.proposal.RestartablePlaceProposer;
import org.processmining.specpp.supervision.supervisors.AltEventCountsSupervisor;
import org.processmining.specpp.supervision.supervisors.BaseSupervisor;
import org.processmining.specpp.supervision.supervisors.PerformanceSupervisor;
import org.processmining.specpp.supervision.supervisors.TerminalSupervisor;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.time.Duration;
import java.util.ListIterator;
import java.util.function.Predicate;

public class ConfigurationPanel extends AbstractStagePanel<ConfigurationController> {


    private final JComboBox<Preset> presetComboBox;
    private final JComboBox<SupervisionSetting> supervisionComboBox;
    private final JCheckBox trackCandidateTreeCheckBox;
    private final JComboBox<TreeExpansionSetting> enumerationStrategyComboBox;
    private final JComboBox<FrameworkBridge.BridgedHeuristics> heuristicComboBox;
    private final JCheckBox permitWiringCheckBox;
    private final JCheckBox permitRestartCheckBox;
    private final JCheckBox concurrentReplayCheckBox;
    private final JCheckBox fittingSublogCheckBox;
    private final JComboBox<FrameworkBridge.BridgedDeltaAdaptationFunctions> deltaAdaptationFunctionComboBox;
    private final JComboBox<CompositionStrategy> compositionVariantComboBox;
    private final JCheckBox ciprCheckBox;
    private final MyListModel<FrameworkBridge.BridgedPostProcessors> ppPipelineModel;
    private final JTextField tauField;
    private final JTextField deltaField;
    private final JTextField depthField;
    private final JTextField timeField;
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
    private static final Predicate<JTextField> posIntPredicate = input -> {
        try {
            double v = Integer.parseInt(input.getText());
            return 0 < v;
        } catch (NumberFormatException e) {
            return false;
        }
    };
    private static final Predicate<JTextField> durationStringPredicate = input -> {
        if (input.getText().isEmpty()) return true;
        try {
            Duration.parse(input.getText());
        } catch (Exception e) {
            return false;
        }
        return true;
    };
    private final LabeledTextField labeledDelta;

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
        enumerationStrategyComboBox = candidate_enumeration.getComboBox();
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

        fittingSublogCheckBox = FactoryUtils.labeledCheckBox("restrict replay to fitting sub log for implicit place removal");
        fittingSublogCheckBox.addChangeListener(e -> updatedEvaluationSettings());
        evaluation.append(fittingSublogCheckBox);
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
        compositionVariantComboBox = compositionStrategyLabeledComboBox.getComboBox();
        compositionVariantComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) updatedCompositionSettings();
        });
        composition.append(compositionStrategyLabeledComboBox);
        ciprCheckBox = FactoryUtils.labeledCheckBox("apply replay-based concurrent implicit place removal");
        ciprCheckBox.addChangeListener(e -> updatedCompositionSettings());
        composition.append(ciprCheckBox);

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
        LabeledTextField tau = new LabeledTextField("tau", "");
        ActionListener doubleListener = e -> {
            JTextField source = (JTextField) e.getSource();
            updateValidityHighlight(source, zeroOneDoublePredicate);
            updatedParameters();
        };
        tauField = tau.getTextField();
        tauField.addActionListener(doubleListener);
        parameters.append(tau);
        labeledDelta = new LabeledTextField("labeledDelta", "");
        deltaField = labeledDelta.getTextField();
        deltaField.addActionListener(doubleListener);
        labeledDelta.setVisible(false);
        parameters.append(labeledDelta);
        LabeledTextField maxDepth = new LabeledTextField("max depth", "");
        depthField = maxDepth.getTextField();
        depthField.addActionListener(e -> {
            updateValidityHighlight(depthField, posIntPredicate);
            updatedParameters();
        });
        parameters.append(maxDepth);
        parameters.completeWithWhitespace();

        // ** EXECUTION ** //

        TitledBorderPanel execution = new TitledBorderPanel("Execution");
        LabeledTextField timeLimit = new LabeledTextField("time limit", "10min");
        timeField = timeLimit.getTextField();
        timeField.setToolTipText("<html>ISO-8601 format: P<it>x</it>DT<it>x</it>H<it>x</it>M<it>x</it>.<it>x</it>S</html>");
        timeField.addActionListener(e -> {
            System.out.println("ConfigurationPanel.ConfigurationPanel");
            System.out.println(e.getActionCommand());
            updateValidityHighlight(timeField, durationStringPredicate);
            updatedParameters();
        });
        execution.append(timeLimit);
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

        setDefaults();
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

    private void setDefaults() {

    }

    public ConfigsCollection collectConfig() {
        SupervisionSetting lod = (SupervisionSetting) supervisionComboBox.getSelectedItem();
        TreeExpansionSetting treeExpansionSetting = (TreeExpansionSetting) enumerationStrategyComboBox.getSelectedItem();
        boolean permitWiring = permitWiringCheckBox.isSelected();
        boolean permitRestart = permitRestartCheckBox.isSelected();
        FrameworkBridge.BridgedHeuristics bridgedHeuristics = (FrameworkBridge.BridgedHeuristics) heuristicComboBox.getSelectedItem();
        boolean concurrentReplay = concurrentReplayCheckBox.isSelected();
        boolean restrictToFittingSubLog = fittingSublogCheckBox.isSelected();
        FrameworkBridge.BridgedDeltaAdaptationFunctions bridgedDelta = (FrameworkBridge.BridgedDeltaAdaptationFunctions) deltaAdaptationFunctionComboBox.getSelectedItem();
        CompositionStrategy compositionStrategy = (CompositionStrategy) compositionVariantComboBox.getSelectedItem();
        boolean applyCIPR = ciprCheckBox.isSelected();
        if (!validatePostProcessingPipeline()) return null;
        ImmutableList<FrameworkBridge.BridgedPostProcessors> pipeline = ImmutableList.copyOf(ppPipelineModel.iterator());
        if (!updateValidityHighlight(tauField, zeroOneDoublePredicate)) return null;
        double tau = Double.parseDouble(tauField.getText());
        if (!updateValidityHighlight(deltaField, zeroOneDoublePredicate)) return null;
        double delta = Double.parseDouble(deltaField.getText());
        if (!updateValidityHighlight(depthField, posIntPredicate)) return null;
        int depth = Integer.parseInt(depthField.getText());
        if (!updateValidityHighlight(timeField, durationStringPredicate)) return null;
        Duration duration = Duration.parse(timeField.getText());

        // COLLECTION COMPLETE

        // VALIDATING COMPLETENESS

        boolean incomplete = false;
        incomplete |= lod == null | treeExpansionSetting == null | compositionStrategy == null;
        incomplete |= treeExpansionSetting == TreeExpansionSetting.Heuristic && bridgedHeuristics == null;
        incomplete |= compositionStrategy == CompositionStrategy.TauDelta && bridgedDelta == null;
        if (incomplete) return null;

        // BUILDING CONFIGURATORS

        // ** SUPERVISION ** //

        SupervisionConfiguration.Configurator svCfg = new SupervisionConfiguration.Configurator();
        svCfg.supervisor(BaseSupervisor::new);
        switch (lod) {
            case Lightweight:
                break;
            case PerformanceOnly:
                svCfg.supervisor(PerformanceSupervisor::new);
                break;
            case Full:
                svCfg.supervisor(PerformanceSupervisor::new);
                svCfg.supervisor(AltEventCountsSupervisor::new);
                break;
        }
        svCfg.supervisor(TerminalSupervisor::new);

        // ** PROPOSAL, COMPOSITION ** //
        ProposerComposerConfiguration.Configurator<Place, AdvancedComposition<Place>, PetriNet> pcCfg = new ProposerComposerConfiguration.Configurator<>();
        if (permitRestart) pcCfg.proposer(new RestartablePlaceProposer.Builder());
        else pcCfg.proposer(new ConstrainablePlaceProposer.Builder());
        if (permitWiring) pcCfg.composition(ConstrainingPlaceCollection::new);
        else if (compositionStrategy == CompositionStrategy.TauDelta || applyCIPR)
            pcCfg.composition(PlaceCollection::new);
        else pcCfg.composition(LightweightPlaceCollection::new);
        if (applyCIPR)
            pcCfg.terminalComposer(lod == SupervisionSetting.Full ? EventingPlaceComposerWithCIPR::new : PlaceComposerWithCIPR::new);
        else pcCfg.terminalComposer(PlaceAccepter::new);
        switch (compositionStrategy) {
            case Standard:
                pcCfg.composerChain(lod == SupervisionSetting.Full ? EventingPlaceFitnessFilter::new : PlaceFitnessFilter::new);
                break;
            case TauDelta:
                pcCfg.composerChain(lod == SupervisionSetting.Full ? EventingPlaceFitnessFilter::new : PlaceFitnessFilter::new, QueueingDeltaComposer::new);
                break;
            case Uniwired:
                pcCfg.composerChain(lod == SupervisionSetting.Full ? EventingPlaceFitnessFilter::new : PlaceFitnessFilter::new, UniwiredComposer::new);
                break;
        }

        // ** EVALUATION ** //

        EvaluatorConfiguration.Configurator evCfg = new EvaluatorConfiguration.Configurator();
        evCfg.evaluatorProvider(LogHistoryMaker::new);
        evCfg.evaluatorProvider(concurrentReplay ? ForkJoinFitnessEvaluator::new : AbsolutelyNoFrillsFitnessEvaluator::new);
        if (bridgedDelta != null) evCfg.evaluatorProvider(bridgedDelta.getBridge().getBuilder());

        EfficientTreeConfiguration.Configurator<Place, PlaceState, PlaceNode> etCfg;
        if (treeExpansionSetting == TreeExpansionSetting.Heuristic) {
            HeuristicTreeConfiguration.Configurator<Place, PlaceState, PlaceNode, DoubleScore> htCfg = new HeuristicTreeConfiguration.Configurator<>();
            htCfg.heuristic(bridgedHeuristics.getBridge().getBuilder());
            htCfg.heuristicExpansion(lod == SupervisionSetting.Full ? EventingHeuristicTreeExpansion::new : HeuristicTreeExpansion::new);
            htCfg.tree(lod == SupervisionSetting.Full ? EventingEnumeratingTree::new : EnumeratingTree::new);
            htCfg.childGenerationLogic(new MonotonousPlaceGenerationLogic.Builder());
            etCfg = htCfg;
        } else {
            etCfg = new EfficientTreeConfiguration.Configurator<>();
            etCfg.tree(lod == SupervisionSetting.Full ? EventingEnumeratingTree::new : EnumeratingTree::new);
            etCfg.expansionStrategy(treeExpansionSetting == TreeExpansionSetting.BFS ? VariableExpansion::bfs : VariableExpansion::dfs);
            etCfg.childGenerationLogic(new MonotonousPlaceGenerationLogic.Builder());
        }

        // ** Post Processing ** //

        PostProcessingConfiguration.Configurator<PetriNet, PetriNet> configurator = new PostProcessingConfiguration.Configurator<>(IdentityPostProcessor::new);
        for (FrameworkBridge.BridgedPostProcessors bridgedPostProcessors : pipeline) {
            FrameworkBridge.BridgedPostProcessor next = bridgedPostProcessors.getBridge();
            configurator.processor((SimpleBuilder) next.getBuilder());
        }
        PostProcessingConfiguration.Configurator<PetriNet, ProMPetrinetWrapper> ppCfg = configurator.processor(ProMConverter::new);


        // ** PARAMETERS ** //

        ProvidesParameters parameters = makeAlgorithmParameterClass(tau, delta, depth, permitWiring, lod != SupervisionSetting.Lightweight);
        AdaptedAlgorithmParameterConfig parCfg = new AdaptedAlgorithmParameterConfig(parameters);

        return new ConfigsCollection(svCfg, pcCfg, evCfg, etCfg, ppCfg, parCfg);
    }

    private ProvidesParameters makeAlgorithmParameterClass(double tau, double delta, int maxDepth, boolean permitWiring, boolean shouldInstrument) {

        class CustomParameters extends AbstractGlobalComponentSystemUser implements ProvidesParameters {
            public CustomParameters() {
                globalComponentSystem().provide(ParameterRequirements.SUPERVISION_PARAMETERS.fulfilWith(StaticDataSource.of(shouldInstrument ? SupervisionParameters.instrumentAll(false) : SupervisionParameters.instrumentNone(false))))
                                       .provide(ParameterRequirements.TAU_FITNESS_THRESHOLDS.fulfilWith(StaticDataSource.of(TauFitnessThresholds.tau(tau))))
                                       .provide(ParameterRequirements.PLACE_GENERATOR_PARAMETERS.fulfilWith(StaticDataSource.of(new PlaceGeneratorParameters(maxDepth, true, permitWiring, false, false))));

                globalComponentSystem().provide(ParameterRequirements.DELTA_PARAMETERS.fulfilWith(StaticDataSource.of(new DeltaParameters(delta))));
            }
        }

        return new CustomParameters();
    }

    private void tryRun() {
        Object basicConfig = collectConfig();
        if (basicConfig != null) controller.basicConfigCompleted(basicConfig);
    }

    private boolean updateValidityHighlight(JTextField field, Predicate<JTextField> predicate) {
        boolean test = predicate.test(field);
        field.setBackground(test ? null : ColorScheme.lightPink);
        return test;
    }

    private void updatedParameters() {

    }

    private void updatedCompositionSettings() {
        deltaAdaptationLabeledComboBox.setVisible(compositionVariantComboBox.getSelectedItem() == CompositionStrategy.TauDelta);
        labeledDelta.setVisible(compositionVariantComboBox.getSelectedItem() == CompositionStrategy.TauDelta);
    }

    private void updatedEvaluationSettings() {

    }

    private void updatedProposalSettings() {
        bridgedHeuristicsLabeledComboBox.setVisible(enumerationStrategyComboBox.getSelectedItem() == TreeExpansionSetting.Heuristic);
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
