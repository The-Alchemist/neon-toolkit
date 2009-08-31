/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.gui.internal.navigator;

import java.util.Iterator;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.widgets.TreeItem;
import org.neontoolkit.gui.navigator.ITreeElement;
import org.neontoolkit.gui.navigator.SelectionTransfer;
import org.neontoolkit.gui.properties.EntityPropertiesView;

/* 
 * Created on: 04.01.2005
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Navigator, Drag
 */
/**
 * Command for adding a local concept property.
 */
public class MTreeDragListener implements DragSourceListener {

	private TreeViewer _treeViewer;

	public MTreeDragListener(TreeViewer viewer) {
		super();
		_treeViewer = viewer;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragStart(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	public void dragStart(DragSourceEvent event) {
		EntityPropertiesView.setDragging(true);
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
	 *  (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragSetData(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	public void dragSetData(DragSourceEvent event) {
		if (SelectionTransfer.getInstance().isSupportedType(event.dataType)) {
			event.data = SelectionTransfer.getInstance().getSelection();
			return;
		}
		//Do nothing if no item is selected
		event.doit = false;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.swt.dnd.DragSourceListener#dragFinished(org.eclipse.swt.dnd.DragSourceEvent)
	 */
	public void dragFinished(DragSourceEvent event) {
	    if (SelectionTransfer.getInstance().isSupportedType(event.dataType)) {
	        IStructuredSelection sel = (IStructuredSelection) SelectionTransfer.getInstance().getSelection();
	        for (Iterator<?> i = sel.iterator(); i.hasNext();) {
	            _treeViewer.refresh(((TreeItem) i.next()).getData());
	        }
	    }
	}

	/**
	 * Checks if a drag operation is possible for the set of current selected elements. Returns true
	 * if a drag can be performed, false otherwise.
	 * @param treeItems
	 * @return
	 */
	private boolean isDragAllowed(TreeItem[] treeItems) {
	    boolean allowed = true;
	    for (int i = 0; i < treeItems.length; i++) {
	        Object data = treeItems[i].getData();
	        if (data instanceof ITreeElement) {
	            ITreeElement item = (ITreeElement) data;
	            allowed = allowed && item.getProvider().isDragSupported();
	        } else {
	            allowed = false;
	        }
	    }
	    return allowed;
	}

}
