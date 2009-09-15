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

import java.awt.event.ActionEvent;
import java.util.Iterator;

import javax.swing.AbstractAction;

import net.sourceforge.jpowergraph.Legend;
import net.sourceforge.jpowergraph.painters.LegendPainter;
import net.sourceforge.jpowergraph.painters.NodePainter;
import net.sourceforge.jpowergraph.pane.JGraphPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.neontoolkit.ontovisualize.Messages;
import org.neontoolkit.ontovisualize.OntovisualizePlugin;


/*
 * Created by Werner Hihn
 */

/**
 * 
 * Paints a legend for the used node types and provides the possibility to filter certain node types.
 * 
 */
public class OntoVisualizerLegendPainter extends LegendPainter {

    private Color _backgroundColor;
    private Color _disabledButtonColor;
    private Color _black;
    private Color _white;
    private Color _enabledButtonColor;
    private String _ontologyLanguage;
    
    private int _maxWidth;

    public OntoVisualizerLegendPainter(Display theDisplay) {
        super(theDisplay);
        _backgroundColor = new Color(theDisplay, 220, 220, 220); // light grey
        _disabledButtonColor = new Color(theDisplay, 220, 220, 220); // light grey
        _black = new Color(theDisplay, 0, 0, 0); // black
        _white = theDisplay.getSystemColor(SWT.COLOR_WHITE);
        _enabledButtonColor = _black;
        _maxWidth = 0;
    }

    public void setOntologyLanguage(String ontologyLanguage) {
        _ontologyLanguage = ontologyLanguage;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Rectangle paintLegend(JGraphPane graphPane, GC g, final Legend theLegend) {
        if (theLegend.getNodePainters().size() == 0) {
            return new Rectangle(0, 0, 0, 0);
        }
        int padding = 2;
        int widthPad = 20;
        int heightPad = 10;
        int width = widthPad;
        int height = heightPad;
        int buttonWidth = 10;
        for (Iterator i = theLegend.getNodePainters().iterator(); i.hasNext();) {
            NodePainter nodePainter = (NodePainter) i.next();
            String label = theLegend.getDescriptionForPainter(nodePainter);
            Point d = nodePainter.getLegendItemSize(graphPane, label);
            width = Math.max(width, d.x + widthPad);
            height += d.y;
        }

        if (theLegend.isNodeFilterInUse()) {
            width += buttonWidth + (padding * 2);
        }

        int x = 10;
        int y = graphPane.getSize().y - (height + 80);

        Color oldColor = g.getBackground();
        g.setBackground(_backgroundColor);
        g.fillRoundRectangle(x, y, width, height, 10, 10);
        g.setBackground(_black);
        g.drawRoundRectangle(x, y, width, height, 10, 10);
        g.setForeground(_black);
        g.setBackground(_white);
        g.drawString(Messages.OntoVisualizerLegendPainter_0, x + 5, y - 16);
        g.setBackground(oldColor);

        int itemX = x + widthPad / 2;
        int itemY = y + heightPad;

        for (Iterator i = theLegend.getNodePainters().iterator(); i.hasNext();) {
            NodePainter nodePainter = (NodePainter) i.next();
            String label = theLegend.getDescriptionForPainter(nodePainter);
            final Class theClass = theLegend.getNodeClassForPainter(nodePainter);
            Point d = nodePainter.getLegendItemSize(graphPane, label);
            nodePainter.paintLegendItem(graphPane, g, new Point(itemX, itemY), label);
            buttonWidth = 10;

            int buttonX = x + (width - (buttonWidth + (padding * 2)) - 5);
            int buttonY = itemY - (padding / 2);
            int buttonHeight = (int) (d.y - (padding * 2.5));

            if (theLegend.isNodeFilterInUse()) {
                boolean canFilterNode = theLegend.getNodeFilterLens().canFilterNode(theClass);
                buttonWidth = Math.max(_maxWidth, buttonHeight);
                _maxWidth = buttonWidth;
                Rectangle r = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
                if (!canFilterNode) {
                    g.setForeground(_disabledButtonColor);
                } else {
                    g.setForeground(_enabledButtonColor);
                }
                g.drawRectangle(r.x, r.y, r.width, r.height);
                if (theLegend.getNodeFilterLens().getFilterValue(theClass)) {
                    g.drawLine(r.x + 3, r.y + 3, r.x + r.width - 3, r.y + r.height - 3);
                    g.drawLine(r.x + 3, r.y + r.height - 3, r.x + r.width - 3, r.y + 3);
                }
                if (canFilterNode) {
                    theLegend.addActionRectangle(r, new AbstractAction() {

                        private static final long serialVersionUID = -5263022424231199799L;

                        public void actionPerformed(ActionEvent e) {
                            boolean status = theLegend.getNodeFilterLens().getFilterValue(theClass);
                            OntovisualizePlugin.getDefault().getVisualizerConfigurator(_ontologyLanguage).setFilterValue(theClass, status, theLegend);
                        }
                    });
                }
            }

            itemY += d.y;
        }
        return new Rectangle(x, y, width, height);
    }
}
