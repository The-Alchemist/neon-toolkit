/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.gui.internal.navigator;

import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Widget;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.navigator.SelectionTransfer;

/* 
 * Created on: 30.03.2005
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Navigator
 */
/**
 * Listener handling drops on the MTreeView
 */
public class MTreeDropListener implements DropTargetListener {

	private int _currentDetail;
    
    public MTreeDropListener(TreeViewer viewer) {
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.dnd.DropTargetListener#dragEnter(org.eclipse.swt.dnd.DropTargetEvent)
     */
    public void dragEnter(DropTargetEvent event) {
    	if (!SelectionTransfer.getInstance().isSupportedType(event.currentDataType)) {
    		Transfer[] transfers = NeOnUIPlugin.getDefault().getTransfers();
        	for (int i = 0; i < transfers.length; i++) {
            	if (transfers[i].isSupportedType(event.currentDataType)) {
            		DropTargetListener handler = NeOnUIPlugin.getDefault().getTransferHandler(transfers[i].getClass());
            		if (handler != null) {
            			handler.dragEnter(event);
            		}
            	}
        	}
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.dnd.DropTargetListener#dragLeave(org.eclipse.swt.dnd.DropTargetEvent)
     */
    public void dragLeave(DropTargetEvent event) {
    	if (!SelectionTransfer.getInstance().isSupportedType(event.currentDataType)) {
    		Transfer[] transfers = NeOnUIPlugin.getDefault().getTransfers();
        	for (int i = 0; i < transfers.length; i++) {
            	if (transfers[i].isSupportedType(event.currentDataType)) {
            		DropTargetListener handler = NeOnUIPlugin.getDefault().getTransferHandler(transfers[i].getClass());
            		if (handler != null) {
            			handler.dragLeave(event);
            		}
            	}
        	}
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.dnd.DropTargetListener#dragOperationChanged(org.eclipse.swt.dnd.DropTargetEvent)
     */
    public void dragOperationChanged(DropTargetEvent event) {
    	if (!SelectionTransfer.getInstance().isSupportedType(event.currentDataType)) {
    		Transfer[] transfers = NeOnUIPlugin.getDefault().getTransfers();
        	for (int i = 0; i < transfers.length; i++) {
            	if (transfers[i].isSupportedType(event.currentDataType)) {
            		DropTargetListener handler = NeOnUIPlugin.getDefault().getTransferHandler(transfers[i].getClass());
            		if (handler != null) {
            			handler.dragOperationChanged(event);
            		}
            	}
        	}
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.dnd.DropTargetListener#dragOver(org.eclipse.swt.dnd.DropTargetEvent)
     */
    public void dragOver(DropTargetEvent event) {
    	if (event.detail == DND.DROP_NONE && _currentDetail != DND.DROP_NONE) {
    		event.detail = _currentDetail;
    	} else {
    		_currentDetail = event.detail;
		}
    	event.feedback = DND.FEEDBACK_SCROLL | DND.FEEDBACK_SELECT | DND.FEEDBACK_EXPAND;
    	
        HashSet<DropTargetListener> dropListeners = new HashSet<DropTargetListener>();        
    	if (SelectionTransfer.getInstance().isSupportedType(event.currentDataType)) {
    	    if(event.item == null) {
    	        return;
    	    }
            Object dropTarget = event.item.getData();
            StructuredSelection sel = (StructuredSelection) SelectionTransfer.getInstance().getSelection();
            if (sel != null && event.item != null) {
                for (Iterator<?> i = sel.iterator(); i.hasNext();) {
                    Object next = i.next();
					DropTargetListener tdh = getDropTargetListener(dropTarget, next);
                    if (tdh != null) {
                        dropListeners.add(tdh);
                    }
                }
            }
        } else {
        	Transfer[] transfers = NeOnUIPlugin.getDefault().getTransfers();
        	for (int i = 0; i < transfers.length; i++) {
            	if (transfers[i].isSupportedType(event.currentDataType)) {
            		DropTargetListener handler = NeOnUIPlugin.getDefault().getTransferHandler(transfers[i].getClass());
            		if (handler != null) {
            			dropListeners.add(handler);
            		}
            	}
        	}
        }
        if (dropListeners.isEmpty()) {
    		//refuse action                	
            event.detail = DND.DROP_NONE;
            
        } else {
            for (Iterator<DropTargetListener> i = dropListeners.iterator(); i.hasNext();) {
                i.next().dragOver(event);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.swt.dnd.DropTargetListener#drop(org.eclipse.swt.dnd.DropTargetEvent)
     */
    public void drop(DropTargetEvent event) {
        HashSet<DropTargetListener> dropListeners = new HashSet<DropTargetListener>();
        Widget eventItem = event.item;
        if (eventItem == null) {
            return;
        }
        Object dropTarget = eventItem.getData();
        if (SelectionTransfer.getInstance().isSupportedType(event.currentDataType)) {
            IStructuredSelection sel = (IStructuredSelection) event.data;
            if (sel != null && event.item != null) {
                for (Iterator<?> i = sel.iterator(); i.hasNext();) {
                    Object next = i.next();
					DropTargetListener tdh = getDropTargetListener(dropTarget, next);
                    if (tdh != null) {
                        dropListeners.add(tdh);
                    }
                }
            }
        } else {
        	Transfer[] transfers = NeOnUIPlugin.getDefault().getTransfers();
        	for (int i = 0; i < transfers.length; i++) {
            	if (transfers[i].isSupportedType(event.currentDataType)) {
            		DropTargetListener handler = NeOnUIPlugin.getDefault().getTransferHandler(transfers[i].getClass());
            		if (handler != null) {
            			dropListeners.add(handler);
            		}
            	}
        	}
        }
        for (Iterator<DropTargetListener> i = dropListeners.iterator(); i.hasNext();) {
            i.next().drop(event);
        }
    }

	private DropTargetListener getDropTargetListener(Object dropTarget, Object dragSource) {
		if (dragSource instanceof Widget) {
			dragSource = ((Widget) dragSource).getData();
		}
		DropTargetListener tdh = NeOnUIPlugin.getDefault().getDropHandler(dragSource, dropTarget);
		return tdh;
	}

    /* (non-Javadoc)
     * @see org.eclipse.swt.dnd.DropTargetListener#dropAccept(org.eclipse.swt.dnd.DropTargetEvent)
     */
    public void dropAccept(DropTargetEvent event) {
    	if (!SelectionTransfer.getInstance().isSupportedType(event.currentDataType)) {
    		Transfer[] transfers = NeOnUIPlugin.getDefault().getTransfers();
        	for (int i = 0; i < transfers.length; i++) {
            	if (transfers[i].isSupportedType(event.currentDataType)) {
            		DropTargetListener handler = NeOnUIPlugin.getDefault().getTransferHandler(transfers[i].getClass());
            		if (handler != null) {
            			handler.dropAccept(event);
            		}
            	}
        	}
        }
    }
}
