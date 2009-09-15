/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.jpowergraph.painters;

import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.manipulator.dragging.DraggingManipulator;
import net.sourceforge.jpowergraph.manipulator.selection.HighlightingManipulator;
import net.sourceforge.jpowergraph.pane.JGraphPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/*
 * Created by Werner Hihn
 */

/**
 * Draws an edge using a dashed line in the passed color. 
 *  
 */
public class DashedLineEdgePainter extends ExtendedLineEdgePainter {

	public DashedLineEdgePainter(Color blackAndWhite, Color dragging, Color normal) {
		super(blackAndWhite, dragging, normal);
	}

	@Override
	public void paintEdge(JGraphPane graphPane, GC g, Edge edge) {
        HighlightingManipulator highlightingManipulator=(HighlightingManipulator)graphPane.getManipulator(HighlightingManipulator.NAME);
        boolean isHighlighted=highlightingManipulator!=null && highlightingManipulator.getHighlightedEdge()==edge;
        DraggingManipulator draggingManipulator=(DraggingManipulator)graphPane.getManipulator(DraggingManipulator.NAME);
        boolean isDragging=draggingManipulator!=null && draggingManipulator.getDraggedEdge()==edge;
        Point from=graphPane.getScreenPointForNode(edge.getFrom());
        Point to=graphPane.getScreenPointForNode(edge.getTo());
        Color oldFGColor=g.getForeground();
        Color oldBGColor=g.getBackground();
        g.setForeground(getEdgeColor(edge,isHighlighted,isDragging, false));
        g.setBackground(getEdgeColor(edge,isHighlighted,isDragging, false));
        paintArrow(g,from.x,from.y,to.x,to.y, SWT.LINE_DASH);
        g.setForeground(oldFGColor);
        g.setBackground(oldBGColor);	
    }
}
