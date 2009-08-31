/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.gui.navigator;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

/* 
 * Created on: 29.07.2005
 * Created by: Dirk Wenke
 *
 * Keywords: UI, TreeViewer, Navigator
 */
/**
 * This extension of the TreeViewer supports the multile occurance of items in the tree.  
 */
public class ComplexTreeViewer extends AbstractComplexTreeViewer {
    private ListenerList _listeners = new ListenerList();
    
    /**
     * @param parent
     * @param style
     */
    public ComplexTreeViewer(Composite parent, int style) {
        super(parent, style);
    }

    public void addTestListener(ITestTreeViewerListener listener) {
        _listeners.add(listener);
    }

    public void removeTestListener(ITestTreeViewerListener listener) {
        _listeners.remove(listener);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.AbstractTreeViewer#add(java.lang.Object, java.lang.Object[])
     */
    @Override
	public void add(Object parentElement, Object[] childElements) {
		Assert.isNotNull(parentElement);
		assertElementsNotNull(childElements);
		
		Widget[] widget = findTreeItems(parentElement);
		// If parent hasn't been realized yet, just ignore the add.
		if (widget == null) {
			return;
		}
		Control tree = getControl();

		for (int k = 0; k < widget.length; k++) {
			// optimization!
			// if the widget is not expanded we just invalidate the subtree
			if (widget[k] instanceof Item) {
				Item ti = (Item) widget[k];
				if (!getExpanded(ti)) {
					boolean needDummy = isExpandable(parentElement);
					boolean haveDummy = false;
					// remove all children
					Item[] items = getItems(ti);
					for (int i = 0; i < items.length; i++) {
						if (items[i].getData() != null) {
							disassociate(items[i]);
							items[i].dispose();
						} else {
							if (needDummy && !haveDummy) {
								haveDummy = true;
							} else {
								items[i].dispose();
							}
						}
					}
					// append a dummy if necessary
					if (needDummy && !haveDummy) {
						newItem(ti, SWT.NULL, -1);
					} else {
						// XXX: Workaround (PR missing)
						tree.redraw();
					}
					fireChildrenAdded(widget[k], parentElement, childElements, false);
					continue;
				}
			}

			if (childElements.length > 0) {
				Object[] filtered = filter(childElements);
				for (int i = 0; i < filtered.length; i++) {
					createAddedElement(widget[k], filtered[i]);
				}
                fireChildrenAdded(widget[k], parentElement, childElements, true);
			}
		}
    }
    
	/**
	 * Create the new element in the parent widget. If the
	 * child already exists do nothing.
	 * @param widget
	 * @param element
	 */
	private void createAddedElement(Widget widget, Object element) {
		
		if (equals(element, widget.getData())) {
			return;
		}
		Item[] items = getChildren(widget); 
		for(int i = 0; i < items.length; i++) {
			if(items[i].getData().equals(element)) {
				return;
			}
		}				
		
		int index = indexForElement(widget, element);
		createTreeItem(widget, element, index);
	}
	
	/**
	 * Similiar to findItem(Object), but for multiple occurences of items in the tree
	 * @param element
	 * @return
	 */
	@Override
	public final Widget[] findTreeItems(Object element) {
		Widget result = doFindInputItem(element);
		if (result != null) {
			return new Widget[]{result};
		}
		return doFindItems(element);
	}


	/**
	 * Similiar to doFindItem(Object), but can handle multiple inheritance. Thus multiple widgets are 
	 * returned.
	 */
	protected Widget[] doFindItems(Object element) {
		// compare with root
		Object root = getRoot();
		if (root == null) {
			return null;
		}

		HashSet<Widget> elementSet = new HashSet<Widget>();

		Item[] items = getChildren(getControl());
		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				internalFindItems(items[i], element, elementSet);
			}
			return elementSet.toArray(new Widget[0]);
		}
		return null;
	}
	/**
	 * Recursively tries to find the given element.
	 * 
	 * @param parent the parent item
	 * @param element the element
	 * @return Widget
	 */
	private void internalFindItems(Item parent, Object element, HashSet<Widget> itemSet) {

		// compare with node
		Object data = parent.getData();
		if (data != null) {
			if (equals(data, element)) {
				itemSet.add(parent);
			}
		}
		// recurse over children
		Item[] items = getChildren(parent);
		for (int i = 0; i < items.length; i++) {
			Item item = items[i];
			internalFindItems(item, element, itemSet);
		}
	}
	
	/**
	 * Performs the remove operation of children of nodes.
	 * @param parentElement
	 * @param childElements
	 */
	@Override
	public void remove(final Object parentElement, final Object[] childElements) {
		assertElementsNotNull(childElements);
		preservingSelection(new Runnable() {
			public void run() {
				internalRemove(parentElement, childElements);
			}
		});
	}

	/**
	 * Removes the given elements from the specified parent
	 * @param parent
	 * @param elements
	 */
	@Override
	protected void internalRemove(Object parent, Object[] elements) {
		Object input = getInput();
		// Note: do not use the comparer here since the hashtable
		// contains SWT Items, not model elements.
		Hashtable<Widget, Widget> parentItems = new Hashtable<Widget, Widget>(5);
		for (int i = 0; i < elements.length; ++i) {
			if (equals(elements[i], input)) {
				setInput(null);
				return;
			}
			Widget[] childItem = findTreeItems(elements[i]);
			boolean multiParent = false;
			ArrayList<Widget> items = new ArrayList<Widget>();
			for (int j = 0; j < childItem.length; j++) {
				if (childItem[j] instanceof Item) {
					Item parentItem = getParentItem((Item) childItem[j]);
					if (parentItem != null) {
					    if (parent.equals(parentItem.getData())) {
					        parentItems.put(parentItem, parentItem);
					        items.add(childItem[j]);
					    } else {
					        multiParent = true;
					    }
					} else {
					    // root element is removed
					    disassociate((Item) childItem[i]);
					    childItem[i].dispose();
					    fireChildRemoved(parentItem, parent, elements[i], true);
					}
				}
			}
			for (int j = 0; j < items.size(); j++) {
			    Item child = (Item) items.get(j);
			    Item parentItem = getParentItem(child);
			    Object childData = child.getData();
				if (!multiParent) {
				    disassociate(child);
				}
				child.dispose();
                fireChildRemoved(parentItem, parentItem.getData(), childData, true);
			}
		}
		Control tree = getControl();
		for (Enumeration<Widget> e = parentItems.keys(); e.hasMoreElements();) {
			Item parentItem = (Item) e.nextElement();
			if (!getExpanded(parentItem) && getItemCount(parentItem) == 0) {
				// append a dummy if necessary
				if (isExpandable(parentItem.getData())) {
					newItem(parentItem, SWT.NULL, -1);
				} else {
					// XXX: Workaround (PR missing)
					tree.redraw();
				}
			}
		}
	}
	
	
	/**
	 * Returns true, if a node with the given object exists, false otherwise.
	 * @param data
	 * @return
	 */
	@Override
	public boolean existsNode(Object data) {
	    return findItem(data) != null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.TreeViewer#setExpanded(org.eclipse.swt.widgets.Item, boolean)
	 */
	@Override
	public TreeItem setPathExpanded(ITreeElementPath path) {
		
		if (path == null) {
			return null;
		}
		//compare with root element
		Object root = getRoot();
		if (root == null) {
			return null;
		}
		ITreeElement[] pathElements = path.toArray();
		//get top level elements
		
		Item[] items = getChildren(getControl());
		for (int i = 0; i < pathElements.length; i++) {
			for (int j = 0; j < items.length; j++) {
				if (pathElements[i].equals(items[j].getData())) {
					if (i == pathElements.length - 1) {
						return (TreeItem) items[j];
					} else {
						//item on the path found, expand it
						setExpanded(items[j], true);
						Item[] newItems = getChildren(items[j]);
						if (newItems == null || newItems.length == 0 || newItems[0].getData() == null) {
							//children maybe have to be created
							createChildren(items[j]);
							newItems = getChildren(items[j]);
						}
						items = newItems;
					}
					break;
				}
			}
		}
		return null;
	}
	
	@Override
	public void updateItem(Item treeItem, ITreeElement newData) {
	    Object oldData = treeItem.getData();
		doUpdateItem(treeItem, newData);
		fireItemChanged(treeItem, oldData, newData);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.TreeViewer#setSelection(java.util.List)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void setSelection(List items) {
		Item[] current = getSelection(getTree());
		// Don't bother resetting the same selection
		if (isSameSelection(items, current))
			return;

		TreeItem[] newItems = new TreeItem[items.size()];
		items.toArray(newItems);
		getTree().setSelection(newItems);
	}
	
	/**
	 * updates all labels in the tree
	 */
	@Override
	public void updateLabels() {
		updateLabels(getTree().getItems());
	}

	/**
	 * Updates all occurences of the given element and its subelements. 
	 * @param element
	 */
	@Override
	public void updateLabels(Object element) {
		Widget[] widgets = findTreeItems(element);
		List<TreeItem> items = new ArrayList<TreeItem>();
		for (Widget widget:widgets) {
			if (widget instanceof TreeItem) {
				items.add((TreeItem)widget);
			}
		}
		updateLabels(items.toArray(new TreeItem[0]));
	}
	
	/**
	 * updates the labels of the given items and their subitems.
	 * @param items
	 */
	private void updateLabels(TreeItem[] items) {
		for (TreeItem item:items) {
			if (item.getData() == null) {
				return;
			}
			doUpdateItem(item, item.getData());
			if (item.getItems() != null) {
				updateLabels(item.getItems());
			}
		}
	}
	
	private void fireChildrenAdded(Widget item, Object parent, Object[] children, boolean itemCreated) {
	    for (Object listener: _listeners.getListeners()) {
	        ITestTreeViewerListener l = (ITestTreeViewerListener)listener;
	        for (Object child: children) {
	            l.childrenAdded(item, parent, child, itemCreated);
	        }
	    }
	}

	private void fireChildRemoved(Widget item, Object parent, Object child, boolean itemDisposed) {
        for (Object listener: _listeners.getListeners()) {
            ((ITestTreeViewerListener)listener).childRemoved(item, parent, child, itemDisposed);
        }
    }
	
    private void fireItemChanged(Widget item, Object oldElement, Object newElement) {
        for (Object listener: _listeners.getListeners()) {
            ((ITestTreeViewerListener)listener).itemUpdated(item, oldElement, newElement);
        }
    }
}
