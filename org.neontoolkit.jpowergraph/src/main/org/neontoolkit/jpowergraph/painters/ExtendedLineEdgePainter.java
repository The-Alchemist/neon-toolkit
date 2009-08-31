/*****************************************************************************
 * Copyright (c) 2007 ontoprise GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU General Public License (GPL)
 * which accompanies this distribution, and is available at
 * http://www.ontoprise.de/legal/gpl.html
 *****************************************************************************/

package org.neontoolkit.jpowergraph.painters;

import net.sourceforge.jpowergraph.painters.LineEdgePainter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;

/*
 * Created by Werner Hihn
 */

/**
 * Draws an arrow in the middle of the edge.
 *  
 */
public class ExtendedLineEdgePainter extends LineEdgePainter {

    public ExtendedLineEdgePainter(Composite theParent) {
        super(theParent);
    }

    public ExtendedLineEdgePainter(Color blackAndWhite, Color dragging, Color normal) {
        super(blackAndWhite, dragging, normal);
    }

    /**
     * Draws an arrow at the middle of the edge.
     *
     * @param g                     the graphics to draw on
     * @param x1                    the source x coordinate
     * @param y1                    the source y coordinate
     * @param x2                    the target x coordinate
     * @param y2                    the target y coordinate
     */
    public static void paintArrow(GC g,int x1,int y1,int x2,int y2, int style) {
        int oldStyle = g.getLineStyle();
        if (style == SWT.LINE_DASH) {
            g.setLineStyle(SWT.LINE_DASHDOTDOT);
            g.drawLine(x1,y1,x2,y2);
            g.setLineStyle(oldStyle);
        } else {
            g.drawLine(x1,y1,x2,y2);
        }
    }}
