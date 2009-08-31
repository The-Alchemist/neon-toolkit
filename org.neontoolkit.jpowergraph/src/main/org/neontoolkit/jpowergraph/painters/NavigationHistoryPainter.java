/*****************************************************************************
 * Copyright (c) 2007 ontoprise GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU General Public License (GPL)
 * which accompanies this distribution, and is available at
 * http://www.ontoprise.de/legal/gpl.html
 *****************************************************************************/

package org.neontoolkit.jpowergraph.painters;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.event.EventListenerList;

import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.pane.JGraphPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.neontoolkit.jpowergraph.Messages;
import org.neontoolkit.jpowergraph.history.NavigationHistory;
import org.neontoolkit.jpowergraph.nodes.DefaultImageNode;
import org.neontoolkit.jpowergraph.nodes.OntoPowerGraphDefaultNode;
import org.neontoolkit.jpowergraph.util.GUIUtils;
import org.neontoolkit.jpowergraph.viewcontrols.INavigationListener;
import org.neontoolkit.jpowergraph.viewcontrols.ImageFactory;


/*
 * Created by Werner Hihn
 */

public class NavigationHistoryPainter {

    protected static final double ARROW_BASE_LENGTH = 3.0;

    private Color _white;
    private Color _black;
    private Color _lightGrey;
    private ArrayList<Node> _navigationNodes;
    private EventListenerList _listeners;
    private Image _arrowLeft = ImageFactory.get(ImageFactory.BACK_NAV_DISABLED);
    private Image _arrowRight = ImageFactory.get(ImageFactory.FORWARD_NAV_DISABLED);

    private int _width;
    private int _height;
    private int _maxWidth;
    private int _y_valueForNodes;
    private int _y_valueForRectangle;
    private int _x_start = 10;
    private int _lineHeight = 40;

    public NavigationHistoryPainter(Display theDisplay) {
        _navigationNodes = new ArrayList<Node>();
        _white = theDisplay.getSystemColor(SWT.COLOR_WHITE);
        _black = theDisplay.getSystemColor(SWT.COLOR_BLACK);
        _lightGrey = new Color(theDisplay, 220, 220, 220);
        _listeners = new EventListenerList();
    }

    public Rectangle paintNavigationHistory(JGraphPane graphPane, GC g, final NavigationHistory history) {
        if (history == null) {
            return new Rectangle(0, 0, 0, 0);
        }
        _width = graphPane.getSize().x - 20;
        _height = _lineHeight;
        _maxWidth = graphPane.getSize().x - 100;
        int x = _x_start + 20; // start navigation history here
        _y_valueForNodes = graphPane.getSize().y - (_height + 10);
        _y_valueForRectangle = _y_valueForNodes;

        Color oldColor = g.getBackground();
        g.setBackground(_black);
        int offset = x + 10;
        int rectangleOffset = _x_start + 10;
        int rectangleYValueSubtractor = 10;
        int counter = 0;
        LinkedList<Node> tempList = new LinkedList<Node>();

        // do we have to display just an excerpt?
        if (GUIUtils.nodeListExtent(history.backIterator(), g) > _maxWidth) {
            if (history.getVisibleNodes(_maxWidth, g).get(0).getLabel().equals(history.getOntologyId())) {
                _arrowLeft = ImageFactory.get(ImageFactory.BACK_NAV_DISABLED);
            } else {
                _arrowLeft = ImageFactory.get(ImageFactory.BACK_NAV);
            }
        }

        if (GUIUtils.nodeListExtent(history.backIterator(), g) > _maxWidth) {
            if (history.getVisibleNodes(_maxWidth, g).get(0).getLabel().equals(history.getOntologyId())) {
                _arrowLeft = ImageFactory.get(ImageFactory.BACK_NAV_DISABLED);
            } else {
                _arrowLeft = ImageFactory.get(ImageFactory.BACK_NAV);
            }
        }

        List<Node> visibleNodes = history.getVisibleNodes(_maxWidth, g);
        Node lastNodeLocal = history.getLast();
        Node lastVisibleNode = visibleNodes.size() == 0 ? null : visibleNodes.get(visibleNodes.size() - 1);
        if (lastNodeLocal != null && lastVisibleNode.equals(lastNodeLocal)) {
            _arrowRight = ImageFactory.get(ImageFactory.FORWARD_NAV_DISABLED);
        }

        Rectangle r = drawRectangle(history, g);
        g.drawImage(_arrowLeft, rectangleOffset, _y_valueForNodes + 15);
        g.drawImage(_arrowRight, _width - 10, _y_valueForNodes + 15);

        history.clearActionRectangles();
        if (_arrowLeft.equals(ImageFactory.get(ImageFactory.BACK_NAV))) {
            // action rectangles for arrows
            Rectangle arrowLeftRectangle = new Rectangle(rectangleOffset, _y_valueForNodes + 15, 20, 13);
            history.addActionRectangle(arrowLeftRectangle, new AbstractAction() {
                /**
                 * Comment for <code>serialVersionUID</code>
                 */
                private static final long serialVersionUID = 4498613415687624654L;

                public void actionPerformed(ActionEvent e) {
                    EventListener[] listeners = _listeners.getListeners(INavigationListener.class);
                    for (int i = 0; i < listeners.length; i++) {
                        _arrowRight = ImageFactory.get(ImageFactory.FORWARD_NAV);
                        history.shiftLeft();
                    }
                }
            });
            history.addMouseTrackAdapterRectangle(arrowLeftRectangle, new MouseTrackAdapter() {
            });
        }
        if (_arrowRight.equals(ImageFactory.get(ImageFactory.FORWARD_NAV))) {
            Rectangle arrowRightRectangle = new Rectangle(_width - 10, _y_valueForNodes + 15, 20, 13);
            history.addActionRectangle(arrowRightRectangle, new AbstractAction() {
                /**
                 * Comment for <code>serialVersionUID</code>
                 */
                private static final long serialVersionUID = -5987564908394422893L;

                public void actionPerformed(ActionEvent e) {
                    EventListener[] listeners = _listeners.getListeners(INavigationListener.class);
                    for (int i = 0; i < listeners.length; i++) {
                        _arrowLeft = ImageFactory.get(ImageFactory.BACK_NAV);
                        history.shiftRight();
                    }
                }
            });
            history.addMouseTrackAdapterRectangle(arrowRightRectangle, new MouseTrackAdapter() {
            });
        }

        // do we have to display just an excerpt?
        List<Node> visibleList = history.getVisibleNodes(_maxWidth, g);
        _navigationNodes.clear();
        for (Iterator<Node> iter = visibleList.iterator(); iter.hasNext();) {
            final Node node = (Node) iter.next();
            g.setForeground(_black);
            _navigationNodes.add(node);
            Node lastNode = new OntoPowerGraphDefaultNode();
            if (tempList.size() > 1) {
                lastNode = tempList.getLast();
            }
            if (!lastNode.equals(node)) {
                g.setBackground(_white);
                Image image = ((DefaultImageNode) node).getImage();
                if (counter != 0) {
                    if (offset > (_maxWidth - 10)) {
                        _y_valueForNodes -= _lineHeight;
                        rectangleYValueSubtractor += 10;
                        offset = 180;
                    }
                    g.setBackground(_black);
                    paintArrow(g, offset, _y_valueForNodes + 23, offset + 20, _y_valueForNodes + 23);
                    g.setBackground(_lightGrey);
                    offset += 30; // space between arrow and icon
                    rectangleOffset += 30;
                }
                if (image != null) {
                    final JGraphPane pane = graphPane;
                    g.drawImage(image, offset, _y_valueForNodes + 15);
                    Rectangle nodeRectangle = new Rectangle(offset, _y_valueForNodes + 15, 15 + g.stringExtent(guiString(node)).x, 15);
                    history.addActionRectangle(nodeRectangle, new AbstractAction() {
                        /**
                         * Comment for <code>serialVersionUID</code>
                         */
                        private static final long serialVersionUID = -8909475318938095696L;

                        public void actionPerformed(ActionEvent e) {
                            EventListener[] listeners = _listeners.getListeners(INavigationListener.class);
                            for (int i = 0; i < listeners.length; i++) {
                                if (_y_valueForNodes <= pane.getSize().y) {
                                    pane.setCursor(new Cursor(pane.getDisplay(), SWT.CURSOR_WAIT));
                                    ((INavigationListener) listeners[i]).goTo(node);
                                    pane.setCursor(new Cursor(pane.getDisplay(), SWT.CURSOR_ARROW));
                                }
                            }
                        }
                    });
                    history.addMouseTrackAdapterRectangle(nodeRectangle, new MouseTrackAdapter() {
                    });
                }
                offset += 10; // icon size
                rectangleOffset += 10;
                offset += 10; // space between icon and text
                rectangleOffset += 10;
                g.setBackground(_lightGrey);
                g.drawString(guiString(node), offset, _y_valueForNodes + 14);
                offset += g.stringExtent(guiString(node)).x; // text
                rectangleOffset += g.stringExtent(guiString(node)).x;
                offset += 5; // space between text & arrow
                rectangleOffset += 5;
                offset += 10; // space between entries
                rectangleOffset += 10;
                counter++;
                tempList.add(node);
            }
        }
        _width = rectangleOffset + 10 - 160;

        g.setBackground(oldColor);
        return r;
    }

    public void addNavigationListener(INavigationListener listener) {
        _listeners.add(INavigationListener.class, listener);
    }

    protected String guiString(Node node) {
        return node.getLabel();
    }

    private Rectangle drawRectangle(NavigationHistory theHistory, GC g) {
        Rectangle r = null;
        int itemWidth = 180 + 10;
        int counter = 0;
        // root node
        if (theHistory != null) {
            for (Iterator<?> iter = theHistory.backIterator(); iter.hasNext();) {
                Node node = (Node) iter.next();
                itemWidth += 10; // icon size
                itemWidth += 10; // space between icon and text
                itemWidth += 15;
                itemWidth += g.stringExtent(guiString(node)).x; // text
                counter++;
            }
            itemWidth += (counter - 1) * 50;
        }
        g.setForeground(_black);
        g.setBackground(_white);
        g.drawString(Messages.NavigationHistoryPainter_0, _x_start + 5, _y_valueForRectangle - 16);
        r = new Rectangle(_x_start, _y_valueForRectangle, _width, _lineHeight);
        g.setBackground(_lightGrey);
        g.fillRoundRectangle(_x_start, _y_valueForRectangle, _width, _lineHeight, 10, 10);
        g.setBackground(_black);
        g.drawRoundRectangle(_x_start, _y_valueForRectangle, _width, _lineHeight, 10, 10);
        return r;
    }

    /**
     * Paints the arrow.
     * 
     * @param g the graphics
     * @param x1 the source x coordinate
     * @param y1 the source y coordinate
     * @param x2 the target x coordinate
     * @param y2 the target y coordinate
     */
    private void paintArrow(GC g, int x1, int y1, int x2, int y2) {
        double dx;
        double dy;
        double deltaX = x1 - x2;
        double deltaY = y1 - y2;
        if (Math.abs(deltaY) > Math.abs(deltaX)) {
            double slope = Math.abs(deltaX / deltaY);
            dx = ARROW_BASE_LENGTH / Math.sqrt(1 + slope * slope);
            dy = dx * slope;
        } else {
            double slope = Math.abs(deltaY / deltaX);
            dy = ARROW_BASE_LENGTH / Math.sqrt(1 + slope * slope);
            dx = dy * slope;
        }
        if (deltaY > 0)
            dx *= -1;
        if (deltaX < 0)
            dy *= -1;

        int polyX1 = x2;
        int polyY1 = y2;
        int polyX2 = (int) (x1 - dx);
        int polyY2 = (int) (y1 - dy);
        int polyX3 = (int) (x1 + dx);
        int polyY3 = (int) (y1 + dy);

        g.drawPolygon(new int[] {polyX1, polyY1, polyX2, polyY2, polyX3, polyY3});
        int center = (polyY2 + polyY3) / 2;
        g.drawLine(polyX2, center, polyX2 - 10, center);
    }

}
