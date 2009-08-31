/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.gui.navigator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

/* 
 * Created on: 14.11.2007
 * Created by: Michael Erdmann
 *
 * Keywords: UI, TreeViewer, Navigator, Interface
 */
/**
 * Interface for TreeViewer extensions that support multile occurances of items in the tree.  
 */
public abstract class AbstractComplexTreeViewer extends TreeViewer {

    /**
     * @param parent
     * @param style
     */
    public AbstractComplexTreeViewer(Composite parent, int style) {
        super(parent, style);
    }

    /**
     * Sets the selection to the given list of items.
     * see org.eclipse.jface.viewers.AbstractTreeViewer.setSelection(List)
     * 
     * @param items
     *           list of items (element type: <code>org.eclipse.swt.widgets.Item</code>)
     */
	public void selection(List<TreeItem> list) {
		super.setSelection(list);
	}

	/**
	 * Returns true, if a node with the given object exists, false otherwise.
	 * @param data
	 * @return
	 */
	public abstract boolean existsNode(Object data);
	
	public abstract TreeItem setPathExpanded(ITreeElementPath path);
	
	public abstract void updateItem(Item treeItem, ITreeElement newData);
	
	/**
	 * updates all labels in the tree
	 */
	public abstract void updateLabels();

	/**
	 * Updates all occurences of the given element and its subelements. 
	 * @param element
	 */
	public abstract void updateLabels(Object element);
	
	/**
	 * Similiar to findItem(Object), but for multiple occurences of items in the tree
	 * @param element
	 * @return
	 */
	public abstract Widget[] findTreeItems(Object element);
	
	/**
     * Replaces all occurences of oldItem by newItem.
     * If one of the occurences is contained in the current selection,
     * the selection will be sent to the selection service again to trigger
     * a refresh of listening components.
     * @param oldItem
     * @param newItem
     */
    public void replaceItem(ITreeElement oldItem, ITreeElement newItem) {
        final Widget[] items = findTreeItems(oldItem);
        if (items != null && items.length > 0) {
            List<?> selection = Arrays.asList(getTree().getSelection());
            boolean isInSelection = false;
            for (int i=0; i<items.length; i++) {
                items[i].setData(newItem);
                updateItem((TreeItem)items[i], newItem);
                isInSelection |= selection.contains(items[i]);
            }
            if (isInSelection) {
                setSelection(new StructuredSelection(getSelectionPath(selection)));
            }
        }
    }
    
    private TreePath[] getSelectionPath(List<?> items) {
        TreePath[] selectionPaths = new TreePath[items.size()];
        for (int i=0; i<items.size(); i++) {
            selectionPaths[i] = getTreePath((TreeItem)items.get(i));
        }
        return selectionPaths; 
    }
    
    private TreePath getTreePath(TreeItem item) {
        TreeItem currentItem = item;
        List<Object> path = new ArrayList<Object>();
        while (currentItem != null) {
            path.add(0, currentItem.getData());
            currentItem = currentItem.getParentItem();
        }
        return new TreePath(path.toArray());
    }
}
