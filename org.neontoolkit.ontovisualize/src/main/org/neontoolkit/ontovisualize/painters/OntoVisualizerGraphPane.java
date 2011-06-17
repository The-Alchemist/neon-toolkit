/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.ontovisualize.painters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.jpowergraph.DefaultGraph;
import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.Graph;
import net.sourceforge.jpowergraph.Legend;
import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.manipulator.Manipulator;
import net.sourceforge.jpowergraph.painters.LegendPainter;
import net.sourceforge.jpowergraph.pane.JGraphPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.neontoolkit.jpowergraph.history.NavigationHistory;
import org.neontoolkit.jpowergraph.painters.NavigationHistoryPainter;
import org.neontoolkit.jpowergraph.viewcontrols.INavigationListener;


/*
 * Created by Werner Hihn
 */
/**
 * @author Nico Stieler
 */

public class OntoVisualizerGraphPane extends JGraphPane {

    private NavigationHistoryPainter _navigationHistoryPainter;
    private NavigationHistory _navigationHistory;
    private ArrayList<Manipulator> _manipulators;
    private Map<String,Manipulator> _manipulatorsByName;
    @SuppressWarnings("rawtypes")
    private ArrayList<Class> _hiddenNodeTypes = new ArrayList<Class>();
    private OntoVisualizerLegendPainter _legendPainter;
    private static int _gdeEnabled = -1;

    public OntoVisualizerGraphPane(Composite theParent, Graph graph, DefaultGraph navigationGraph) {
        super(theParent, graph);
        _manipulators = new ArrayList<Manipulator>();
        _manipulatorsByName = new HashMap<String,Manipulator>();
        _legendPainter = new OntoVisualizerLegendPainter(theParent.getDisplay());
        _navigationHistoryPainter = new OntoVisualizerNavigationHistoryPainter(getDisplay());
    }

    public void setNavigationHistory(NavigationHistory history) {
        _navigationHistory = history;
    }
    
    public void setOntologyLanguage(String ontologyLanguage) {
        _legendPainter.setOntologyLanguage(ontologyLanguage);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void addFilteredNode(Class clazz) {
        _hiddenNodeTypes.add(clazz);
    }

    @Override
    public void clearNodeFilter() {
        _hiddenNodeTypes.clear();
    }

    public void addNavigationListener(INavigationListener listener) {
        _navigationHistoryPainter.addNavigationListener(listener);
    }

    /**
     * Adds a manipulator to this pane. If a manipulator with the same name if registered, if is rist removed.
     * 
     * @param manipulator the manipulator
     */
    @SuppressWarnings("unchecked")
    @Override
    public void addManipulator(Manipulator manipulator) {
        removeManipulator(manipulator.getName());
        _manipulators.add(manipulator);
        m_manipulatorsByName.put(manipulator.getName(), manipulator);
        manipulator.setGraphPane(this);
        this.addMouseListener(manipulator);
        this.addMouseMoveListener(manipulator);
        this.addMouseTrackListener(manipulator);
        this.addKeyListener(manipulator);
        this.addFocusListener(manipulator);
    }

    /**
     * Returns a manipulator with given name.
     * 
     * @param name the name of the manpulator
     * @return the manipulator with given name (or <code>null</code> if the manipualtor with given name is not registered)
     */
    @Override
    public Manipulator getManipulator(String name) {
        return _manipulatorsByName.get(name);
    }

    /**
     * Removes a manipulator with given name.
     * 
     * @param name the name of the manpulator
     */
    @Override
    public void removeManipulator(String name) {
        Manipulator manipulator = _manipulatorsByName.remove(name);
        if (manipulator != null) {
            _manipulators.remove(manipulator);
            manipulator.setGraphPane(null);
            this.removeMouseListener(manipulator);
            this.removeMouseMoveListener(manipulator);
            this.removeMouseTrackListener(manipulator);
            this.removeKeyListener(manipulator);
            this.removeFocusListener(manipulator);
        }
    }

    @Override
    public LegendPainter getPainterForLegend(Legend theLegend) {
        return _legendPainter;
    }

    /**
     * Updates the component.
     * 
     * @param g the graphics
     */
    @Override
    public void paintControl(PaintEvent e) {
        Image bufferImage = new Image(getParent().getDisplay(), this.getSize().x, this.getSize().y);
        GC g = new GC(bufferImage);
        if (isAntialias()) {
            if (isGDEEnabled()) {
                g.setAntialias(SWT.ON);
            }
        }
        Rectangle clipRectangle = g.getClipping();
        if (getGraph() != null) {
            synchronized (getGraph()) {
                Rectangle bounds = new Rectangle(0, 0, 0, 0);
                Iterator<?> iterator = getGraph().getEdges().iterator();
                while (iterator.hasNext()) {
                    Edge edge = (Edge) iterator.next();
                    if (!_hiddenNodeTypes.contains(edge.getFrom().getClass()) && !_hiddenNodeTypes.contains(edge.getTo().getClass())) {
                        getEdgeScreenBounds(edge, bounds);
                        if (clipRectangle.intersects(bounds)) {
                            paintEdge(g, edge);
                        }
                    }
                }
                iterator = getGraph().getNodes().iterator();
                while (iterator.hasNext()) {
                    Node node = (Node) iterator.next();
                    if (!_hiddenNodeTypes.contains(node.getClass())) {
                        getNodeScreenBounds(node, bounds);
                        if (clipRectangle.intersects(bounds)) {
                            paintNode(g, node);
                        }
                    }
                }
                for (int i = 0; i < _manipulators.size(); i++) {
                    _manipulators.get(i).paint(g);
                }
                // legend
                paintLegend(g, m_legend);
                
                // navigation history
                Rectangle r = paintNavigationHistory(g, _navigationHistory);
                if (_navigationHistory != null) {
                    _navigationHistory.setRectangle(r);
                }
            }
        }
        e.gc.drawImage(bufferImage, 0, 0);
        bufferImage.dispose();
    }

    protected Rectangle paintNavigationHistory(GC g, NavigationHistory history) {
        if (history != null && history.isDrawHistory()) {
            return _navigationHistoryPainter.paintNavigationHistory(this, g, history);
        }
        return new Rectangle(0, 0, 0, 0);
    }

    public static boolean isGDEEnabled() {
        if (_gdeEnabled == -1) {
            GC gc = new GC(Display.getCurrent());
            gc.setAdvanced(true);
            if (gc.getAdvanced()) {
                _gdeEnabled = 1;
            } else {
                _gdeEnabled = 0;
            }
            gc.dispose();
        }
        if (_gdeEnabled == 1) {
            return true;
        } else {
            return false;
        }
    }

}
