/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.jpowergraph.manipulator.contextandtooltip;

import net.sourceforge.jpowergraph.DefaultGraph;
import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.layout.Layouter;
import net.sourceforge.jpowergraph.lens.LegendLens;
import net.sourceforge.jpowergraph.lens.RotateLens;
import net.sourceforge.jpowergraph.lens.ZoomLens;
import net.sourceforge.jpowergraph.manipulator.contextandtooltip.DefaultContextMenuListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.neontoolkit.jpowergraph.Messages;


/*
 * Created by Werner Hihn
 */

public class OntoPowerGraphContextMenuListener extends DefaultContextMenuListener {

    private Layouter _layouter;

    public OntoPowerGraphContextMenuListener(DefaultGraph theGraph, LegendLens theLegendLens, ZoomLens theZoomLens, Integer[] theZoomLevels, RotateLens theRotateLens, Integer[] theRotateAngles) {
        super(theGraph, theLegendLens, theZoomLens, theZoomLevels, theRotateLens, theRotateAngles);
    }

    public OntoPowerGraphContextMenuListener(Layouter theLayouter, DefaultGraph theGraph, LegendLens theLegendLens, ZoomLens theZoomLens, Integer[] theZoomLevels, RotateLens theRotateLens, Integer[] theRotateAngles) {
        this(theGraph, theLegendLens, theZoomLens, theZoomLevels, theRotateLens, theRotateAngles);
        _layouter = theLayouter;
    }

    @Override
    public void fillBackgroundContextMenu(Menu theMenu) {
        super.fillBackgroundContextMenu(theMenu);
        MenuItem startLayouter = new MenuItem(theMenu, SWT.CASCADE);
        startLayouter.setText(Messages.OntoPowerGraphContextMenuListener_0);
        startLayouter.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                _layouter.start();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
                _layouter.start();
            }
        });
        startLayouter.setEnabled(true);

        MenuItem stopLayouter = new MenuItem(theMenu, SWT.CASCADE);
        stopLayouter.setText(Messages.OntoPowerGraphContextMenuListener_1);
        stopLayouter.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                _layouter.stop();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
                _layouter.stop();
            }
        });
        stopLayouter.setEnabled(true);
    }

    @Override
    public void fillEdgeContextMenu(Edge theEdge, Menu theMenu) {
        // nothing to do, we don't need no context menu for nodes & edges
    }

    @Override
    public void fillNodeContextMenu(Node theNode, Menu theMenu) {
        // nothing to do, we don't need no context menu for nodes & edges
    }

}
