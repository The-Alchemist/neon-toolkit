/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.navigator;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;


public class TestTreeViewerListener implements org.neontoolkit.gui.navigator.ITestTreeViewerListener {
    private java.util.List<TreeViewerEvent> events = new ArrayList<TreeViewerEvent>();

    public java.util.List<TreeViewerEvent> getEventList() {
        return events;
    }
    
    @Override
    public void childRemoved(Widget item, Object parent, Object children, boolean itemDisposed) {
        events.add(new ChildRemovedEvent(item, parent, children, itemDisposed));
    }

    @Override
    public void childrenAdded(Widget parentItem, Object parent, Object child, boolean itemCreated) {
        events.add(new ChildAddedEvent(parentItem, parent, child, itemCreated));
    }

    @Override
    public void itemUpdated(Widget item, Object oldElement, Object newElement) {
        events.add(new ItemUpdatedEvent(item, oldElement, newElement));
    }        

    public void attachToNavigator() {
        MTreeView view = (MTreeView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(MTreeView.ID);
        ComplexTreeViewer ctv = (ComplexTreeViewer) view.getTreeViewer();
        ctv.addTestListener(this);

    }
    public void detachFromNavigator() {
        MTreeView view = (MTreeView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(MTreeView.ID);
        ComplexTreeViewer ctv = (ComplexTreeViewer) view.getTreeViewer();
        ctv.removeTestListener(this);
    }
    public ComplexTreeViewer getTreeViewer() {
        MTreeView view = (MTreeView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(MTreeView.ID);
        ComplexTreeViewer ctv = (ComplexTreeViewer) view.getTreeViewer();
        return ctv;
    }
    
    public interface TreeViewerEvent {
        
    }
    public class ChildRemovedEvent implements TreeViewerEvent {
        public Widget item;
        public Object parent;
        public Object child;
        public boolean itemDisposed;
        public ChildRemovedEvent(Widget item, Object parent, Object child, boolean itemDisposed) {
            this.item = item;
            this.parent = parent;
            this.child = child;
            this.itemDisposed = itemDisposed;
        }
    }
    public class ChildAddedEvent implements TreeViewerEvent {
        public Widget item;
        public Object parent;
        public Object child;
        public boolean itemCreated;
        public ChildAddedEvent(Widget item, Object parent, Object child, boolean itemCreated) {
            this.item = item;
            this.parent = parent;
            this.child = child;
            this.itemCreated = itemCreated;
        }
    }
    public class ItemUpdatedEvent implements TreeViewerEvent {
        public Widget item;
        public Object oldElement;
        public Object newElement;
        public ItemUpdatedEvent(Widget item, Object oldElement, Object newElement) {
            this.item = item;
            this.oldElement = oldElement;
            this.newElement = newElement;
        }
    }

}
