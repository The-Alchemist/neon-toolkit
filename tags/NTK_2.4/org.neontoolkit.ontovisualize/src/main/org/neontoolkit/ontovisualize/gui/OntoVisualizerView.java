/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.ontovisualize.gui;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import net.sourceforge.jpowergraph.DefaultGraph;
import net.sourceforge.jpowergraph.Legend;
import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.layout.Layouter;
import net.sourceforge.jpowergraph.layout.SpringLayoutStrategy;
import net.sourceforge.jpowergraph.lens.CursorLens;
import net.sourceforge.jpowergraph.lens.LegendLens;
import net.sourceforge.jpowergraph.lens.LensSet;
import net.sourceforge.jpowergraph.lens.NodeFilterLens;
import net.sourceforge.jpowergraph.lens.RotateLens;
import net.sourceforge.jpowergraph.lens.TooltipLens;
import net.sourceforge.jpowergraph.lens.TranslateLens;
import net.sourceforge.jpowergraph.lens.ZoomLens;
import net.sourceforge.jpowergraph.manipulator.contextandtooltip.ContextMenuAndToolTipManipulator;
import net.sourceforge.jpowergraph.manipulator.contextandtooltip.DefaultToolTipListener;
import net.sourceforge.jpowergraph.manipulator.dragging.DraggingManipulator;
import net.sourceforge.jpowergraph.painters.LineEdgePainter;
import net.sourceforge.jpowergraph.viewcontrols.RotateControlPanel;
import net.sourceforge.jpowergraph.viewcontrols.ZoomControlPanel;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.neontoolkit.gui.IHelpContextIds;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.navigator.elements.AbstractOntologyEntity;
import org.neontoolkit.gui.navigator.elements.AbstractOntologyTreeElement;
import org.neontoolkit.jpowergraph.history.NavigationHistory;
import org.neontoolkit.jpowergraph.manipulator.contextandtooltip.OntoPowerGraphContextMenuListener;
import org.neontoolkit.jpowergraph.pane.OntoVisualizerViewPane;
import org.neontoolkit.jpowergraph.viewcontrols.INavigationListener;
import org.neontoolkit.ontovisualize.DefaultNodeContentProvider;
import org.neontoolkit.ontovisualize.Messages;
import org.neontoolkit.ontovisualize.OntovisualizePlugin;
import org.neontoolkit.ontovisualize.actions.AbstractShowInVisualizerAction;
import org.neontoolkit.ontovisualize.actions.AbstractVisualizeOntologyAction;
import org.neontoolkit.ontovisualize.edges.OntoStudioDefaultEdge;
import org.neontoolkit.ontovisualize.nodes.LabelImageNode;
import org.neontoolkit.ontovisualize.painters.OntoVisualizerGraphPane;
import org.neontoolkit.ontovisualize.preferences.OntoVisualizerPreferencePage;


/*
 * Created by Werner Hihn
 */

public class OntoVisualizerView extends ViewPart implements INavigationListener {

    /**
     * The id of this viewer.
     */
    public static final String ID = "org.neontoolkit.ontovisualize2.OntoVisualizerView2"; //$NON-NLS-1$

    private Color _gray;
    private DefaultGraph _graph;
    private DefaultGraph _navigationGraph;
    private OntoVisualizerGraphPane _graphPane;
    private Layouter _layouter;
    private ZoomLens _zoomLens;
    private TranslateLens _translateLens;
    private RotateLens _rotateLens;
    private TooltipLens _tooltipLens;
    private LegendLens _legendLens;
    private CursorLens _cursorLens;
    private LabelImageNode _rootNode;
    private NavigationHistory _history;
    private OntoVisualizerViewPane _viewPane;

    private IPropertyChangeListener _listener;
    private IPreferenceStore _store;

    private boolean _handCursorOn = false;

    private Point _clickPoint = new Point(0, 0);
    private int _hierarchyLevel = 0;
    private String _ontologyLanguage;
    private String _projectId;
    private String _ontologyId;

    public OntoVisualizerView() {
        _store = OntovisualizePlugin.getDefault().getPreferenceStore();

        // read hierarchy level to visualize from PreferenceStore
        readHierarchyLevel();
    }

    /**
     * Called from {@link AbstractShowInVisualizerAction} and {@link AbstractVisualizeOntologyAction}. Visualizes an ontology starting at the passed
     * <code>element</code>.
     * 
     * @param rootLevel indicates wether the ontology is visualized on root level, i.e. the root node is the ontology node.
     * @param element The id of the node to jump to. If this is an empty String, we call updateGraph() and visualize at root level.
     * @param ontologyLanguage The ontology language
     */
    public void visualizeElement(boolean rootLevel, AbstractOntologyTreeElement element, String ontologyLanguage) {
        _ontologyLanguage = ontologyLanguage;
        _graphPane.setOntologyLanguage(ontologyLanguage);
        getHistory().clear();
        readHierarchyLevel();

        _projectId = element.getProjectName();
        _ontologyId = element.getOntologyUri();
        _cursorLens.setCursor(new Cursor(null, SWT.CURSOR_WAIT));

        // language specific configuration (extension point visualizerContext)
        setNodeFilterLens();
        setNodePainters();
        setEdgePainters();

        // get nodes and edges of ContentProviders
        DefaultNodeContentProvider provider = new DefaultNodeContentProvider(_ontologyLanguage, _hierarchyLevel);
        List<LabelImageNode> nodeList = provider.getNodes(element);
        List<OntoStudioDefaultEdge> edgeList = provider.getEdges(element);

        if (rootLevel) {
            _rootNode = (LabelImageNode) OntovisualizePlugin.getDefault().getVisualizerConfigurator(_ontologyLanguage).getOntologyNode(element);
            _ontologyId = _rootNode.getId();
            getHistory().addEntry(_rootNode);
        } else {
            LabelImageNode ontologyNode = (LabelImageNode) OntovisualizePlugin.getDefault().getVisualizerConfigurator(_ontologyLanguage).getOntologyNode(element);
//            LabelImageNode ontologyNode = new ModuleNode(_ontologyId, _projectId);
            getHistory().addEntry(ontologyNode);
            _rootNode = (LabelImageNode) OntovisualizePlugin.getDefault().getVisualizerConfigurator(_ontologyLanguage).getRootElementNode(((AbstractOntologyEntity) element).getId(), _ontologyId, _projectId);
            if (_rootNode != null) {
                getHistory().addEntry(_rootNode);
            }
        }
        getHistory().setOntologyId(_ontologyId);

        // this is where the graph is painted
        updateGraph(nodeList, edgeList);
        _cursorLens.setCursor(null);
    }

    private void setEdgePainters() {
        OntovisualizePlugin.getDefault().getVisualizerConfigurator(_ontologyLanguage).setEdgePainters(_graphPane);
        _graphPane.setDefaultEdgePainter(new LineEdgePainter(_gray, _gray, _gray));
    }

    private void setNodePainters() {
        OntovisualizePlugin.getDefault().getVisualizerConfigurator(_ontologyLanguage).setNodePainters(_graphPane);
    }

    @SuppressWarnings("unchecked")
    private void setNodeFilterLens() {
        ArrayList<Class> filterableNodes = OntovisualizePlugin.getDefault().getVisualizerConfigurator(_ontologyLanguage).getFilterableNodes();
        NodeFilterLens nodeFilterLens = new NodeFilterLens(filterableNodes);
        LensSet lensSet = getStandardLensSet();
        lensSet.addLens(nodeFilterLens);
        _graphPane.setLens(lensSet);
    }

    /**
     * This is where the graph is painted: datamodel is queried for the ontology content and it is displayed accordingly.
     * 
     */
    private void updateGraph(List<LabelImageNode> nodes, List<OntoStudioDefaultEdge> edges) {
        readHierarchyLevel();

        _graphPane.setNavigationHistory(getHistory());

        _graph.clear();
        _graph.addElements(nodes, edges);
        _graph.notifyLayoutUpdated();
        _layouter.start();
    }

    /**
     * Reads the hierarchy level to visualize from PreferenceStore. E.g. level=2 means 2 levels of concepts are visualized.
     * 
     */
    private void readHierarchyLevel() {
        String level = _store.getString(OntoVisualizerPreferencePage.DEFAULT_LEVEL);
        if (level != null && !level.equals("")) { //$NON-NLS-1$
            _hierarchyLevel = Integer.valueOf(level);
        } else {
            _hierarchyLevel = 0;
        }
    }

    private NavigationHistory getHistory() {
        if (_history == null) {
            _history = new NavigationHistory();
        }
        return _history;
    }

    /**
     * Directly jumps to the node with the passed ID
     */
    public void goTo(String nodeId) {
        String qualifiedId = OntovisualizePlugin.getDefault().getVisualizerConfigurator(_ontologyLanguage).expandId(nodeId.trim(), _ontologyId, _projectId);

        LabelImageNode node = (LabelImageNode) OntovisualizePlugin.getDefault().getVisualizerConfigurator(_ontologyLanguage).getRootElementNode(qualifiedId, _ontologyId, _projectId);
        if (node == null) {
            // display error message, no concept with this ID found.
            MessageDialog.openInformation(_graphPane.getShell(), Messages.OntoVisualizerPreferencePage_4, Messages.OntoVisualizerView2_28);
        } else {
            goTo(node);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.jpowergraph.viewcontrols.INavigationListener#goTo(net.sourceforge.jpowergraph.Node)
     */
    public void goTo(Node node) {
        refreshRootNode((LabelImageNode) node);
        updateBackForthButtons();
    }

    public void appendNodes(Node node) {
        // get nodes and edges of ContentProviders
        DefaultNodeContentProvider provider = new DefaultNodeContentProvider(_ontologyLanguage, _hierarchyLevel);
        List<LabelImageNode> nodeList = provider.getNodes((LabelImageNode) node);
        List<OntoStudioDefaultEdge> edgeList = provider.getEdges((LabelImageNode) node);
        node.setFixed(true);
        _graphPane.setNavigationHistory(getHistory());
        _graph.addElements(nodeList, edgeList);
        _graph.notifyLayoutUpdated();
        _layouter.start();
    }

    /**
     * Disable back / forth buttons if there are no elements in navigation history, else enable them.
     * 
     */
    private void updateBackForthButtons() {
        if (getHistory().sizeBack() > 1) {
            _viewPane.getControlPanel().activateBackButton(true);
        } else {
            _viewPane.getControlPanel().activateBackButton(false);
        }
        if (getHistory().sizeForth() > 0) {
            _viewPane.getControlPanel().activateForthButton(true);
        } else {
            _viewPane.getControlPanel().activateForthButton(false);
        }
    }

    /**
     * 
     * 1. Clear the graph<br>
     * 2. Call the dislayXY-methods to add the needed nodes<br>
     * 3. Call graph.addElements() with two collections containing the nodes and edges<br>
     */
    private void refreshRootNode(LabelImageNode node) {
        if (node == null) {
            return;
        }
        _cursorLens.setCursor(new Cursor(null, SWT.CURSOR_WAIT));

        // get nodes and edges of ContentProviders
        DefaultNodeContentProvider provider = new DefaultNodeContentProvider(_ontologyLanguage, _hierarchyLevel);
        List<LabelImageNode> nodeList = provider.getNodes(node);
        List<OntoStudioDefaultEdge> edgeList = provider.getEdges(node);

        getHistory().addEntry(node);

        // this is where the graph is painted
        updateGraph(nodeList, edgeList);
        _cursorLens.setCursor(null);
    }

    @Override
    public void createPartControl(Composite theParent) {
        PlatformUI.getWorkbench().getHelpSystem().setHelp(theParent, IHelpContextIds.VISUALIZER_VIEW);
        Composite composite = new Composite(theParent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.makeColumnsEqualWidth = false;
        composite.setLayout(layout);
        Display display = composite.getDisplay();

        _gray = display.getSystemColor(SWT.COLOR_DARK_GRAY);

        _zoomLens = new ZoomLens();
        _cursorLens = new CursorLens(display);
        _graph = new DefaultGraph();
        _navigationGraph = new DefaultGraph();
        _graphPane = new OntoVisualizerGraphPane(composite, _graph, _navigationGraph);
        // GridData layoutData = new GridData();
        // layoutData.grabExcessHorizontalSpace = true;
        // layoutData.grabExcessVerticalSpace = true;
        // layoutData.horizontalAlignment = GridData.FILL;
        // layoutData.verticalAlignment = GridData.FILL;
        // _graphPane.setLayoutData(layoutData);
        _layouter = new Layouter(new SpringLayoutStrategy(_graph));
        _translateLens = new TranslateLens();
        _rotateLens = new RotateLens();
        _tooltipLens = new TooltipLens();
        _legendLens = new LegendLens();
        LensSet lensSet = getStandardLensSet();
        _graphPane.setLens(lensSet);

        /*
         * Manipulators: 1. DraggingManipulator 2. ContextMenuAndToolTipManipulator
         */
        _graphPane.addManipulator(new DraggingManipulator(_cursorLens, SWT.NONE));
        _graphPane.addManipulator(new ContextMenuAndToolTipManipulator(_graphPane,
                new OntoPowerGraphContextMenuListener(_layouter, _graph, _legendLens, _zoomLens, ZoomControlPanel.DEFAULT_ZOOM_LEVELS, _rotateLens, RotateControlPanel.DEFAULT_ROTATE_ANGLES),
                new DefaultToolTipListener(),
                _tooltipLens));

        _graphPane.setAntialias(true);
        _layouter.start();

        _viewPane = new OntoVisualizerViewPane(composite, _graphPane, _translateLens, _zoomLens, _rotateLens, _cursorLens, _tooltipLens, _legendLens, null, getHistory());
        // _viewPane.setParent(composite);

        /*
         * Listeners: 1. KeyListener 2. Mouse (click) 3. MouseWheel (zoom) 4. MouseMove (change cursor) 5. PropertyChangeListener 6. NavigationListener
         */
        addListeners();

        updateBackForthButtons();
    }

    private LensSet getStandardLensSet() {
        LensSet lensSet = new LensSet();
        lensSet.addLens(_translateLens);
        lensSet.addLens(_rotateLens);
        lensSet.addLens(_tooltipLens);
        lensSet.addLens(_legendLens);
        lensSet.addLens(_zoomLens);
        lensSet.addLens(_cursorLens);
        return lensSet;
    }

    private void addListeners() {
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
        /*
         * MouseListener for: - navigation in the graph, - the navigation history, - enabling / disabling filters in legend.
         */
        _graphPane.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent e) {
                // mouse click
                super.mouseUp(e);
                final Point p = new Point(e.x, e.y);
                if (p.x == _clickPoint.x && p.y == _clickPoint.y) {
                    // navigation history:
                    if (getHistory().getHistoryArea().contains(e.x, e.y)) {
                        if (getHistory() != null) {
                            Action a = getHistory().getActionAtPoint(p);
                            if (a != null) {
                                a.actionPerformed(new ActionEvent(e.getSource(), 0, "do Action")); //$NON-NLS-1$
                            }
                        }
                    }

                    // enable / disable filters in legend:
                    Legend legend = _graphPane.getLegendAtPoint(p);
                    if (legend != null) {
                        Action a = legend.getActionAtPoint(p);
                        if (a != null) {
                            a.actionPerformed(null);
                        }
                    }
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
                if (getHistory().getHistoryArea().contains(e.x, e.y)) {
                    if (getHistory() != null) {
                        MouseTrackAdapter a = getHistory().getMouseTrackAdapterAtPoint(new Point(e.x, e.y));
                        Shell shell = e.display.getActiveShell();
                        if (a != null) {
                            if (shell != null) {
                                _handCursorOn = true;
                                _cursorLens.setCursor(new Cursor(e.display, SWT.CURSOR_HAND));
                            }
                        } else {
                            if (shell != null && _handCursorOn) {
                                _handCursorOn = false;
                                _cursorLens.setCursor(null);
                            }
                        }
                    }
                } else {
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
            }

        });
        _listener = new IPropertyChangeListener() {

            // Listens to the events that change the namespace and update visualizer
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty().equals(NeOnUIPlugin.ID_DISPLAY_PREFERENCE)) {
                    if (_rootNode != null) {
                        if (!_graphPane.isDisposed()) {
                            _graph.notifyUpdated();
                        }
                    }
                }
            }
        };
        _store.addPropertyChangeListener(_listener);
        NeOnUIPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(_listener);
        _viewPane.addNavigationListener(this);
        _graphPane.addNavigationListener(this);

    }

    /**
     * Navigates to the last element of navigation history.
     */
    public void back() {
        _cursorLens.setCursor(new Cursor(null, SWT.CURSOR_WAIT));
        LabelImageNode node = (LabelImageNode) getHistory().backPerformed();
        refreshRootNode(node);
        _cursorLens.setCursor(null);
    }

    /**
     * Navigates back to the lastly removed element of navigation history.
     */
    public void forth() {
        _cursorLens.setCursor(new Cursor(null, SWT.CURSOR_WAIT));
        LabelImageNode node = (LabelImageNode) getHistory().forthPerformed();
        if (node != null) {
            refreshRootNode(node);
        }
        _cursorLens.setCursor(null);
    }

    @Override
    public void dispose() {
        OntovisualizePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell().setCursor(new Cursor(null, SWT.CURSOR_WAIT));
        _layouter = null;
        _graph = null;
        _store.removePropertyChangeListener(_listener);
        // VIZ2 move to concrete plugins
        NeOnUIPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(_listener);
        OntovisualizePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell().setCursor(null);
    }

    /**
     * Need this, because a simple repaint doesn't work if the user wants to hide navigation history.
     */
    public void refresh() {
        refreshRootNode(_rootNode);
    }

    @Override
    public void setFocus() {
        _viewPane.setFocus();
    }

    public void zoomIn() {
        _zoomLens.setZoomFactor(_zoomLens.getZoomFactor() + 0.25);
    }

    public void zoomOut() {
        _zoomLens.setZoomFactor(_zoomLens.getZoomFactor() - 0.25);
    }

}
