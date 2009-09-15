/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.jpowergraph.history;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Action;

import net.sourceforge.jpowergraph.Node;

import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.neontoolkit.jpowergraph.nodes.OntoPowerGraphDefaultNode;
import org.neontoolkit.jpowergraph.util.GUIUtils;


/*
 * Created by Werner Hihn
 */

public class NavigationHistory {

    private static final int SHIFT_NONE = 0;
    private static final int SHIFT_LEFT = 1;
    private static final int SHIFT_RIGHT = 2;

    private String _ontologyId;
    private LinkedList<Node> _backHistoryList;
    private LinkedList<Node> _forthHistoryList;
    private HashMap<Rectangle,Action> _rectangleActionMap;
    private HashMap<Rectangle,MouseTrackAdapter> _rectangleMouseTrackAdapterMap;
    private Rectangle _historyArea = new Rectangle(0, 0, 0, 0);
    private boolean _drawHistory = true;
    private boolean _doIt = false;
    private int _shiftCode = SHIFT_NONE;
    private int _firstSelectedElement = 0;
    private int _lastSelectedElement = 0;

    public NavigationHistory() {
        _backHistoryList = new LinkedList<Node>();
        _forthHistoryList = new LinkedList<Node>();
        _rectangleActionMap = new HashMap<Rectangle,Action>();
        _rectangleMouseTrackAdapterMap = new HashMap<Rectangle,MouseTrackAdapter>();
    }

    public String getOntologyId() {
        return _ontologyId;
    }

    public void setOntologyId(String ontologyId) {
        _ontologyId = ontologyId;
    }

    public void clear() {
        _backHistoryList.clear();
        _forthHistoryList.clear();
        _shiftCode = SHIFT_NONE;
    }

    public void setDrawHistory(boolean draw) {
        _drawHistory = draw;
    }

    public boolean isDrawHistory() {
        return _drawHistory;
    }

    public void addEntry(Node node) {
        Node lastNode = new OntoPowerGraphDefaultNode();
        Node firstNode = new OntoPowerGraphDefaultNode();
        if (_backHistoryList.size() > 0) {
            lastNode = _backHistoryList.getLast();
            firstNode = _backHistoryList.getFirst();
        }
        if (!(lastNode.equals(node) || firstNode.equals(node))) {
            _backHistoryList.add(_backHistoryList.size(), node);
        }
        _shiftCode = SHIFT_NONE;
    }

    public void removeEntry(Node node) {
        boolean contains = true;
        while (contains) {
            if (_backHistoryList.contains(node)) {
                _backHistoryList.remove(node);
            } else {
                contains = false;
            }

        }
        contains = true;
        while (contains) {
            if (_forthHistoryList.contains(node)) {
                _forthHistoryList.remove(node);
            } else {
                contains = false;
            }
        }
    }

    public Node getEntry(int index) {
        return _backHistoryList.get(index);
    }

    /**
     * Returns last visited node. Removes the last added node from _backHistoryList and adds it to _forthHistoryList.
     * 
     * @return
     */
    public Node backPerformed() {
        if (_backHistoryList.size() == 1) { // this means there is only the ontology left
            return _backHistoryList.getFirst();
        }
        int index = _backHistoryList.size() - 2;
        if (index < 0) {
            return null;
        }
        Node n = _backHistoryList.get(index);
        Node removeNode = _backHistoryList.get(index + 1);
        _backHistoryList.remove(index + 1);
        _forthHistoryList.add(removeNode);
        return n;
    }

    public Iterator<Node> backIterator() {
        return _backHistoryList.iterator();
    }

    public Iterator<Node> forthIterator() {
        return _forthHistoryList.iterator();
    }

    public boolean isEmpty() {
        return _backHistoryList.size() == 0;
    }

    public Node getFirst() {
        return _backHistoryList.getFirst();
    }

    public Node getLast() {
        if (_backHistoryList.isEmpty()) {
            return null;
        }
        return _backHistoryList.getLast();
    }

    public List<Node> getVisibleNodes(int maxWidth, GC g) {
        List<Node> localList = _backHistoryList;
        int firstLocal = _firstSelectedElement;
        if (_shiftCode == SHIFT_LEFT) {
            List<Node> sublist = null;
            if (_doIt) {
                int x = _firstSelectedElement - 1;
                firstLocal = x > -1 ? x : _firstSelectedElement;
                sublist = _backHistoryList.subList(firstLocal > 0 ? firstLocal : 0, _lastSelectedElement - 1);
            } else {
                firstLocal = _firstSelectedElement;
                sublist = _backHistoryList.subList(firstLocal > 0 ? firstLocal : 0, _lastSelectedElement);
                return sublist;
            }
            if (GUIUtils.nodeListExtent(sublist.iterator(), g) > maxWidth) {
                boolean tooWide = true;
                int counter = 0;
                while (tooWide) {
                    int first = counter;
                    int last = sublist.size();
                    sublist = sublist.subList(first, last);
                    _firstSelectedElement = first;
                    _lastSelectedElement = last;
                    if (GUIUtils.nodeListExtent(sublist.iterator(), g) < maxWidth) {
                        tooWide = false;
                    }
                    counter++;
                }
            }
            localList = sublist;
            _lastSelectedElement -= 1;
        } else if (_shiftCode == SHIFT_RIGHT) {
            if (_doIt) {
                firstLocal = _firstSelectedElement + 1;
            } else {
                firstLocal = _firstSelectedElement;
            }
            _lastSelectedElement = _backHistoryList.size();
            List<Node> sublist = _backHistoryList.subList(firstLocal > 0 ? firstLocal : 0, _lastSelectedElement);
            if (GUIUtils.nodeListExtent(sublist.iterator(), g) > maxWidth) {
                boolean tooWide = true;
                int counter = 0;
                while (tooWide) {
                    int first = counter;
                    int last = sublist.size();
                    sublist = sublist.subList(first, last);
                    _firstSelectedElement = first;
                    _lastSelectedElement = last;
                    if (GUIUtils.nodeListExtent(sublist.iterator(), g) < maxWidth) {
                        tooWide = false;
                    }
                    counter++;
                }
            }
            localList = sublist;
        } else {
            if (GUIUtils.nodeListExtent(backIterator(), g) > maxWidth) {
                List<Node> sublist = null;
                boolean tooWide = true;
                int counter = 0;
                while (tooWide) {
                    int first = counter;
                    int last = _backHistoryList.size();
                    sublist = _backHistoryList.subList(first, last);
                    _firstSelectedElement = first;
                    _lastSelectedElement = last;
                    if (GUIUtils.nodeListExtent(sublist.iterator(), g) < maxWidth) {
                        tooWide = false;
                    }
                    counter++;
                }
                _shiftCode = SHIFT_NONE;
                firstLocal = _firstSelectedElement;
                localList = sublist;
            } else {
                localList = _backHistoryList;
            }
        }
        _doIt = false;
        _firstSelectedElement = firstLocal;
        return localList;
    }

    public void shiftLeft() {
        _shiftCode = SHIFT_LEFT;
        _doIt = true;
    }

    public void shiftRight() {
        if (_lastSelectedElement != _backHistoryList.size()) {
            _shiftCode = SHIFT_RIGHT;
            _doIt = true;
        }
    }

    /**
     * Returns last stored node from list _forthHistoryList. Removes from _forthHistoryList, and adds to _backHistoryList again.
     * 
     * @return
     */
    public Node forthPerformed() {
        if (_forthHistoryList.size() == 0) {
            return null;
        }
        int index = _forthHistoryList.size() - 1;
        Node n = _forthHistoryList.get(index);
        _forthHistoryList.remove(index);
        _backHistoryList.add(n);
        return n;
    }

    public int sizeBack() {
        return _backHistoryList.size();
    }

    public int sizeForth() {
        return _forthHistoryList.size();
    }

    public void addActionRectangle(Rectangle theRectangle, Action theAction) {
        _rectangleActionMap.put(theRectangle, theAction);
    }

    public void clearActionRectangles() {
        _rectangleActionMap.clear();
        _rectangleMouseTrackAdapterMap.clear();
    }

    public void addMouseTrackAdapterRectangle(Rectangle rectangle, MouseTrackAdapter mouseAdapter) {
        _rectangleMouseTrackAdapterMap.put(rectangle, mouseAdapter);
    }

    public Action getActionAtPoint(Point point) {
        for (Iterator<Rectangle> i = _rectangleActionMap.keySet().iterator(); i.hasNext();) {
            Rectangle rectangle = (Rectangle) i.next();
            if (rectangle.contains(point)) {
                return (Action) _rectangleActionMap.get(rectangle);
            }
        }
        return null;
    }

    public MouseTrackAdapter getMouseTrackAdapterAtPoint(Point point) {
        for (Iterator<Rectangle> i = _rectangleMouseTrackAdapterMap.keySet().iterator(); i.hasNext();) {
            Rectangle rectangle = (Rectangle) i.next();
            if (rectangle.contains(point)) {
                return (MouseTrackAdapter) _rectangleMouseTrackAdapterMap.get(rectangle);
            }
        }
        return null;
    }

    public void setRectangle(Rectangle r) {
        _historyArea = r;
    }

    public Rectangle getHistoryArea() {
        return _historyArea;
    }

}
