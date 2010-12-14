/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.ontologyimportsgraph.ui;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.jpowergraph.DefaultGraph;
import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.layout.Layouter;
import net.sourceforge.jpowergraph.layout.SpringLayoutStrategy;
import net.sourceforge.jpowergraph.lens.CursorLens;
import net.sourceforge.jpowergraph.lens.LegendLens;
import net.sourceforge.jpowergraph.lens.LensSet;
import net.sourceforge.jpowergraph.lens.NodeSizeLens;
import net.sourceforge.jpowergraph.lens.RotateLens;
import net.sourceforge.jpowergraph.lens.TooltipLens;
import net.sourceforge.jpowergraph.lens.TranslateLens;
import net.sourceforge.jpowergraph.lens.ZoomLens;
import net.sourceforge.jpowergraph.manipulator.contextandtooltip.ContextMenuAndToolTipManipulator;
import net.sourceforge.jpowergraph.manipulator.contextandtooltip.DefaultToolTipListener;
import net.sourceforge.jpowergraph.manipulator.dragging.DraggingManipulator;
import net.sourceforge.jpowergraph.manipulator.selection.DefaultNodeSelectionModel;
import net.sourceforge.jpowergraph.manipulator.selection.SelectionManipulator;
import net.sourceforge.jpowergraph.painters.LineEdgePainter;
import net.sourceforge.jpowergraph.pane.JGraphScrollPane;
import net.sourceforge.jpowergraph.viewcontrols.RotateControlPanel;
import net.sourceforge.jpowergraph.viewcontrols.ZoomControlPanel;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.Section;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.navigator.ITreeDataProvider;
import org.neontoolkit.gui.navigator.MTreeView;
import org.neontoolkit.gui.navigator.TreeProviderManager;
import org.neontoolkit.gui.navigator.elements.AbstractOntologyTreeElement;
import org.neontoolkit.jpowergraph.history.NavigationHistory;
import org.neontoolkit.jpowergraph.manipulator.contextandtooltip.OntoPowerGraphContextMenuListener;
import org.neontoolkit.ontovisualize.DefaultNodeContentProvider;
import org.neontoolkit.ontovisualize.OntovisualizePlugin;
import org.neontoolkit.ontovisualize.edges.OntoStudioDefaultEdge;
import org.neontoolkit.ontovisualize.nodes.LabelImageNode;
import org.neontoolkit.ontovisualize.painters.OntoVisualizerGraphPane;

import com.ontoprise.ontostudio.owl.gui.navigator.ontology.OntologyProvider;
import com.ontoprise.ontostudio.owl.gui.navigator.ontology.OntologyTreeElement;
import com.ontoprise.ontostudio.owl.gui.ontologyimportsgraph.Messages;
import com.ontoprise.ontostudio.owl.gui.properties.AbstractOWLIdPropertyPage;
import com.ontoprise.ontostudio.owl.visualize.nodes.OntologyNode;

/* 
 * Created on: 26.02.2009
 * Created by: Werner Hihn
 *
 * Keywords: UI, EntityPropertyPage, Module, Ontology, Imports
 */
/**
 * PropertyPage that displays the properties of ontologies in the EntityProperty view.
 */

public class OntologyImportsGraphPropertyPage extends AbstractOWLIdPropertyPage {

    private static final String ONTOLOGY_LANGUAGE_DUMMY = "OWLOntologyImports"; //$NON-NLS-1$
    private DefaultGraph _graph;
    private OntoVisualizerGraphPane _graphPane;
    private Layouter _layouter;

    private IPropertyChangeListener _listener;

    private NavigationHistory _history;
    private Point _clickPoint = new Point(0, 0);
    private CursorLens _cursorLens;
    private ZoomLens _zoomLens;
    private boolean _handCursorOn;

    @Override
    public Composite createContents(Composite theParent) {
        Composite comp = prepareForm(theParent);
        
        Section section = _toolkit.createSection(comp, Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        section.setText(Messages.OntologyImportsGraphPropertyPage_0);
        section.setDescription(Messages.OntologyImportsGraphPropertyPage_1);
        section.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                _form.reflow(true);
            }
        });
        comp.setLayout(new GridLayout());
        
        Composite parent = _toolkit.createComposite(section, SWT.NONE);
        FillLayout layout = new FillLayout();
        layout.type = SWT.VERTICAL;
        parent.setLayout(layout);
        GridData data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        data.verticalAlignment = GridData.FILL;
        data.grabExcessVerticalSpace = true;
        section.setLayoutData(data);
        section.setClient(parent);

        Display display = parent.getDisplay();

        _graph = new DefaultGraph();
        _graphPane = new OntoVisualizerGraphPane(parent, _graph, null);
        data = new GridData();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        data.verticalAlignment = GridData.FILL;
        data.grabExcessVerticalSpace = true;
        _graphPane.getParent().setLayoutData(data);
        _layouter = new Layouter(new SpringLayoutStrategy(_graph));

        TranslateLens m_translateLens = new TranslateLens();
        _zoomLens = new ZoomLens();
        RotateLens m_rotateLens = new RotateLens();
        _cursorLens = new CursorLens(display);
        TooltipLens m_tooltipLens = new TooltipLens();
        LegendLens m_legendLens = new LegendLens();
        m_legendLens.setShowLegend(false);
        NodeSizeLens m_nodeSizelens = new NodeSizeLens();
        LensSet lensSet = new LensSet();
        lensSet.addLens(m_translateLens);
        lensSet.addLens(_zoomLens);
        lensSet.addLens(m_rotateLens);
        lensSet.addLens(_cursorLens);
        lensSet.addLens(m_tooltipLens);
        lensSet.addLens(m_legendLens);
        lensSet.addLens(m_nodeSizelens);
        _graphPane.setLens(lensSet);

        _graphPane.addManipulator(new SelectionManipulator(display, new DefaultNodeSelectionModel(_graph), SWT.NONE, SWT.CTRL));
        _graphPane.addManipulator(new DraggingManipulator(_cursorLens, SWT.NONE));

        Color gray = display.getSystemColor(SWT.COLOR_DARK_GRAY);

        _graphPane.setDefaultEdgePainter(new LineEdgePainter(gray, gray, gray));
        _graphPane.setAntialias(true);
        _graphPane.addManipulator(new ContextMenuAndToolTipManipulator(_graphPane,
                new OntoPowerGraphContextMenuListener(_layouter, _graph, m_legendLens, _zoomLens, ZoomControlPanel.DEFAULT_ZOOM_LEVELS, m_rotateLens, RotateControlPanel.DEFAULT_ROTATE_ANGLES),
                new DefaultToolTipListener(),
                m_tooltipLens));

        JGraphScrollPane scroll = new JGraphScrollPane(parent, _graphPane, m_translateLens);
        scroll.setParent(parent);

        OntovisualizePlugin.getDefault().getVisualizerConfigurator(ONTOLOGY_LANGUAGE_DUMMY).setNodePainters(_graphPane);
        addListeners();
        _layouter.start();
        return parent;
    }

    private void addListeners() {
        _listener = new IPropertyChangeListener() {

            // Listens to the events that change the namespace and update visualizer
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty().equals(NeOnUIPlugin.ID_DISPLAY_PREFERENCE)) {
                    if (!_graphPane.isDisposed()) {
                        _graph.notifyUpdated();
                    }
                }
            }
        };
        NeOnUIPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(_listener);
        /*
         * KeyListener for shortcuts: Alt & Left (Back) Alt & Right (Forth) Ctrl & + (Zoom in) Ctrl & - (Zoom out)
         */
        _graphPane.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == SWT.ARROW_LEFT && e.stateMask == SWT.ALT) {
                    back();
                } else if (e.keyCode == SWT.ARROW_RIGHT && e.stateMask == SWT.ALT) {
                    forth();
                } else if (e.keyCode == SWT.KEYPAD_ADD && e.stateMask == SWT.CTRL || e.keyCode == 43 && e.stateMask == SWT.CTRL) {
                    zoomIn();
                } else if (e.keyCode == SWT.KEYPAD_SUBTRACT && e.stateMask == SWT.CTRL || e.keyCode == 45 && e.stateMask == SWT.CTRL) {
                    zoomOut();
                }
            }

        });
        _graphPane.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent e) {
                // mouse click
                super.mouseUp(e);
                final Point p = new Point(e.x, e.y);
                if (p.x == _clickPoint.x && p.y == _clickPoint.y) {
                    // navigation:
                    _cursorLens.setCursor(new Cursor(null, SWT.CURSOR_WAIT));
                    LabelImageNode rootNode = (LabelImageNode) ((OntoVisualizerGraphPane) e.getSource()).getNodeAtPoint(p);
                    if (rootNode != null && rootNode.canBeNavigatedTo()) {
                        if (e.button == 2 || e.button == 3) {
                            // right click - don' t focus on selected node, only append its child nodes
                            appendNodes(rootNode);
                        } else {
                            goTo(rootNode);
                        }
                    }
                    _cursorLens.setCursor(null);
                }
            }

            @Override
            public void mouseDown(MouseEvent e) {
                super.mouseDown(e);
                _clickPoint = new Point(e.x, e.y);
            }

        });

        /*
         * MouseWheel Listener, needed for Zoom in/out if Ctrl is pressed.
         */
        _graphPane.addListener(SWT.MouseWheel, new Listener() {
            public void handleEvent(Event event) {
                if (event.stateMask == SWT.CTRL) {
                    int c = event.count;
                    c = (int) Math.ceil(c / 3.0f);
                    while (c < 0) {
                        zoomOut();
                        c++;
                    }

                    while (c > 0) {
                        zoomIn();
                        c--;
                    }
                }
            }
        });

        /*
         * MouseMove Listener, changes Cursor when moving the mouse over a node the user can navigate to.
         */
        _graphPane.addListener(SWT.MouseMove, new Listener() {

            public void handleEvent(Event e) {
                Point p = new Point(e.x, e.y);
                LabelImageNode rootNode = (LabelImageNode) ((OntoVisualizerGraphPane) e.widget).getNodeAtPoint(p);
                if (rootNode == null) {
                    _cursorLens.setCursor(null);
                    return;
                }
                Shell shell = OntovisualizePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
                if (rootNode.canBeNavigatedTo()) {
                    _handCursorOn = true;
                    _cursorLens.setCursor(new Cursor(e.display, SWT.CURSOR_HAND));
                } else {
                    if (shell != null && _handCursorOn) {
                        _handCursorOn = false;
                        _cursorLens.setCursor(null);
                    }
                }
            }

        });
    }

    @Override
    public void refresh() {
        super.refresh();
        if (_ontologyUri != null) {
            goTo(_ontologyUri);
        }
    }

    private void goTo(String moduleId) {
        ITreeDataProvider _provider = TreeProviderManager.getDefault().getProvider(MTreeView.ID, OntologyProvider.class);
        OntologyTreeElement rootElement = new OntologyTreeElement(_project, moduleId, _provider);
        goTo(rootElement);
    }

    private void goTo(Object rootElement) {
        DefaultNodeContentProvider contentProvider = new DefaultNodeContentProvider(ONTOLOGY_LANGUAGE_DUMMY, -1);
        List<LabelImageNode> nodeList = new ArrayList<LabelImageNode>();
        List<OntoStudioDefaultEdge> edgeList = new ArrayList<OntoStudioDefaultEdge>();
        if (rootElement instanceof LabelImageNode) {
            getHistory().addEntry((Node) rootElement);
            nodeList.addAll(contentProvider.getNodes((LabelImageNode) rootElement));
            edgeList.addAll(contentProvider.getEdges((LabelImageNode) rootElement));
        } else if (rootElement instanceof AbstractOntologyTreeElement) {
            AbstractOntologyTreeElement element = (AbstractOntologyTreeElement) rootElement;
            getHistory().addEntry(new OntologyNode(((OntologyTreeElement) element).getId(), element.getProjectName()));
            nodeList.addAll(contentProvider.getNodes(element));
            edgeList.addAll(contentProvider.getEdges(element));
        }
        _graph.clear();
        _graph.addElements(nodeList, edgeList);
        _graph.notifyLayoutUpdated();
        _layouter.start();
    }

    @Override
    public void update() {
        super.update();
        if (_ontologyUri != null) {
            goTo(_ontologyUri);
        }
    }

    public void appendNodes(Node node) {
        // get nodes and edges of ContentProviders
        DefaultNodeContentProvider provider = new DefaultNodeContentProvider(ONTOLOGY_LANGUAGE_DUMMY, -1);
        List<LabelImageNode> nodeList = provider.getNodes((LabelImageNode) node);
        List<OntoStudioDefaultEdge> edgeList = provider.getEdges((LabelImageNode) node);
        node.setFixed(true);
        _graph.addElements(nodeList, edgeList);
        _graph.notifyLayoutUpdated();
        _layouter.start();
    }

    public void zoomIn() {
        _zoomLens.setZoomFactor(_zoomLens.getZoomFactor() + 0.25);
    }

    public void zoomOut() {
        _zoomLens.setZoomFactor(_zoomLens.getZoomFactor() - 0.25);
    }

    private NavigationHistory getHistory() {
        if (_history == null) {
            _history = new NavigationHistory();
        }
        return _history;
    }

    /**
     * Navigates to the last element of navigation history.
     */
    public void back() {
        _cursorLens.setCursor(new Cursor(null, SWT.CURSOR_WAIT));
        LabelImageNode node = (LabelImageNode) getHistory().backPerformed();
        goTo(node);
        _cursorLens.setCursor(null);
    }

    /**
     * Navigates back to the lastly removed element of navigation history.
     */
    public void forth() {
        _cursorLens.setCursor(new Cursor(null, SWT.CURSOR_WAIT));
        LabelImageNode node = (LabelImageNode) getHistory().forthPerformed();
        if (node != null) {
            goTo(node);
        }
        _cursorLens.setCursor(null);
    }

    @Override
    protected List<Section> getSections() {
        return null;
    }

}
