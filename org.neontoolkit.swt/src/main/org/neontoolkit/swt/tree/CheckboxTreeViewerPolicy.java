/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.swt.tree;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/*
 * Created on 09.10.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
/**
 * This class implements the default behaviour for checkbox trees.
 * If a node is selected, the parent node gets grayed or if all children are selected,
 * the parent node is selected as well.
 * If a node is selected, all child nodes are selected as well.
 * 
 * Just create the CheckboxTreeViewerPolicy with the CheckboxTreeViewer as argument.
 */
public class CheckboxTreeViewerPolicy implements ICheckStateListener, ITreeViewerListener {
	private CheckboxTreeViewer _viewer;

	/**
	 * Default constructor with the CheckboxTreeViewer that should get the default
	 * behaviour as argument.
	 * @param viewer
	 */
	public CheckboxTreeViewerPolicy(CheckboxTreeViewer viewer) {
		viewer.addCheckStateListener(this);
		viewer.addTreeListener(this);
		_viewer = viewer;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ICheckStateListener#checkStateChanged(org.eclipse.jface.viewers.CheckStateChangedEvent)
	 */
	public void checkStateChanged(CheckStateChangedEvent event) {
		TreeItem treeItem = findElement(_viewer.getTree(), event.getElement());
		if (treeItem == null) {
		    return;
		}
		if (treeItem.getChecked()) {
			treeItem.setGrayed(false);
		}
		updateChildrenStates(treeItem);
		updateParentStates(treeItem);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeViewerListener#treeCollapsed(org.eclipse.jface.viewers.TreeExpansionEvent)
	 */
	public void treeCollapsed(TreeExpansionEvent event) {
		//nothing to do
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeViewerListener#treeExpanded(org.eclipse.jface.viewers.TreeExpansionEvent)
	 */
	public void treeExpanded(TreeExpansionEvent event) {
		TreeItem treeItem = findElement(_viewer.getTree(), event.getElement());
		updateChildrenStates(treeItem);
	}

	/**
	 * Updates the children selections depending on the selection status of the
	 * given element. 
	 * @param item
	 */
	private void updateChildrenStates(TreeItem item) {
		for (TreeItem child: item.getItems()) {
			child.setChecked(item.getChecked());
			updateChildrenStates(child);
		}
	}
	
	/**
	 * Updates the selection state of the parent element of the given element
	 * depending on the selection state of the given element.
	 * @param item
	 */
	private void updateParentStates(TreeItem item) {
		TreeItem parent = item.getParentItem();
		if (parent == null) {
			return;
		}
		
		int checkedChildren = getCheckedChildCount(parent);
		if (checkedChildren == 0) {
			_viewer.setChecked(parent.getData(), false);
			_viewer.setGrayChecked(parent.getData(), false);
		}
		else if (checkedChildren == parent.getItemCount()) {
			_viewer.setGrayChecked(parent.getData(), false);
			_viewer.setChecked(parent.getData(), true);
		}
		else {
			_viewer.setGrayChecked(parent.getData(), true);
		}
		updateParentStates(parent);
	}
	
	/**
	 * Returns the number of selected child nodes.
	 * @param parent
	 * @return
	 */
	private int getCheckedChildCount(TreeItem parent) {
		int counter = 0;
		for (TreeItem child: parent.getItems()) {
			if (child.getChecked()) {
				counter++;
			}
		}
		return counter;
	}
	
	/**
	 * Finds the given element in the tree, starting to search from the given node.
	 * The method returns the TreeItem that belongs to the searched element. If the 
	 * element cannot be found, <code>null</code> is returned.
	 * 
	 * @param node a <code>TreeItem</code> indicating the subtree in which the element
	 * is looked up or the <Tree> itself if the whole tree should be searched. 
	 * @param element the element that should be looked up
	 * @return
	 */
	private TreeItem findElement(Object node, Object element) {
		if (node instanceof Tree) {
			for (TreeItem item:((Tree)node).getItems()) {
				TreeItem result = findElement(item, element);
				if (result != null) {
					return result;
				}
			}
		} else if (node instanceof TreeItem) {
			TreeItem iNode = (TreeItem)node;
			if (element.equals(iNode.getData())) {
				return iNode;
			}
			else {
				for (TreeItem item:iNode.getItems()) {
					TreeItem result = findElement(item, element);
					if (result != null) {
						return result;
					}
				}
			}
		}
		return null;
		
	}
}
