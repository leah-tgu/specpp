package org.processmining.specpp.prom.mvc.config;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.specpp.prom.alg.FrameworkBridge;
import org.processmining.specpp.prom.alg.ProMPostProcessor;
import org.processmining.specpp.prom.mvc.swing.MyListModel;
import org.processmining.specpp.prom.mvc.swing.SwingFactory;
import org.processmining.specpp.prom.util.AnnotatedPostProcessorTransferable;
import org.processmining.specpp.prom.util.Iconic;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class PostProcessingConfigPanel extends JPanel {

    private final MyListModel<FrameworkBridge.AnnotatedPostProcessor> availablePostProcessors;

    public PostProcessingConfigPanel(PluginContext pc, MyListModel<FrameworkBridge.AnnotatedPostProcessor> ppPipelineModel) {
        super(new GridBagLayout());

        availablePostProcessors = new MyListModel<>(FrameworkBridge.POST_PROCESSORS);
        JList<FrameworkBridge.AnnotatedPostProcessor> outList = new JList<>(availablePostProcessors);
        outList.setDragEnabled(true);
        outList.setDropMode(DropMode.INSERT);
        outList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        outList.setTransferHandler(new TransferHandler() {
            @Override
            protected Transferable createTransferable(JComponent c) {
                if (outList.getSelectedValue() == null) return null;
                else return new AnnotatedPostProcessorTransferable(outList.getSelectedValue());
            }

            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDrop() && support.isDataFlavorSupported(AnnotatedPostProcessorTransferable.myFlave);
            }

            @Override
            public boolean importData(TransferSupport support) {
                return canImport(support);
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
        ppc.weightx = 1;
        ppc.weighty = 0;
        ppc.anchor = GridBagConstraints.WEST;
        add(SwingFactory.createHeader("Available Post Processors"), ppc);
        ppc.fill = GridBagConstraints.BOTH;
        ppc.weighty = 1;
        ppc.gridy++;
        JScrollPane outListScrollPane = new JScrollPane(outList);
        outListScrollPane.setMaximumSize(new Dimension(500, 300));
        outListScrollPane.setPreferredSize(new Dimension(500, 300));
        add(outListScrollPane, ppc);
        ppc.fill = GridBagConstraints.NONE;

        JList<FrameworkBridge.AnnotatedPostProcessor> inList = new JList<>(ppPipelineModel);
        inList.setDragEnabled(true);
        inList.setDropMode(DropMode.INSERT);
        inList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inList.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    int i = inList.getSelectedIndex();
                    if (i >= 0) ppPipelineModel.remove(i);
                }
            }

        });

        MouseAdapter ml = new MouseAdapter() {

            public void popIt(MouseEvent e) {
                int i = inList.locationToIndex(e.getPoint());
                if (i < 0) return;
                boolean b = inList.getCellBounds(i, i).contains(e.getPoint());
                if (b) {
                    JPopupMenu jpm = new JPopupMenu() {{
                        JMenuItem remove = new JMenuItem("remove");
                        remove.addActionListener(e -> ppPipelineModel.remove(i));
                        add(remove);
                    }};
                    jpm.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) popIt(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) popIt(e);
            }
        };
        inList.addMouseListener(ml);
        inList.setTransferHandler(new TransferHandler() {

            private int importedIndex = -1;
            private int exportedIndex = -1;

            @Override
            protected Transferable createTransferable(JComponent c) {
                Object selectedValue = inList.getSelectedValue();
                exportedIndex = inList.getSelectedIndex();
                return new AnnotatedPostProcessorTransferable((FrameworkBridge.AnnotatedPostProcessor) selectedValue);
            }

            @Override
            public int getSourceActions(JComponent c) {
                return MOVE;
            }

            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDrop() && support.isDataFlavorSupported(AnnotatedPostProcessorTransferable.myFlave);
            }

            @Override
            protected void exportDone(JComponent source, Transferable data, int action) {
                if (action == MOVE) {
                    if (importedIndex < 0) ppPipelineModel.remove(exportedIndex);
                    else ppPipelineModel.remove(importedIndex < exportedIndex ? exportedIndex + 1 : exportedIndex);
                    exportedIndex = -1;
                    importedIndex = -1;
                }
            }

            @Override
            public boolean importData(TransferSupport support) {
                try {
                    FrameworkBridge.AnnotatedPostProcessor transferData = (FrameworkBridge.AnnotatedPostProcessor) support.getTransferable()
                                                                                                                          .getTransferData(AnnotatedPostProcessorTransferable.myFlave);
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
        ppc.anchor = GridBagConstraints.CENTER;
        ppc.weightx = 0;
        ppc.weighty = 0;
        add(new JLabel(Iconic.tiny_right_arrow), ppc);
        ppc.gridheight = 2;
        ppc.gridy++;
        add(new JLabel(Iconic.tiny_hand), ppc);
        ppc.weightx = 1;
        ppc.gridheight = 1;
        ppc.gridx = 2;
        ppc.gridy = 0;
        ppc.anchor = GridBagConstraints.WEST;
        add(SwingFactory.createHeader("Post Processing Pipeline"), ppc);
        ppc.weighty = 1;
        ppc.gridy++;
        ppc.fill = GridBagConstraints.BOTH;
        JScrollPane inListScrollPane = new JScrollPane(inList);
        inListScrollPane.setMaximumSize(new Dimension(500, 300));
        inListScrollPane.setPreferredSize(new Dimension(500, 300));
        add(inListScrollPane, ppc);
        JButton importPostProcessorButton = SlickerFactory.instance().createButton("import from ProM");
        importPostProcessorButton.addActionListener(e -> ProMPostProcessor.createPluginFinderWindow(pc, this::importAnnotatedPostProcessor));
        ppc.fill = GridBagConstraints.NONE;
        ppc.weightx = 0;
        ppc.weighty = 0.1;
        ppc.gridy = 2;
        ppc.gridx = 0;
        ppc.gridwidth = 1;
        add(importPostProcessorButton, ppc);
        JLabel ppTypesOkay = SlickerFactory.instance().createLabel("are types ok?");
        ppc.gridx = 2;
        add(ppTypesOkay, ppc);
        ppPipelineModel.addListDataListener(new ListDataListener() {

            private void updateValidationStatus() {
                if (ConfigurationPanel.validatePostProcessingPipeline(ppPipelineModel)) {
                    ppTypesOkay.setText("input, intermediate & output types match \"[PetriNet => ProMPetrinetWrapper]\"");
                    ppTypesOkay.setIcon(Iconic.checkmark);
                } else {
                    ppTypesOkay.setText("input & output types are incompatible \"[PetriNet =/> ProMPetrinetWrapper]\"");
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

    }

    public void importAnnotatedPostProcessor(FrameworkBridge.AnnotatedPostProcessor annotatedPostProcessor) {
        if (annotatedPostProcessor == null) return;
        availablePostProcessors.append(annotatedPostProcessor);
    }

}
