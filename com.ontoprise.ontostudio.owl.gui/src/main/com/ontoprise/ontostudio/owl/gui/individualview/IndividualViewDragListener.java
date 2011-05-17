/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.individualview;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TreeItem;
import org.neontoolkit.gui.navigator.SelectionTransfer;
import org.neontoolkit.gui.navigator.elements.IEntityElement;
import org.neontoolkit.gui.navigator.elements.IIndividualTreeElement;

/* 
 * Created on: 03.03.2006
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Instance, View, Drag
 */
/**
 * Drag listener of the InstanceView.
 */
public class IndividualViewDragListener implements DragSourceListener {
    private TreeViewer _treeViewer;

    public IndividualViewDragListener(TreeViewer viewer) {
        super();
        _treeViewer = viewer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.dnd.DragSourceListener#dragStart(org.eclipse.swt.dnd.DragSourceEvent)
     */
    public void dragStart(DragSourceEvent event) {
        ISelection selection = _treeViewer.getSelection();
        if (selection == null || selection.isEmpty()) {
            event.doit = false;
            return;
        } else {
            TreeItem[] items = _treeViewer.getTree().getSelection();
            if (!isDragAllowed(items)) {
                event.doit = false;
                return;
            }
            selection = new StructuredSelection(items);
        }
        SelectionTransfer.getInstance().setSelection(selection);
        event.doit = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.dnd.DragSourceListener#dragSetData(org.eclipse.swt.dnd.DragSourceEvent)
     */
    public void dragSetData(DragSourceEvent event) {
        if (SelectionTransfer.getInstance().isSupportedType(event.dataType)) {
            event.data = SelectionTransfer.getInstance().getSelection();
            return;
        }
        // TODO should be provided as plug-in (same for MTreeDragListner)
        else if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
            ISelection selection = SelectionTransfer.getInstance().getSelection();
            if (selection.isEmpty()) {
                event.doit = false;
                return;
            }
            if (selection instanceof StructuredSelection) {
                StructuredSelection ssel = (StructuredSelection) selection;
                List<?> items = ssel.toList();
                for (Object item: items) {
                    if (item instanceof Item) {
                        Object data = ((Item) item).getData();
                        if (data instanceof IEntityElement) {
                            IEntityElement element = (IEntityElement) data;
//                            String uri = StringUtil.unQuote(element.getNamespace()) + StringUtil.unSingleQuote(element.getLocalName());
                            String uri = element.getId();
                            event.data = uri;
                            return;
                        }
                    }
                }
            }
        }
        // Do nothing if no item is selected
        event.doit = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.dnd.DragSourceListener#dragFinished(org.eclipse.swt.dnd.DragSourceEvent)
     */
    @SuppressWarnings("unchecked")
    public void dragFinished(DragSourceEvent event) {
        if (SelectionTransfer.getInstance().isSupportedType(event.dataType)) {
            IStructuredSelection sel = (IStructuredSelection) SelectionTransfer.getInstance().getSelection();
            for (Iterator i = sel.iterator(); i.hasNext();) {
                _treeViewer.refresh(((TreeItem) i.next()).getData());
            }
        }
    }

    /**
     * Checks if a drag operation is possible for the set of current selected elements. Returns true if a drag can be performed, false otherwise.
     * 
     * @param treeItems
     * @return
     */
    private boolean isDragAllowed(TreeItem[] treeItems) {
        boolean allowed = true;
        for (int i = 0; i < treeItems.length; i++) {
            Object data = treeItems[i].getData();
            if (data instanceof IIndividualTreeElement) {
                allowed = true;
            } else {
                allowed = false;
            }
        }
        return allowed;
    }

}
