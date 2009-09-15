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

import net.sourceforge.jpowergraph.Graph;
import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.pane.JGraphPane;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.neontoolkit.jpowergraph.nodes.DataNode;


/*
 * Created by Dirk Wenke
 */

/**
 * This class extends the JGraphPane to provide selection notification to other
 * components via the ISelectionProvider interface.
 * NOTE: If a selected Node in the Graph is of the type DataNode, the getSelection
 * method will return a selection containing the data object.
 */
public class SelectableGraphPane extends JGraphPane implements ISelectionProvider {
    private ListenerList _selectionListeners = new ListenerList(1);
    private IStructuredSelection _selection;

    /**
     * Constructor from JGraphPane
     */
    public SelectableGraphPane(Composite arg0, Graph arg1) {
        super(arg0, arg1);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                mouseClicked(e);
            }
        });
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
     */
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        _selectionListeners.add(listener);

    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
     */
    public ISelection getSelection() {
        return _selection;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
     */
    public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        _selectionListeners.remove(listener);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
     */
    public void setSelection(ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            Object element = ((IStructuredSelection)selection).getFirstElement();
            if (element instanceof Node) {
                //TODO set the selection
                
            }
        }
    }
    
    private void mouseClicked(MouseEvent e) {
        Node node= getNodeAtPoint(new Point(e.x, e.y));
        if (node != null) {
            if (node instanceof DataNode) {
                _selection = new StructuredSelection(((DataNode)node).getData());
            }
            else {
                _selection = new StructuredSelection(node);
            }
            fireSelectionChanged();
        }
    }
    
    private void fireSelectionChanged() {
        final SelectionChangedEvent event = new SelectionChangedEvent(this, _selection);
        Object[] listeners = _selectionListeners.getListeners();
        for (int i=0; i<listeners.length; i++) {
            final ISelectionChangedListener listener = (ISelectionChangedListener)listeners[i];
            SafeRunnable.run(new SafeRunnable() {
                public void run() {
                    listener.selectionChanged(event);
                }
            });
        }
    }
}
