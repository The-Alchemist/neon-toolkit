/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.jpowergraph.pane;

import java.util.EventListener;

import javax.swing.event.EventListenerList;

import net.sourceforge.jpowergraph.lens.CursorLens;
import net.sourceforge.jpowergraph.lens.LegendLens;
import net.sourceforge.jpowergraph.lens.NodeSizeLens;
import net.sourceforge.jpowergraph.lens.RotateLens;
import net.sourceforge.jpowergraph.lens.TooltipLens;
import net.sourceforge.jpowergraph.lens.TranslateLens;
import net.sourceforge.jpowergraph.lens.ZoomLens;
import net.sourceforge.jpowergraph.pane.JGraphPane;
import net.sourceforge.jpowergraph.pane.JGraphScrollPane;
import net.sourceforge.jpowergraph.viewcontrols.LegendControlPanel;
import net.sourceforge.jpowergraph.viewcontrols.RotateControlPanel;
import net.sourceforge.jpowergraph.viewcontrols.TooltipControlPanel;
import net.sourceforge.jpowergraph.viewcontrols.ZoomControlPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.neontoolkit.jpowergraph.history.NavigationHistory;
import org.neontoolkit.jpowergraph.viewcontrols.INavigationListener;
import org.neontoolkit.jpowergraph.viewcontrols.NavigationHistoryControlPanel;


/*
 * Created by Werner Hihn
 */

public class OntoVisualizerViewPane extends Composite {

    /** The list of listeners. */
    protected EventListenerList _listeners;
    private NavigationHistoryControlPanel _navigationHistoryControlPanel;

    public OntoVisualizerViewPane(Composite theParent, JGraphPane graphPane, TranslateLens translateLens, ZoomLens theZoomLens, RotateLens theRotateLens, CursorLens theDraggingLens, TooltipLens theTooltipLens, LegendLens theLegendLens, NodeSizeLens theNodeSizeLens, NavigationHistory theHistory) {
        super(theParent, SWT.NONE);
        _listeners = new EventListenerList();
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 10;
        gridLayout.marginWidth = 2;
        gridLayout.marginHeight = 4;
        setLayout(gridLayout);

        JGraphScrollPane scroll = new JGraphScrollPane(theParent, graphPane, translateLens);
        scroll.setParent(theParent);

        GridData gridData2 = new GridData();
        gridData2.horizontalAlignment = GridData.FILL;
        gridData2.verticalAlignment = GridData.FILL;
        gridData2.grabExcessHorizontalSpace = true;
        gridData2.grabExcessVerticalSpace = true;
        gridData2.horizontalSpan = gridLayout.numColumns;
        scroll.setLayoutData(gridData2);

        Composite c1 = new ZoomControlPanel(this, theZoomLens);
        Composite c2 = new RotateControlPanel(this, theRotateLens);
        _navigationHistoryControlPanel = new NavigationHistoryControlPanel(this, theHistory);
        new LegendControlPanel(this, theLegendLens);
        new TooltipControlPanel(this, theTooltipLens);
        GridData layoutData = new GridData();
        layoutData.verticalAlignment = GridData.BEGINNING;
        c1.setLayoutData(layoutData);
        
        layoutData = new GridData();
        layoutData.verticalAlignment = GridData.BEGINNING;
        c2.setLayoutData(layoutData);

        _navigationHistoryControlPanel.setLayoutData(layoutData);
    }

    public void addNavigationListener(INavigationListener listener) {
        _listeners.add(INavigationListener.class, listener);
    }

    public void removeNavigationListener(INavigationListener listener) {
        _listeners.remove(INavigationListener.class, listener);
    }

    public void fireNavigationForth() {
        EventListener[] listeners = _listeners.getListeners(INavigationListener.class);
        for (int i = 0; i < listeners.length; i++) {
            ((INavigationListener) listeners[i]).forth();
        }
    }

    public void fireNavigationBack() {
        EventListener[] listeners = _listeners.getListeners(INavigationListener.class);
        for (int i = 0; i < listeners.length; i++) {
            ((INavigationListener) listeners[i]).back();
        }
    }

    public void goTo(String nodeId) {
        EventListener[] listeners = _listeners.getListeners(INavigationListener.class);
        for (int i = 0; i < listeners.length; i++) {
            ((INavigationListener) listeners[i]).goTo(nodeId);
        }
    }

    public void refresh() {
        EventListener[] listeners = _listeners.getListeners(INavigationListener.class);
        for (int i = 0; i < listeners.length; i++) {
            ((INavigationListener) listeners[i]).refresh();
        }
    }

    public NavigationHistoryControlPanel getControlPanel() {
        return _navigationHistoryControlPanel;
    }

    @Override
    public boolean setFocus() {
        return _navigationHistoryControlPanel.setFocus();
    }

}
